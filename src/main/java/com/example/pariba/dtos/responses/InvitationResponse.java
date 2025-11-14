package com.example.pariba.dtos.responses;

import com.example.pariba.enums.InvitationStatus;
import com.example.pariba.models.Invitation;

import java.time.Instant;

public class InvitationResponse {
    
    private String id;
    private String groupId;
    private String groupName;
    private String targetPhone;
    private String targetEmail;
    private String linkCode;
    private String invitationLink;  // Lien complet
    private String whatsappLink;    // Lien WhatsApp pré-rempli
    private InvitationStatus status;
    private Instant expiresAt;
    private Instant createdAt;

    public InvitationResponse() {}

    public InvitationResponse(Invitation invitation) {
        this.id = invitation.getId();
        this.groupId = invitation.getGroup().getId();
        this.groupName = invitation.getGroup().getNom();
        this.targetPhone = invitation.getTargetPhone();
        this.targetEmail = invitation.getTargetEmail();
        this.linkCode = invitation.getLinkCode();
        this.status = invitation.getStatus();
        this.expiresAt = invitation.getExpiresAt();
        this.createdAt = invitation.getCreatedAt();
        
        // Générer les liens
        // TODO: Récupérer le domaine depuis la configuration
        String baseUrl = "https://pariba.app"; // ou depuis application.properties
        this.invitationLink = baseUrl + "/join/" + invitation.getLinkCode();
        
        // Générer le lien WhatsApp avec message pré-rempli
        String message = String.format(
            "🎉 Vous êtes invité à rejoindre le groupe *%s* sur Pariba!\n\n" +
            "Cliquez sur ce lien pour rejoindre: %s\n\n" +
            "⏰ Ce lien expire dans 24h",
            invitation.getGroup().getNom(),
            this.invitationLink
        );
        this.whatsappLink = "https://wa.me/?text=" + java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public String getTargetPhone() { return targetPhone; }
    public void setTargetPhone(String targetPhone) { this.targetPhone = targetPhone; }
    public String getTargetEmail() { return targetEmail; }
    public void setTargetEmail(String targetEmail) { this.targetEmail = targetEmail; }
    public String getLinkCode() { return linkCode; }
    public void setLinkCode(String linkCode) { this.linkCode = linkCode; }
    public String getInvitationLink() { return invitationLink; }
    public void setInvitationLink(String invitationLink) { this.invitationLink = invitationLink; }
    public String getWhatsappLink() { return whatsappLink; }
    public void setWhatsappLink(String whatsappLink) { this.whatsappLink = whatsappLink; }
    public InvitationStatus getStatus() { return status; }
    public void setStatus(InvitationStatus status) { this.status = status; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
