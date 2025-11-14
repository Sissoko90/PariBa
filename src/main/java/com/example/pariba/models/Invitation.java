package com.example.pariba.models;

import com.example.pariba.enums.InvitationStatus;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "invitations", indexes = {
  @Index(columnList = "targetPhone"),
  @Index(columnList = "status")
})
public class Invitation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private TontineGroup group;

    // peut être vide si lien partagé
    private String targetPhone;
    private String targetEmail;
    @Column(nullable = false, unique = true) private String linkCode; // pour invitation par lien

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status = InvitationStatus.PENDING;

    private Instant expiresAt;

    public TontineGroup getGroup() { return group; }
    public void setGroup(TontineGroup group) { this.group = group; }
    public String getTargetPhone() { return targetPhone; }
    public void setTargetPhone(String targetPhone) { this.targetPhone = targetPhone; }
    public String getTargetEmail() { return targetEmail; }
    public void setTargetEmail(String targetEmail) { this.targetEmail = targetEmail; }
    public String getLinkCode() { return linkCode; }
    public void setLinkCode(String linkCode) { this.linkCode = linkCode; }
    public InvitationStatus getStatus() { return status; }
    public void setStatus(InvitationStatus status) { this.status = status; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}