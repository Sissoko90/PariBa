package com.example.pariba.dtos.requests;

import com.example.pariba.constants.ValidationMessages;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class GenerateToursRequest {
    
    @NotBlank(message = ValidationMessages.REQUIRED_GROUP_ID)
    private String groupId;
    
    // Liste optionnelle pour CUSTOM rotation mode (ordre personnalisé des bénéficiaires)
    private List<String> customBeneficiaryOrder;
    
    // Optionnel: true pour mélanger l'ordre, false pour ordre séquentiel
    private Boolean shuffle;

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public List<String> getCustomBeneficiaryOrder() { return customBeneficiaryOrder; }
    public void setCustomBeneficiaryOrder(List<String> customBeneficiaryOrder) { this.customBeneficiaryOrder = customBeneficiaryOrder; }
    public Boolean getShuffle() { return shuffle; }
    public void setShuffle(Boolean shuffle) { this.shuffle = shuffle; }
}
