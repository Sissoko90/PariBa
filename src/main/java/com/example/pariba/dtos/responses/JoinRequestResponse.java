package com.example.pariba.dtos.responses;

import com.example.pariba.enums.JoinRequestStatus;
import com.example.pariba.models.JoinRequest;

import java.time.Instant;

public class JoinRequestResponse {
    private String id;
    private String groupId;
    private String groupName;
    private PersonResponse person;
    private JoinRequestStatus status;
    private String message;
    private PersonResponse reviewedBy;
    private Instant reviewedAt;
    private String reviewNote;
    private Instant createdAt;

    public JoinRequestResponse() {}

    public JoinRequestResponse(JoinRequest joinRequest) {
        this.id = joinRequest.getId();
        this.groupId = joinRequest.getGroup().getId();
        this.groupName = joinRequest.getGroup().getNom();
        this.person = new PersonResponse(joinRequest.getPerson());
        this.status = joinRequest.getStatus();
        this.message = joinRequest.getMessage();
        if (joinRequest.getReviewedBy() != null) {
            this.reviewedBy = new PersonResponse(joinRequest.getReviewedBy());
        }
        this.reviewedAt = joinRequest.getReviewedAt();
        this.reviewNote = joinRequest.getReviewNote();
        this.createdAt = joinRequest.getCreatedAt();
    }

    // Getters et Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public PersonResponse getPerson() { return person; }
    public void setPerson(PersonResponse person) { this.person = person; }

    public JoinRequestStatus getStatus() { return status; }
    public void setStatus(JoinRequestStatus status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public PersonResponse getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(PersonResponse reviewedBy) { this.reviewedBy = reviewedBy; }

    public Instant getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(Instant reviewedAt) { this.reviewedAt = reviewedAt; }

    public String getReviewNote() { return reviewNote; }
    public void setReviewNote(String reviewNote) { this.reviewNote = reviewNote; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
