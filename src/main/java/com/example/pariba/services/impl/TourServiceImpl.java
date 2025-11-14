package com.example.pariba.services.impl;

import com.example.pariba.constants.AppConstants;
import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.GenerateToursRequest;
import com.example.pariba.dtos.responses.TourResponse;
import com.example.pariba.enums.ContributionStatus;
import com.example.pariba.enums.Frequency;
import com.example.pariba.enums.TourStatus;
import com.example.pariba.exceptions.BadRequestException;
import com.example.pariba.exceptions.ResourceNotFoundException;
import com.example.pariba.models.*;
import com.example.pariba.repositories.*;
import com.example.pariba.services.IAuditService;
import com.example.pariba.services.ITontineGroupService;
import com.example.pariba.services.ITourService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TourServiceImpl implements ITourService {

    private final TourRepository tourRepository;
    private final TontineGroupRepository groupRepository;
    private final GroupMembershipRepository membershipRepository;
    private final ContributionRepository contributionRepository;
    private final ITontineGroupService groupService;
    private final IAuditService auditService;

    public TourServiceImpl(TourRepository tourRepository,
                          TontineGroupRepository groupRepository,
                          GroupMembershipRepository membershipRepository,
                          ContributionRepository contributionRepository,
                          ITontineGroupService groupService,
                          IAuditService auditService) {
        this.tourRepository = tourRepository;
        this.groupRepository = groupRepository;
        this.membershipRepository = membershipRepository;
        this.contributionRepository = contributionRepository;
        this.groupService = groupService;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public List<TourResponse> generateTours(String personId, GenerateToursRequest request) {
        TontineGroup group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("TontineGroup", "id", request.getGroupId()));

        // Vérifier que la personne est admin
        groupService.checkIsAdmin(request.getGroupId(), personId);

        // Vérifier qu'il n'y a pas déjà des tours
        if (!group.getTours().isEmpty()) {
            throw new BadRequestException(MessageConstants.TOUR_ERROR_ALREADY_GENERATED);
        }

        // Récupérer les membres actifs
        List<GroupMembership> members = membershipRepository.findByGroupId(request.getGroupId());
        if (members.isEmpty()) {
            throw new BadRequestException(MessageConstants.TOUR_ERROR_NO_MEMBERS);
        }

        // Déterminer l'ordre des bénéficiaires selon le mode de rotation
        List<Person> beneficiaryOrder = determineBeneficiaryOrder(group, members, request);

        // Générer les tours
        List<Tour> tours = new ArrayList<>();
        LocalDate currentDate = group.getStartDate();

        for (int i = 0; i < group.getTotalTours(); i++) {
            Tour tour = new Tour();
            tour.setGroup(group);
            tour.setIndexInGroup(i + 1);
            tour.setBeneficiary(beneficiaryOrder.get(i % beneficiaryOrder.size()));
            tour.setStartDate(currentDate);
            tour.setEndDate(calculateEndDate(currentDate, group.getFrequency()));
            tour.setStatus(i == 0 ? TourStatus.IN_PROGRESS : TourStatus.PENDING);
            tour.setExpectedAmount(group.getMontant().multiply(java.math.BigDecimal.valueOf(members.size())));
            tours.add(tour);

            currentDate = tour.getEndDate().plusDays(1);
        }

        tours = tourRepository.saveAll(tours);

        // Créer les contributions pour le premier tour
        createContributionsForTour(tours.get(0), members, group);

        // Audit log
        auditService.log(personId, AppConstants.AUDIT_GENERATE_TOURS, "TontineGroup", group.getId(), null);

        return tours.stream().map(TourResponse::new).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TourResponse getTourById(String tourId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Tour", "id", tourId));
        return new TourResponse(tour);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TourResponse> getToursByGroup(String groupId) {
        return tourRepository.findByGroupIdOrderByIndexInGroupAsc(groupId)
                .stream()
                .map(TourResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TourResponse getCurrentTour(String groupId) {
        Tour tour = tourRepository.findByGroupIdAndStatus(groupId, TourStatus.IN_PROGRESS)
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.TOUR_ERROR_NO_CURRENT));
        return new TourResponse(tour);
    }

    @Override
    @Transactional(readOnly = true)
    public TourResponse getNextTour(String groupId) {
        List<Tour> tours = tourRepository.findByGroupIdOrderByIndexInGroupAsc(groupId);
        Tour currentTour = tours.stream()
                .filter(t -> t.getStatus() == TourStatus.IN_PROGRESS)
                .findFirst()
                .orElse(null);

        if (currentTour == null) {
            throw new ResourceNotFoundException(MessageConstants.TOUR_ERROR_NO_CURRENT);
        }

        return tours.stream()
                .filter(t -> t.getIndexInGroup() == currentTour.getIndexInGroup() + 1)
                .findFirst()
                .map(TourResponse::new)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.TOUR_ERROR_NO_NEXT));
    }

    @Override
    @Transactional
    public void startTour(String tourId, String personId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Tour", "id", tourId));

        groupService.checkIsAdmin(tour.getGroup().getId(), personId);

        if (tour.getStatus() != TourStatus.PENDING) {
            throw new BadRequestException(MessageConstants.TOUR_ERROR_ALREADY_STARTED);
        }

        tour.setStatus(TourStatus.IN_PROGRESS);
        tourRepository.save(tour);

        // Créer les contributions
        List<GroupMembership> members = membershipRepository.findByGroupId(tour.getGroup().getId());
        createContributionsForTour(tour, members, tour.getGroup());

        auditService.log(personId, "START_TOUR", "Tour", tourId, null);
    }

    @Override
    @Transactional
    public void completeTour(String tourId, String personId) {
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new ResourceNotFoundException("Tour", "id", tourId));

        groupService.checkIsAdmin(tour.getGroup().getId(), personId);

        if (tour.getStatus() != TourStatus.IN_PROGRESS) {
            throw new BadRequestException(MessageConstants.TOUR_ERROR_NOT_IN_PROGRESS);
        }

        tour.setStatus(TourStatus.COMPLETED);
        tourRepository.save(tour);

        auditService.log(personId, "COMPLETE_TOUR", "Tour", tourId, null);
    }

    private List<Person> determineBeneficiaryOrder(TontineGroup group, List<GroupMembership> members, GenerateToursRequest request) {
        List<Person> persons = members.stream().map(GroupMembership::getPerson).collect(Collectors.toList());

        switch (group.getRotationMode()) {
            case RANDOM:
                Collections.shuffle(persons);
                break;
            case SHUFFLE:
                if (request.getShuffle() != null && request.getShuffle()) {
                    Collections.shuffle(persons);
                }
                break;
            case CUSTOM:
                if (request.getCustomBeneficiaryOrder() != null && !request.getCustomBeneficiaryOrder().isEmpty()) {
                    // Réorganiser selon l'ordre personnalisé
                    List<Person> customOrder = new ArrayList<>();
                    for (String personId : request.getCustomBeneficiaryOrder()) {
                        persons.stream()
                                .filter(p -> p.getId().equals(personId))
                                .findFirst()
                                .ifPresent(customOrder::add);
                    }
                    return customOrder;
                }
                break;
            case SEQUENTIAL:
            default:
                // Ordre par défaut (ordre d'ajout)
                break;
        }

        return persons;
    }

    private LocalDate calculateEndDate(LocalDate startDate, Frequency frequency) {
        switch (frequency) {
            case WEEKLY:
                return startDate.plusWeeks(1).minusDays(1);
            case BIWEEKLY:
                return startDate.plusWeeks(2).minusDays(1);
            case MONTHLY:
                return startDate.plusMonths(1).minusDays(1);
            default:
                return startDate.plusMonths(1).minusDays(1);
        }
    }

    private void createContributionsForTour(Tour tour, List<GroupMembership> members, TontineGroup group) {
        List<Contribution> contributions = new ArrayList<>();

        for (GroupMembership membership : members) {
            Contribution contribution = new Contribution();
            contribution.setTour(tour);
            contribution.setMember(membership.getPerson());
            contribution.setAmountDue(group.getMontant());
            contribution.setStatus(ContributionStatus.PENDING);
            contribution.setDueDate(tour.getEndDate());
            contribution.setPenaltyApplied(java.math.BigDecimal.ZERO);
            contributions.add(contribution);
        }

        contributionRepository.saveAll(contributions);
    }
}
