package com.example.pariba.security;

import com.example.pariba.enums.GroupRole;
import com.example.pariba.models.Payment;
import com.example.pariba.repositories.GroupMembershipRepository;
import com.example.pariba.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service de sécurité pour vérifier les rôles dans les groupes de tontine
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GroupSecurityService {

    private final GroupMembershipRepository membershipRepository;
    private final PaymentRepository paymentRepository;

    /**
     * Vérifie si une personne est admin du groupe associé à un paiement
     * 
     * @param personId ID de la personne
     * @param paymentId ID du paiement
     * @return true si la personne est admin du groupe, false sinon
     */
    public boolean isGroupAdmin(String personId, String paymentId) {
        log.info("🔐 Vérification admin groupe - personId: {}, paymentId: {}", personId, paymentId);
        
        // Récupérer le paiement pour obtenir le groupe
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment == null) {
            log.warn("❌ Paiement non trouvé: {}", paymentId);
            return false;
        }
        
        String groupId = payment.getGroup().getId();
        log.info("📦 Groupe du paiement: {}", groupId);
        
        // Vérifier le rôle dans group_memberships
        boolean isAdmin = membershipRepository.findByGroupIdAndPersonId(groupId, personId)
                .map(membership -> {
                    boolean admin = membership.getRole() == GroupRole.ADMIN;
                    log.info("👤 Rôle de {} dans le groupe {}: {} - Admin: {}", 
                            personId, groupId, membership.getRole(), admin);
                    return admin;
                })
                .orElse(false);
        
        if (!isAdmin) {
            log.warn("⛔ Accès refusé - {} n'est pas admin du groupe {}", personId, groupId);
        } else {
            log.info("✅ Accès autorisé - {} est admin du groupe {}", personId, groupId);
        }
        
        return isAdmin;
    }
    
    /**
     * Vérifie si une personne est admin d'un groupe spécifique
     * 
     * @param personId ID de la personne
     * @param groupId ID du groupe
     * @return true si la personne est admin du groupe, false sinon
     */
    public boolean isGroupAdminByGroupId(String personId, String groupId) {
        log.info("🔐 Vérification admin groupe - personId: {}, groupId: {}", personId, groupId);
        
        boolean isAdmin = membershipRepository.findByGroupIdAndPersonId(groupId, personId)
                .map(membership -> membership.getRole() == GroupRole.ADMIN)
                .orElse(false);
        
        if (!isAdmin) {
            log.warn("⛔ Accès refusé - {} n'est pas admin du groupe {}", personId, groupId);
        } else {
            log.info("✅ Accès autorisé - {} est admin du groupe {}", personId, groupId);
        }
        
        return isAdmin;
    }
    
    /**
     * Vérifie si une personne est membre d'un groupe (admin ou membre simple)
     * 
     * @param personId ID de la personne
     * @param groupId ID du groupe
     * @return true si la personne est membre du groupe, false sinon
     */
    public boolean isGroupMember(String personId, String groupId) {
        return membershipRepository.findByGroupIdAndPersonId(groupId, personId).isPresent();
    }
}
