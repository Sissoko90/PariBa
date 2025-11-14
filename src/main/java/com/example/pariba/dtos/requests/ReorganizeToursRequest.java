package com.example.pariba.dtos.requests;

import com.example.pariba.constants.ValidationMessages;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * Requête pour réorganiser les tours futurs d'un groupe
 * Seuls les tours PENDING peuvent être réorganisés
 */
public class ReorganizeToursRequest {
    
    @NotBlank(message = ValidationMessages.REQUIRED_GROUP_ID)
    private String groupId;
    
    // Nouvel ordre des bénéficiaires pour les tours PENDING uniquement
    private List<String> newBeneficiaryOrder;

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public List<String> getNewBeneficiaryOrder() { return newBeneficiaryOrder; }
    public void setNewBeneficiaryOrder(List<String> newBeneficiaryOrder) { this.newBeneficiaryOrder = newBeneficiaryOrder; }
}
