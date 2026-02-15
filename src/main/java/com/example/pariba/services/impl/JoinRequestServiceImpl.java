package com.example.pariba.services.impl;

import com.example.pariba.constants.AppConstants;
import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.CreateJoinRequestRequest;
import com.example.pariba.dtos.requests.ReviewJoinRequestRequest;
import com.example.pariba.dtos.responses.JoinRequestResponse;
import com.example.pariba.enums.GroupRole;
import com.example.pariba.enums.JoinRequestStatus;
import com.example.pariba.enums.NotificationChannel;
import com.example.pariba.enums.NotificationType;
import com.example.pariba.exceptions.BadRequestException;
import com.example.pariba.exceptions.ForbiddenException;
import com.example.pariba.exceptions.ResourceNotFoundException;
import com.example.pariba.models.GroupMembership;
import com.example.pariba.models.JoinRequest;
import com.example.pariba.models.Person;
import com.example.pariba.models.TontineGroup;
import com.example.pariba.repositories.GroupMembershipRepository;
import com.example.pariba.repositories.JoinRequestRepository;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.repositories.TontineGroupRepository;
import com.example.pariba.services.IAuditService;
import com.example.pariba.services.IJoinRequestService;
import com.example.pariba.services.INotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JoinRequestServiceImpl implements IJoinRequestService {

    private final JoinRequestRepository joinRequestRepository;
    private final TontineGroupRepository groupRepository;
    private final PersonRepository personRepository;
    private final GroupMembershipRepository membershipRepository;
    private final INotificationService notificationService;
    private final IAuditService auditService;

    public JoinRequestServiceImpl(
            JoinRequestRepository joinRequestRepository,
            TontineGroupRepository groupRepository,
            PersonRepository personRepository,
            GroupMembershipRepository membershipRepository,
            INotificationService notificationService,
            IAuditService auditService) {
        this.joinRequestRepository = joinRequestRepository;
        this.groupRepository = groupRepository;
        this.personRepository = personRepository;
        this.membershipRepository = membershipRepository;
        this.notificationService = notificationService;
        this.auditService = auditService;
    }

    @Transactional
    public JoinRequestResponse createJoinRequest(String personId, CreateJoinRequestRequest request) {
        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));

        TontineGroup group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("TontineGroup", "id", request.getGroupId()));

        // Vérifier si la personne est déjà membre
        if (membershipRepository.findByGroupIdAndPersonId(group.getId(), personId).isPresent()) {
            throw new BadRequestException("Vous êtes déjà membre de ce groupe");
        }

        // Vérifier si une demande existe déjà
        joinRequestRepository.findByGroupIdAndPersonId(group.getId(), personId).ifPresent(existing -> {
            if (existing.getStatus() == JoinRequestStatus.PENDING) {
                throw new BadRequestException("Vous avez déjà une demande en attente pour ce groupe");
            }
        });

        // Créer la demande
        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setGroup(group);
        joinRequest.setPerson(person);
        joinRequest.setMessage(request.getMessage());
        joinRequest.setStatus(JoinRequestStatus.PENDING);

        joinRequest = joinRequestRepository.save(joinRequest);

        // Notifier les admins du groupe
        notifyAdmins(group, person, "NEW_JOIN_REQUEST");

        // Audit log
        auditService.log(personId, "JOIN_REQUEST_CREATED", "JoinRequest", joinRequest.getId(), null);

        return new JoinRequestResponse(joinRequest);
    }

    @Transactional
    public JoinRequestResponse reviewJoinRequest(String requestId, String adminId, ReviewJoinRequestRequest request) {
        JoinRequest joinRequest = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("JoinRequest", "id", requestId));

        // Vérifier que l'utilisateur est admin du groupe
        checkIsAdmin(joinRequest.getGroup().getId(), adminId);

        if (joinRequest.getStatus() != JoinRequestStatus.PENDING) {
            throw new BadRequestException("Cette demande a déjà été traitée");
        }

        Person admin = personRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", adminId));

        String action = request.getAction().toUpperCase();

        if ("APPROVE".equals(action)) {
            // Approuver la demande
            joinRequest.setStatus(JoinRequestStatus.APPROVED);
            joinRequest.setReviewedBy(admin);
            joinRequest.setReviewedAt(Instant.now());
            joinRequest.setReviewNote(request.getNote());

            // Ajouter la personne au groupe
            GroupMembership membership = new GroupMembership();
            membership.setGroup(joinRequest.getGroup());
            membership.setPerson(joinRequest.getPerson());
            membership.setRole(GroupRole.MEMBER);
            membershipRepository.save(membership);

            // Notifier la personne
            Map<String, String> variables = new HashMap<>();
            variables.put("groupName", joinRequest.getGroup().getNom());
            variables.put("adminName", admin.getPrenom() + " " + admin.getNom());
            if (request.getNote() != null) {
                variables.put("note", request.getNote());
            }

            notificationService.sendNotificationWithTemplate(
                    joinRequest.getPerson().getId(),
                    NotificationType.JOIN_REQUEST_APPROVED,
                    NotificationChannel.PUSH,
                    variables
            );

            // Audit log
            auditService.log(adminId, "JOIN_REQUEST_APPROVED", "JoinRequest", requestId, null);

        } else if ("REJECT".equals(action)) {
            // Rejeter la demande
            joinRequest.setStatus(JoinRequestStatus.REJECTED);
            joinRequest.setReviewedBy(admin);
            joinRequest.setReviewedAt(Instant.now());
            joinRequest.setReviewNote(request.getNote());

            // Notifier la personne
            Map<String, String> variables = new HashMap<>();
            variables.put("groupName", joinRequest.getGroup().getNom());
            if (request.getNote() != null) {
                variables.put("reason", request.getNote());
            }

            notificationService.sendNotificationWithTemplate(
                    joinRequest.getPerson().getId(),
                    NotificationType.JOIN_REQUEST_REJECTED,
                    NotificationChannel.PUSH,
                    variables
            );

            // Audit log
            auditService.log(adminId, "JOIN_REQUEST_REJECTED", "JoinRequest", requestId, null);

        } else {
            throw new BadRequestException("Action invalide. Utilisez APPROVE ou REJECT");
        }

        joinRequest = joinRequestRepository.save(joinRequest);
        return new JoinRequestResponse(joinRequest);
    }

    @Transactional
    public void cancelJoinRequest(String requestId, String personId) {
        JoinRequest joinRequest = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("JoinRequest", "id", requestId));

        // Vérifier que c'est bien la personne qui a fait la demande
        if (!joinRequest.getPerson().getId().equals(personId)) {
            throw new ForbiddenException("Vous ne pouvez annuler que vos propres demandes");
        }

        if (joinRequest.getStatus() != JoinRequestStatus.PENDING) {
            throw new BadRequestException("Cette demande ne peut plus être annulée");
        }

        joinRequest.setStatus(JoinRequestStatus.CANCELLED);
        joinRequestRepository.save(joinRequest);

        // Audit log
        auditService.log(personId, "JOIN_REQUEST_CANCELLED", "JoinRequest", requestId, null);
    }

    @Transactional(readOnly = true)
    public List<JoinRequestResponse> getGroupJoinRequests(String groupId, String adminId) {
        // Vérifier que l'utilisateur est admin du groupe
        checkIsAdmin(groupId, adminId);

        List<JoinRequest> requests = joinRequestRepository.findByGroupId(groupId);
        return requests.stream()
                .map(JoinRequestResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<JoinRequestResponse> getMyJoinRequests(String personId) {
        List<JoinRequest> requests = joinRequestRepository.findByPersonId(personId);
        return requests.stream()
                .map(JoinRequestResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countPendingJoinRequests(String groupId, String adminId) {
        // Vérifier que l'utilisateur est admin du groupe
        checkIsAdmin(groupId, adminId);

        return joinRequestRepository.countPendingByGroupId(groupId);
    }

    private void checkIsAdmin(String groupId, String personId) {
        GroupMembership membership = membershipRepository.findByGroupIdAndPersonId(groupId, personId)
                .orElseThrow(() -> new ForbiddenException(MessageConstants.ERROR_FORBIDDEN));

        if (membership.getRole() != GroupRole.ADMIN) {
            throw new ForbiddenException(MessageConstants.GROUP_ERROR_NOT_ADMIN);
        }
    }

    private void notifyAdmins(TontineGroup group, Person requester, String notificationType) {
        // Récupérer tous les admins du groupe
        List<GroupMembership> admins = membershipRepository.findByGroupId(group.getId())
                .stream()
                .filter(m -> m.getRole() == GroupRole.ADMIN)
                .collect(Collectors.toList());

        Map<String, String> variables = new HashMap<>();
        variables.put("groupName", group.getNom());
        variables.put("requesterName", requester.getPrenom() + " " + requester.getNom());
        variables.put("requesterPhone", requester.getPhone());

        for (GroupMembership admin : admins) {
            notificationService.sendNotificationWithTemplate(
                    admin.getPerson().getId(),
                    NotificationType.NEW_JOIN_REQUEST,
                    NotificationChannel.PUSH,
                    variables
            );
        }
    }
}
