package com.example.pariba.security;

import com.example.pariba.enums.GroupRole;
import com.example.pariba.exceptions.UnauthorizedException;
import com.example.pariba.models.GroupMembership;
import com.example.pariba.models.GroupMembershipId;
import com.example.pariba.repositories.GroupMembershipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service de validation des permissions
 * Vérifie les droits d'accès aux ressources par groupe
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionService {
    
    private final GroupMembershipRepository membershipRepository;
    
    /**
     * Vérifie si une personne est membre d'un groupe
     */
    public boolean isMemberOfGroup(String personId, String groupId) {
        GroupMembershipId id = new GroupMembershipId(groupId, personId);
        return membershipRepository.existsById(id);
    }
    
    /**
     * Vérifie si une personne est admin d'un groupe
     */
    public boolean isGroupAdmin(String personId, String groupId) {
        GroupMembershipId id = new GroupMembershipId(groupId, personId);
        Optional<GroupMembership> membership = membershipRepository.findById(id);
        return membership.isPresent() && membership.get().getRole() == GroupRole.ADMIN;
    }
    
    /**
     * Vérifie si une personne est admin ou trésorier d'un groupe
     */
    public boolean isGroupAdminOrTreasurer(String personId, String groupId) {
        GroupMembershipId id = new GroupMembershipId(groupId, personId);
        Optional<GroupMembership> membership = membershipRepository.findById(id);
        
        if (membership.isEmpty()) {
            return false;
        }
        
        GroupRole role = membership.get().getRole();
        return role == GroupRole.ADMIN;
    }
    
    /**
     * Vérifie et lance une exception si la personne n'est pas membre
     */
    public void requireMemberOfGroup(String personId, String groupId) {
        if (!isMemberOfGroup(personId, groupId)) {
            log.warn("Accès refusé: {} n'est pas membre du groupe {}", personId, groupId);
            throw new UnauthorizedException("Vous n'êtes pas membre de ce groupe");
        }
    }
    
    /**
     * Vérifie et lance une exception si la personne n'est pas admin
     */
    public void requireGroupAdmin(String personId, String groupId) {
        if (!isGroupAdmin(personId, groupId)) {
            log.warn("Accès refusé: {} n'est pas admin du groupe {}", personId, groupId);
            throw new UnauthorizedException("Vous devez être administrateur de ce groupe");
        }
    }
    
    /**
     * Vérifie et lance une exception si la personne n'est pas admin ou trésorier
     */
    public void requireGroupAdminOrTreasurer(String personId, String groupId) {
        if (!isGroupAdminOrTreasurer(personId, groupId)) {
            log.warn("Accès refusé: {} n'est pas admin/trésorier du groupe {}", personId, groupId);
            throw new UnauthorizedException("Vous devez être administrateur ou trésorier de ce groupe");
        }
    }
    
    /**
     * Récupère le rôle d'une personne dans un groupe
     */
    public Optional<GroupRole> getGroupRole(String personId, String groupId) {
        GroupMembershipId id = new GroupMembershipId(groupId, personId);
        return membershipRepository.findById(id)
                .map(GroupMembership::getRole);
    }
}
