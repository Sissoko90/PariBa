package com.example.pariba.dtos.responses;

import com.example.pariba.enums.PaymentStatus;
import com.example.pariba.enums.PaymentType;
import com.example.pariba.models.Payout;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de réponse pour les déboursements (payouts)
 * Représente un paiement effectué au bénéficiaire d'un tour
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayoutResponse {
    
    /**
     * Identifiant unique du payout
     */
    private String id;
    
    /**
     * Identifiant du tour concerné
     */
    private String tourId;
    
    /**
     * Index du tour dans le groupe
     */
    private Integer tourIndex;
    
    /**
     * Identifiant du groupe
     */
    private String groupId;
    
    /**
     * Nom du groupe
     */
    private String groupName;
    
    /**
     * Identifiant du bénéficiaire
     */
    private String beneficiaryId;
    
    /**
     * Nom complet du bénéficiaire
     */
    private String beneficiaryName;
    
    /**
     * Téléphone du bénéficiaire
     */
    private String beneficiaryPhone;
    
    /**
     * Montant du payout
     */
    private BigDecimal amount;
    
    /**
     * Type de paiement utilisé
     */
    private PaymentType paymentType;
    
    /**
     * Statut du payout
     */
    private PaymentStatus status;
    
    /**
     * Référence externe (ID de transaction de l'opérateur)
     */
    private String externalReference;
    
    /**
     * URL du reçu/facture
     */
    private String invoiceUrl;
    
    /**
     * Date de création du payout
     */
    private LocalDateTime createdAt;
    
    /**
     * Date de traitement du payout
     */
    private LocalDateTime processedAt;
    
    /**
     * Commentaire ou note
     */
    private String notes;
    
    /**
     * Constructeur à partir d'une entité Payout
     */
    public PayoutResponse(Payout payout) {
        this.id = payout.getId();
        if (payout.getTour() != null) {
            this.tourId = payout.getTour().getId();
            this.tourIndex = payout.getTour().getIndexInGroup();
            if (payout.getTour().getGroup() != null) {
                this.groupId = payout.getTour().getGroup().getId();
                this.groupName = payout.getTour().getGroup().getNom();
            }
        }
        if (payout.getBeneficiary() != null) {
            this.beneficiaryId = payout.getBeneficiary().getId();
            this.beneficiaryName = payout.getBeneficiary().getPrenom() + " " + payout.getBeneficiary().getNom();
            this.beneficiaryPhone = payout.getBeneficiary().getPhone();
        }
        this.amount = payout.getAmount();
        this.paymentType = payout.getPaymentType();
        this.status = payout.getStatus();
        this.externalReference = payout.getExternalRef();
        this.notes = payout.getNotes();
        if (payout.getCreatedAt() != null) {
            this.createdAt = LocalDateTime.ofInstant(payout.getCreatedAt(), java.time.ZoneId.systemDefault());
        }
    }
}
