package com.example.pariba.dtos.responses;

import com.example.pariba.enums.DelegationStatus;
import com.example.pariba.models.Delegation;

import java.time.Instant;
import java.time.LocalDate;

public class DelegationResponse {
    
    private String id;
    private String groupId;
    private String groupName;
    private PersonResponse grantor;
    private PersonResponse proxy;
    private DelegationStatus status;
    private LocalDate validFrom;
    private LocalDate validTo;
    private Instant createdAt;

    public DelegationResponse() {}

    public DelegationResponse(Delegation delegation) {
        this.id = delegation.getId();
        this.groupId = delegation.getGroup().getId();
        this.groupName = delegation.getGroup().getNom();
        this.grantor = new PersonResponse(delegation.getGrantor());
        this.proxy = new PersonResponse(delegation.getProxy());
        this.status = delegation.getStatus();
        this.validFrom = delegation.getValidFrom();
        this.validTo = delegation.getValidTo();
        this.createdAt = delegation.getCreatedAt();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public PersonResponse getGrantor() { return grantor; }
    public void setGrantor(PersonResponse grantor) { this.grantor = grantor; }
    public PersonResponse getProxy() { return proxy; }
    public void setProxy(PersonResponse proxy) { this.proxy = proxy; }
    public DelegationStatus getStatus() { return status; }
    public void setStatus(DelegationStatus status) { this.status = status; }
    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }
    public LocalDate getValidTo() { return validTo; }
    public void setValidTo(LocalDate validTo) { this.validTo = validTo; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
