package com.example.pariba.services.impl;

import com.example.pariba.constants.AppConstants;
import com.example.pariba.constants.MessageConstants;
import com.example.pariba.dtos.requests.CreateGroupRequest;
import com.example.pariba.dtos.requests.UpdateGroupRequest;
import com.example.pariba.dtos.responses.GroupResponse;
import com.example.pariba.dtos.responses.GroupShareLinkResponse;
import com.example.pariba.enums.GroupRole;
import com.example.pariba.enums.NotificationChannel;
import com.example.pariba.enums.NotificationType;
import com.example.pariba.exceptions.BadRequestException;
import com.example.pariba.exceptions.ForbiddenException;
import com.example.pariba.exceptions.ResourceNotFoundException;
import com.example.pariba.models.GroupMembership;
import com.example.pariba.models.GroupMembershipId;
import com.example.pariba.models.Person;
import com.example.pariba.models.TontineGroup;
import com.example.pariba.repositories.GroupMembershipRepository;
import com.example.pariba.repositories.PersonRepository;
import com.example.pariba.repositories.TontineGroupRepository;
import com.example.pariba.services.IAuditService;
import com.example.pariba.services.INotificationService;
import com.example.pariba.services.ISubscriptionService;
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
public class TontineGroupServiceImpl implements ITontineGroupService {

    private final TontineGroupRepository groupRepository;
    private final PersonRepository personRepository;
    private final GroupMembershipRepository membershipRepository;
    private final IAuditService auditService;
    private final INotificationService notificationService;
    private final ISubscriptionService subscriptionService;

    public TontineGroupServiceImpl(TontineGroupRepository groupRepository,
                                  PersonRepository personRepository,
                                  GroupMembershipRepository membershipRepository,
                                  IAuditService auditService,
                                  INotificationService notificationService,
                                  ISubscriptionService subscriptionService) {
        this.groupRepository = groupRepository;
        this.personRepository = personRepository;
        this.membershipRepository = membershipRepository;
        this.auditService = auditService;
        this.notificationService = notificationService;
        this.subscriptionService = subscriptionService;
    }

    @Transactional
    public GroupResponse createGroup(String creatorId, CreateGroupRequest request) {
        Person creator = personRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Person", "id", creatorId));

        // Vérifier la limite de tontines pour les utilisateurs gratuits
        checkGroupCreationLimit(creatorId);

        // Créer le groupe
        TontineGroup group = new TontineGroup();
        group.setNom(request.getNom());
        group.setDescription(request.getDescription());
        group.setMontant(request.getMontant());
        group.setFrequency(request.getFrequency());
        group.setRotationMode(request.getRotationMode());
        group.setTotalTours(request.getTotalTours());
        group.setStartDate(request.getStartDate());
        group.setLatePenaltyAmount(request.getLatePenaltyAmount());
        group.setGraceDays(request.getGraceDays() != null ? request.getGraceDays() : 0);
        group.setCreator(creator);
        group = groupRepository.save(group);

        // Ajouter le créateur comme ADMIN
        GroupMembership membership = new GroupMembership();
        GroupMembershipId membershipId = new GroupMembershipId(group.getId(), creator.getId());
        membership.setId(membershipId);
        membership.setGroup(group);
        membership.setPerson(creator);
        membership.setRole(GroupRole.ADMIN);
        membershipRepository.save(membership);

        // Audit log
        auditService.log(creatorId, AppConstants.AUDIT_CREATE_GROUP, "TontineGroup", group.getId(), null);

        // Envoyer notification de création de groupe
        try {
            String frequence = switch (group.getFrequency()) {
                case DAILY -> "Quotidienne";
                case WEEKLY, HEBDOMADAIRE -> "Hebdomadaire";
                case BIWEEKLY, BIHEBDOMADAIRE -> "Bi-hebdomadaire";
                case MONTHLY, MENSUEL -> "Mensuelle";
                case QUARTERLY -> "Trimestrielle";
                case YEARLY -> "Annuelle";
            };
            
            Map<String, String> variables = new HashMap<>();
            variables.put("groupe", group.getNom());
            variables.put("montant", String.format("%,.0f", group.getMontant()));
            variables.put("frequence", frequence);
            variables.put("tours", String.valueOf(group.getTotalTours()));
            
            notificationService.sendNotificationWithTemplate(
                creatorId,
                NotificationType.GROUP_CREATED,
                NotificationChannel.PUSH,
                variables
            );
            
            // Envoyer aussi par Email
            notificationService.sendNotificationWithTemplate(
                creatorId,
                NotificationType.GROUP_CREATED,
                NotificationChannel.EMAIL,
                variables
            );
            
            log.info("✅ Notifications création groupe envoyées à {}", creator.getEmail());
        } catch (Exception e) {
            log.error("❌ Erreur notification création groupe: {}", e.getMessage());
        }

        return new GroupResponse(group);
    }

    @Transactional
    public GroupResponse updateGroup(String groupId, String personId, UpdateGroupRequest request) {
        TontineGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("TontineGroup", "id", groupId));

        // Vérifier que la personne est admin du groupe
        GroupMembership membership = checkIsAdmin(groupId, personId);

        if (request.getNom() != null) {
            group.setNom(request.getNom());
        }
        if (request.getDescription() != null) {
            group.setDescription(request.getDescription());
        }
        if (request.getMontant() != null) {
            group.setMontant(request.getMontant());
        }
        if (request.getFrequency() != null) {
            group.setFrequency(request.getFrequency());
        }
        if (request.getRotationMode() != null) {
            group.setRotationMode(request.getRotationMode());
        }
        if (request.getLatePenaltyAmount() != null) {
            group.setLatePenaltyAmount(request.getLatePenaltyAmount());
        }
        if (request.getGraceDays() != null) {
            group.setGraceDays(request.getGraceDays());
        }

        group = groupRepository.save(group);

        // Audit log
        auditService.log(personId, AppConstants.AUDIT_UPDATE_GROUP, "TontineGroup", group.getId(), null);

        // Créer la réponse avec le rôle de l'utilisateur
        GroupResponse response = new GroupResponse(group);
        response.setCurrentUserRole(membership.getRole());
        return response;
    }

    @Transactional(readOnly = true)
    public GroupResponse getGroupById(String groupId, String personId) {
        TontineGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("TontineGroup", "id", groupId));
        
        // Récupérer le rôle de l'utilisateur dans ce groupe
        GroupResponse response = new GroupResponse(group);
        membershipRepository.findByGroupIdAndPersonId(groupId, personId)
                .ifPresent(membership -> response.setCurrentUserRole(membership.getRole()));
        
        return response;
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> getGroupsByPerson(String personId) {
        List<TontineGroup> groups = groupRepository.findAllGroupsForPerson(personId);
        
        // Charger tous les memberships de l'utilisateur en une seule requête (évite N+1)
        List<GroupMembership> memberships = membershipRepository.findByPersonId(personId);
        Map<String, GroupRole> rolesByGroupId = memberships.stream()
                .collect(Collectors.toMap(
                    m -> m.getGroup().getId(),
                    GroupMembership::getRole
                ));
        
        return groups.stream()
                .map(group -> {
                    GroupResponse response = new GroupResponse(group);
                    // Récupérer le rôle depuis la map (pas de requête DB)
                    GroupRole role = rolesByGroupId.get(group.getId());
                    if (role != null) {
                        response.setCurrentUserRole(role);
                    }
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> getGroupsCreatedByPerson(String personId) {
        return groupRepository.findByCreatorId(personId)
                .stream()
                .map(GroupResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteGroup(String groupId, String personId) {
        TontineGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("TontineGroup", "id", groupId));

        // Seul le créateur/ADMIN peut supprimer le groupe
        if (!group.getCreator().getId().equals(personId)) {
            throw new ForbiddenException(MessageConstants.GROUP_ERROR_NOT_ADMIN);
        }

        // Récupérer tous les membres avant suppression
        List<GroupMembership> members = membershipRepository.findByGroupId(groupId);
        int memberCount = members.size();

        // Supprimer tous les memberships (déconnecter tous les membres)
        membershipRepository.deleteAll(members);
        log.info("🔌 {} membres déconnectés du groupe {}", memberCount, groupId);

        // Audit log
        auditService.log(personId, AppConstants.AUDIT_DELETE_GROUP, "TontineGroup", group.getId(), 
                "Groupe supprimé avec " + memberCount + " membres");

        // Supprimer le groupe (cascade supprimera les tours, contributions, etc.)
        groupRepository.delete(group);
        
        log.info("🗑️ Groupe {} supprimé définitivement par {}", groupId, personId);
    }

    @Override
    @Transactional
    public void leaveGroup(String groupId, String personId) {
        TontineGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("TontineGroup", "id", groupId));

        // Vérifier que la personne est membre
        GroupMembership membership = membershipRepository.findByGroupIdAndPersonId(groupId, personId)
                .orElseThrow(() -> new BadRequestException("Vous n'êtes pas membre de ce groupe"));

        // Compter le nombre total de membres
        long memberCount = membershipRepository.countByGroupId(groupId);

        // Si c'est l'ADMIN/créateur
        boolean isCreator = group.getCreator().getId().equals(personId);
        boolean isAdmin = membership.getRole() == GroupRole.ADMIN;
        
        if (isCreator || isAdmin) {
            // L'ADMIN ne peut quitter que s'il y a d'autres membres
            if (memberCount <= 1) {
                throw new BadRequestException("Vous êtes le seul membre du groupe. Vous devez supprimer le groupe au lieu de le quitter.");
            }
            
            // Transférer l'administration à un autre membre avant de quitter
            GroupMembership newAdmin = membershipRepository.findByGroupIdAndPersonIdNot(groupId, personId)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("Impossible de trouver un nouveau administrateur"));
            
            newAdmin.setRole(GroupRole.ADMIN);
            membershipRepository.save(newAdmin);
            
            log.info("🔄 Administration transférée de {} à {} pour le groupe {}", 
                    personId, newAdmin.getPerson().getId(), groupId);
        }

        // Supprimer le membership
        membershipRepository.delete(membership);
        
        // Audit log
        auditService.log(personId, AppConstants.AUDIT_LEAVE_GROUP, "TontineGroup", groupId, null);
        
        log.info("✅ {} a quitté le groupe {} ({} membres restants)", personId, groupId, memberCount - 1);
    }

    public GroupMembership checkIsAdmin(String groupId, String personId) {
        GroupMembership membership = membershipRepository.findByGroupIdAndPersonId(groupId, personId)
                .orElseThrow(() -> new ForbiddenException(MessageConstants.ERROR_FORBIDDEN));

        if (membership.getRole() != GroupRole.ADMIN) {
            throw new ForbiddenException(MessageConstants.GROUP_ERROR_NOT_ADMIN);
        }
        
        return membership;
    }

    public boolean isMember(String groupId, String personId) {
        return membershipRepository.existsByGroupIdAndPersonId(groupId, personId);
    }

    @Transactional(readOnly = true)
    public GroupShareLinkResponse generateShareLink(String groupId, String personId) {
        TontineGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("TontineGroup", "id", groupId));

        // Vérifier que la personne est membre du groupe
        if (!isMember(groupId, personId)) {
            throw new ForbiddenException("Vous devez être membre du groupe pour partager");
        }

        // Générer le lien de partage (deep link)
        // Format: pariba://join-group/{groupId}
        String deepLink = "pariba://join-group/" + groupId;
        
        // Générer le texte de partage
        String shareText = String.format(
            "🎉 Rejoignez mon groupe '%s' sur PariBa !\n\n" +
            "👉 Lien d'invitation : %s\n\n" +
            "💡 Si vous n'avez pas l'app, téléchargez-la ici :\n" +
            "https://play.google.com/store/apps/details?id=com.example.pariba",
            group.getNom(),
            deepLink
        );

        return new GroupShareLinkResponse(groupId, group.getNom(), deepLink, shareText);
    }
    
    /**
     * Vérifie si l'utilisateur peut créer un nouveau groupe selon son plan d'abonnement
     * La limite est définie dans le plan (maxGroups), 0 = illimité
     */
    private void checkGroupCreationLimit(String personId) {
        int maxGroups = subscriptionService.getMaxGroupsForPerson(personId);
        
        // 0 = illimité
        if (maxGroups == 0) {
            log.info("✅ Utilisateur {} a un plan avec tontines illimitées", personId);
            return;
        }
        
        // Compter le nombre de groupes créés par cet utilisateur
        long groupCount = groupRepository.countByCreatorId(personId);
        
        if (groupCount >= maxGroups) {
            log.warn("⚠️ Utilisateur {} a atteint la limite de {} tontines", personId, maxGroups);
            throw new BadRequestException(
                String.format("Vous avez atteint la limite de %d tontines pour votre plan. " +
                            "Passez à un plan supérieur pour créer plus de tontines.", maxGroups)
            );
        }
        
        log.info("📊 Utilisateur {} a créé {}/{} tontines", personId, groupCount, maxGroups);
    }
}
