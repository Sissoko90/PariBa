package com.example.pariba.services.impl;

import com.example.pariba.constants.AppConstants;
import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.InviteMemberRequest;
import com.example.pariba.dtos.responses.InvitationResponse;
import com.example.pariba.enums.GroupRole;
import com.example.pariba.enums.InvitationStatus;
import com.example.pariba.enums.NotificationChannel;
import com.example.pariba.enums.NotificationType;
import com.example.pariba.exceptions.AlreadyExistsException;
import com.example.pariba.exceptions.BadRequestException;
import com.example.pariba.exceptions.ResourceNotFoundException;
import com.example.pariba.models.*;
import com.example.pariba.repositories.GroupMembershipRepository;
import com.example.pariba.repositories.InvitationRepository;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.repositories.TontineGroupRepository;
import com.example.pariba.services.IAuditService;
import com.example.pariba.services.IInvitationService;
import com.example.pariba.services.INotificationService;
import com.example.pariba.services.ITontineGroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InvitationServiceImpl implements IInvitationService {

    private final InvitationRepository invitationRepository;
    private final TontineGroupRepository groupRepository;
    private final PersonRepository personRepository;
    private final GroupMembershipRepository membershipRepository;
    private final ITontineGroupService groupService;
    private final IAuditService auditService;
    private final INotificationService notificationService;

    public InvitationServiceImpl(InvitationRepository invitationRepository,
                                TontineGroupRepository groupRepository,
                                PersonRepository personRepository,
                                GroupMembershipRepository membershipRepository,
                                ITontineGroupService groupService,
                                IAuditService auditService,
                                INotificationService notificationService) {
        this.invitationRepository = invitationRepository;
        this.groupRepository = groupRepository;
        this.personRepository = personRepository;
        this.membershipRepository = membershipRepository;
        this.groupService = groupService;
        this.auditService = auditService;
        this.notificationService = notificationService;
    }

    @Transactional
    public InvitationResponse inviteMember(String inviterId, InviteMemberRequest request) {
        TontineGroup group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("TontineGroup", "id", request.getGroupId()));

        // Vérifier que l'inviteur est admin
        groupService.checkIsAdmin(request.getGroupId(), inviterId);
        
        // Vérifier qu'on ne s'invite pas soi-même
        if (request.getTargetPhone() != null) {
            Person inviter = personRepository.findById(inviterId)
                    .orElseThrow(() -> new ResourceNotFoundException("Person", "id", inviterId));
            if (inviter.getPhone().equals(request.getTargetPhone())) {
                throw new BadRequestException("Vous ne pouvez pas vous inviter vous-même");
            }
        }

        // Créer l'invitation avec un lien unique
        Invitation invitation = new Invitation();
        invitation.setGroup(group);
        invitation.setTargetPhone(request.getTargetPhone());
        invitation.setTargetEmail(request.getTargetEmail());
        
        // Générer un code unique pour le lien (8 caractères alphanumériques)
        String linkCode = generateUniqueLinkCode();
        invitation.setLinkCode(linkCode);
        invitation.setStatus(InvitationStatus.PENDING);
        
        // Expiration 24h (au lieu de 7 jours)
        invitation.setExpiresAt(Instant.now().plusSeconds(24 * 60 * 60)); // 24 heures
        invitation = invitationRepository.save(invitation);

        // Envoyer les notifications d'invitation
        sendInvitationNotifications(invitation, request);

        return new InvitationResponse(invitation);
    }
    
    /**
     * Envoie les notifications d'invitation par les canaux spécifiés
     */
    private void sendInvitationNotifications(Invitation invitation, InviteMemberRequest request) {
        // Si aucun destinataire spécifié, ne rien envoyer
        if (request.getTargetPhone() == null && request.getTargetEmail() == null) {
            return;
        }
        
        // Préparer les variables pour le template
        Map<String, String> variables = prepareInvitationVariables(invitation);
        
        // Chercher si l'utilisateur existe déjà
        Person targetPerson = null;
        if (request.getTargetPhone() != null) {
            targetPerson = personRepository.findByPhone(request.getTargetPhone()).orElse(null);
        } else if (request.getTargetEmail() != null) {
            targetPerson = personRepository.findByEmail(request.getTargetEmail()).orElse(null);
        }
        
        // Si l'utilisateur existe, envoyer les notifications
        if (targetPerson != null) {
            // Envoyer par SMS si numéro fourni
            if (request.getTargetPhone() != null) {
                notificationService.sendNotificationWithTemplate(
                    targetPerson.getId(),
                    NotificationType.GROUP_INVITATION_RECEIVED,
                    NotificationChannel.SMS,
                    variables
                );
            }
            
            // Envoyer par Email si email fourni
            if (request.getTargetEmail() != null) {
                notificationService.sendNotificationWithTemplate(
                    targetPerson.getId(),
                    NotificationType.GROUP_INVITATION_RECEIVED,
                    NotificationChannel.EMAIL,
                    variables
                );
            }
            
            // Envoyer notification Push
            notificationService.sendNotificationWithTemplate(
                targetPerson.getId(),
                NotificationType.GROUP_INVITATION_RECEIVED,
                NotificationChannel.PUSH,
                variables
            );
        }
        // Sinon, l'utilisateur devra télécharger l'app via le lien
    }
    
    /**
     * Prépare les variables pour le template d'invitation
     */
    private Map<String, String> prepareInvitationVariables(Invitation invitation) {
        TontineGroup group = invitation.getGroup();
        Map<String, String> variables = new HashMap<>();
        
        // Compter le nombre de membres
        long memberCount = membershipRepository.countByGroupId(group.getId());
        
        // Formater la fréquence de manière lisible
        String frequence = switch (group.getFrequency()) {
            case HEBDOMADAIRE -> "Hebdomadaire";
            case BIHEBDOMADAIRE -> "Bi-hebdomadaire";
            case MENSUEL -> "Mensuelle";
        };
        
        variables.put("prenom", ""); // Sera rempli par le service de notification
        variables.put("groupe", group.getNom());
        // Formater le montant avec séparateurs de milliers
        variables.put("montant", String.format("%,.0f", group.getMontant()));
        variables.put("frequence", frequence);
        variables.put("membres", String.valueOf(memberCount));
        
        // Ajouter le code d'invitation
        variables.put("code", invitation.getLinkCode());
        
        // Générer le lien d'invitation
        String baseUrl = "https://pariba.app"; // TODO: Récupérer depuis configuration
        String invitationLink = baseUrl + "/join/" + invitation.getLinkCode();
        variables.put("lien", invitationLink);
        
        return variables;
    }
    
    /**
     * Génère un code unique de 8 caractères pour le lien d'invitation
     */
    private String generateUniqueLinkCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        java.util.Random random = new java.util.Random();
        
        // Générer jusqu'à trouver un code unique
        do {
            code.setLength(0);
            for (int i = 0; i < 8; i++) {
                code.append(characters.charAt(random.nextInt(characters.length())));
            }
        } while (invitationRepository.findByLinkCode(code.toString()).isPresent());
        
        return code.toString();
    }

    @Transactional
    public void acceptInvitation(String personId, String linkCode) {
        Invitation invitation = invitationRepository.findByLinkCode(linkCode)
                .orElseThrow(() -> new ResourceNotFoundException(MessageConstants.INVITATION_ERROR_NOT_FOUND));

        // Vérifier l'expiration d'abord
        if (invitation.getExpiresAt().isBefore(Instant.now())) {
            invitation.setStatus(InvitationStatus.EXPIRED);
            invitationRepository.save(invitation);
            throw new BadRequestException(MessageConstants.INVITATION_ERROR_EXPIRED);
        }
        
        // Vérifier que le lien est toujours valide (PENDING ou ACCEPTED car réutilisable)
        if (invitation.getStatus() == InvitationStatus.EXPIRED || invitation.getStatus() == InvitationStatus.DECLINED) {
            throw new BadRequestException(MessageConstants.INVITATION_ERROR_INVALID_CODE);
        }

        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", personId));
        
        TontineGroup group = invitation.getGroup();
        
        // Vérifier qu'on ne s'ajoute pas soi-même si on est le créateur
        if (group.getCreator().getId().equals(personId)) {
            throw new BadRequestException("Le créateur du groupe est déjà membre");
        }

        // Vérifier si déjà membre
        if (membershipRepository.existsByGroupIdAndPersonId(group.getId(), personId)) {
            throw new AlreadyExistsException(MessageConstants.INVITATION_ERROR_ALREADY_MEMBER);
        }

        // Créer le membership
        GroupMembership membership = new GroupMembership();
        GroupMembershipId membershipId = new GroupMembershipId(group.getId(), personId);
        membership.setId(membershipId);
        membership.setGroup(group);
        membership.setPerson(person);
        membership.setRole(GroupRole.MEMBER);
        membershipRepository.save(membership);

        // Marquer l'invitation comme acceptée (mais le lien reste réutilisable)
        if (invitation.getStatus() == InvitationStatus.PENDING) {
            invitation.setStatus(InvitationStatus.ACCEPTED);
            invitationRepository.save(invitation);
        }

        // Audit log
        auditService.log(personId, AppConstants.AUDIT_ADD_MEMBER, "TontineGroup", group.getId(), null);
    }

    @Transactional(readOnly = true)
    public List<InvitationResponse> getInvitationsByGroup(String groupId) {
        return invitationRepository.findByGroupId(groupId)
                .stream()
                .map(InvitationResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cleanupExpiredInvitations() {
        invitationRepository.deleteByExpiresAtBeforeAndStatus(Instant.now(), InvitationStatus.PENDING);
    }
}
