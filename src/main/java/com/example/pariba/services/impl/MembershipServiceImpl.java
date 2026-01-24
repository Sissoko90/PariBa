package com.example.pariba.services.impl;

import com.example.pariba.constants.AppConstants;
import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.UpdateMemberRoleRequest;
import com.example.pariba.dtos.responses.MembershipResponse;
import com.example.pariba.enums.NotificationChannel;
import com.example.pariba.enums.NotificationType;
import com.example.pariba.exceptions.BadRequestException;
import com.example.pariba.exceptions.ResourceNotFoundException;
import com.example.pariba.models.GroupMembership;
import com.example.pariba.repositories.GroupMembershipRepository;
import com.example.pariba.services.IAuditService;
import com.example.pariba.services.IMembershipService;
import com.example.pariba.services.INotificationService;
import com.example.pariba.services.ITontineGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MembershipServiceImpl implements IMembershipService {

    private final GroupMembershipRepository membershipRepository;
    private final ITontineGroupService groupService;
    private final IAuditService auditService;
    private final INotificationService notificationService;

    public MembershipServiceImpl(GroupMembershipRepository membershipRepository,
                                ITontineGroupService groupService,
                                IAuditService auditService,
                                INotificationService notificationService) {
        this.membershipRepository = membershipRepository;
        this.groupService = groupService;
        this.auditService = auditService;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public List<MembershipResponse> getMembersByGroup(String groupId) {
        return membershipRepository.findByGroupId(groupId)
                .stream()
                .map(MembershipResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MembershipResponse getMemberByGroupAndPerson(String groupId, String personId) {
        GroupMembership membership = membershipRepository.findByGroupIdAndPersonId(groupId, personId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.MEMBER_ERROR_NOT_FOUND));
        return new MembershipResponse(membership);
    }

    @Transactional(readOnly = true)
    public List<MembershipResponse> getGroupsByPerson(String personId) {
        return membershipRepository.findByPersonId(personId)
                .stream()
                .map(MembershipResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public MembershipResponse updateMemberRole(String requesterId, UpdateMemberRoleRequest request) {
        // Vérifier que le requester est admin
        groupService.checkIsAdmin(request.getGroupId(), requesterId);

        GroupMembership membership = membershipRepository.findByGroupIdAndPersonId(
                request.getGroupId(), request.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.MEMBER_ERROR_NOT_FOUND));

        // Ne pas permettre de modifier le rôle du créateur
        if (membership.getGroup().getCreator().getId().equals(request.getPersonId())) {
            throw new BadRequestException(MessageConstants.MEMBER_ERROR_CANNOT_REMOVE_CREATOR);
        }

        membership.setRole(request.getNewRole());
        membership = membershipRepository.save(membership);

        // Audit log
        auditService.log(requesterId, "UPDATE_MEMBER_ROLE", "GroupMembership", 
            request.getGroupId() + ":" + request.getPersonId(), null);

        return new MembershipResponse(membership);
    }

    @Transactional
    public MembershipResponse promoteMemberToAdmin(String groupId, String personId, String requesterId) {
        // Vérifier que le requester est admin
        groupService.checkIsAdmin(groupId, requesterId);

        GroupMembership membership = membershipRepository.findByGroupIdAndPersonId(groupId, personId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.MEMBER_ERROR_NOT_FOUND));

        // Ne pas permettre de promouvoir le créateur (déjà admin)
        if (membership.getGroup().getCreator().getId().equals(personId)) {
            throw new BadRequestException("Le créateur du groupe est déjà administrateur");
        }

        // Vérifier que le membre n'est pas déjà admin
        if (membership.getRole() == com.example.pariba.enums.GroupRole.ADMIN) {
            throw new BadRequestException("Ce membre est déjà administrateur");
        }

        membership.setRole(com.example.pariba.enums.GroupRole.ADMIN);
        membership = membershipRepository.save(membership);

        // Audit log
        auditService.log(requesterId, "PROMOTE_MEMBER", "GroupMembership", 
            groupId + ":" + personId, null);

        return new MembershipResponse(membership);
    }

    @Transactional
    public MembershipResponse demoteAdminToMember(String groupId, String personId, String requesterId) {
        // Vérifier que le requester est admin
        groupService.checkIsAdmin(groupId, requesterId);

        GroupMembership membership = membershipRepository.findByGroupIdAndPersonId(groupId, personId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.MEMBER_ERROR_NOT_FOUND));

        // Ne pas permettre de rétrograder le créateur
        if (membership.getGroup().getCreator().getId().equals(personId)) {
            throw new BadRequestException(MessageConstants.MEMBER_ERROR_CANNOT_REMOVE_CREATOR);
        }

        // Vérifier que le membre est bien admin
        if (membership.getRole() != com.example.pariba.enums.GroupRole.ADMIN) {
            throw new BadRequestException("Ce membre n'est pas administrateur");
        }

        membership.setRole(com.example.pariba.enums.GroupRole.MEMBER);
        membership = membershipRepository.save(membership);

        // Audit log
        auditService.log(requesterId, "DEMOTE_ADMIN", "GroupMembership", 
            groupId + ":" + personId, null);

        return new MembershipResponse(membership);
    }

    @Transactional
    public void removeMember(String groupId, String personId, String requesterId) {
        // Vérifier que le requester est admin
        groupService.checkIsAdmin(groupId, requesterId);

        GroupMembership membership = membershipRepository.findByGroupIdAndPersonId(groupId, personId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.MEMBER_ERROR_NOT_FOUND));

        // Ne pas permettre de retirer le créateur
        if (membership.getGroup().getCreator().getId().equals(personId)) {
            throw new BadRequestException(MessageConstants.MEMBER_ERROR_CANNOT_REMOVE_CREATOR);
        }

        String groupName = membership.getGroup().getNom();
        String removedPersonId = membership.getPerson().getId();
        
        membershipRepository.delete(membership);

        // Audit log
        auditService.log(requesterId, AppConstants.AUDIT_REMOVE_MEMBER, "TontineGroup", groupId, null);
        
        // Envoyer notification de retrait
        try {
            Map<String, String> variables = new HashMap<>();
            variables.put("groupe", groupName);
            
            notificationService.sendNotificationWithTemplate(
                removedPersonId,
                NotificationType.MEMBER_REMOVED,
                NotificationChannel.PUSH,
                variables
            );
            
            log.info("✅ Notification retrait membre envoyée");
        } catch (Exception e) {
            log.error("❌ Erreur notification retrait: {}", e.getMessage());
        }
    }

    public long countMembersByGroup(String groupId) {
        return membershipRepository.countByGroupId(groupId);
    }
}
