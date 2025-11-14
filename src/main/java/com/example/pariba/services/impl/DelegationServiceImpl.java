package com.example.pariba.services.impl;

import com.example.pariba.constants.AppConstants;
import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.CreateDelegationRequest;
import com.example.pariba.dtos.responses.DelegationResponse;
import com.example.pariba.exceptions.BadRequestException;
import com.example.pariba.exceptions.ResourceNotFoundException;
import com.example.pariba.models.Delegation;
import com.example.pariba.models.Person;
import com.example.pariba.models.TontineGroup;
import com.example.pariba.repositories.DelegationRepository;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.repositories.TontineGroupRepository;
import com.example.pariba.services.IAuditService;
import com.example.pariba.services.IDelegationService;
import com.example.pariba.services.ITontineGroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DelegationServiceImpl implements IDelegationService {

    private final DelegationRepository delegationRepository;
    private final TontineGroupRepository groupRepository;
    private final PersonRepository personRepository;
    private final ITontineGroupService groupService;
    private final IAuditService auditService;

    public DelegationServiceImpl(DelegationRepository delegationRepository,
                                TontineGroupRepository groupRepository,
                                PersonRepository personRepository,
                                ITontineGroupService groupService,
                                IAuditService auditService) {
        this.delegationRepository = delegationRepository;
        this.groupRepository = groupRepository;
        this.personRepository = personRepository;
        this.groupService = groupService;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public DelegationResponse createDelegation(String personId, CreateDelegationRequest request) {
        TontineGroup group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("TontineGroup", "id", request.getGroupId()));

        Person delegator = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));

        Person proxy = personRepository.findById(request.getProxyPersonId())
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", request.getProxyPersonId()));

        // Vérifier que les deux sont membres du groupe
        if (!groupService.isMember(group.getId(), personId) || 
            !groupService.isMember(group.getId(), request.getProxyPersonId())) {
            throw new BadRequestException(MessageConstants.DELEGATION_ERROR_NOT_MEMBERS);
        }

        // Vérifier les dates
        if (request.getValidTo().isBefore(request.getValidFrom())) {
            throw new BadRequestException(MessageConstants.DELEGATION_ERROR_INVALID_DATES);
        }

        // Créer la délégation
        Delegation delegation = new Delegation();
        delegation.setGroup(group);
        delegation.setGrantor(delegator);
        delegation.setProxy(proxy);
        delegation.setValidFrom(request.getValidFrom());
        delegation.setValidTo(request.getValidTo());
        delegation.setActive(true);

        delegation = delegationRepository.save(delegation);

        // Audit log
        auditService.log(personId, AppConstants.AUDIT_CREATE_DELEGATION, "Delegation", delegation.getId(), null);

        return new DelegationResponse(delegation);
    }

    @Override
    public DelegationResponse getDelegationById(String delegationId) {
        Delegation delegation = delegationRepository.findById(delegationId)
                .orElseThrow(() -> new ResourceNotFoundException("Delegation", "id", delegationId));
        return new DelegationResponse(delegation);
    }

    @Override
    public List<DelegationResponse> getDelegationsByGroup(String groupId) {
        return delegationRepository.findByGroupId(groupId)
                .stream()
                .map(DelegationResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<DelegationResponse> getActiveDelegations(String groupId) {
        LocalDate today = LocalDate.now();
        return delegationRepository.findActiveDelegations(groupId, today)
                .stream()
                .map(DelegationResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void revokeDelegation(String delegationId, String personId) {
        Delegation delegation = delegationRepository.findById(delegationId)
                .orElseThrow(() -> new ResourceNotFoundException("Delegation", "id", delegationId));

        // Vérifier que c'est le délégateur qui révoque
        if (!delegation.getGrantor().getId().equals(personId)) {
            throw new BadRequestException(MessageConstants.DELEGATION_ERROR_NOT_DELEGATOR);
        }

        delegation.setActive(false);
        delegationRepository.save(delegation);

        auditService.log(personId, "REVOKE_DELEGATION", "Delegation", delegationId, null);
    }

    @Override
    @Transactional
    public void expireOldDelegations() {
        LocalDate today = LocalDate.now();
        List<Delegation> expiredDelegations = delegationRepository.findExpiredDelegations(today);

        for (Delegation delegation : expiredDelegations) {
            delegation.setActive(false);
            delegationRepository.save(delegation);
        }
    }
}
