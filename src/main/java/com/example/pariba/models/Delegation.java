package com.example.pariba.models;

import com.example.pariba.enums.DelegationStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "delegations",
       uniqueConstraints = @UniqueConstraint(columnNames = {"group_id","grantor_person_id","proxy_person_id"}))
public class Delegation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private TontineGroup group;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grantor_person_id", nullable = false)
    private Person grantor; // donneur d'ordre

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proxy_person_id", nullable = false)
    private Person proxy; // personne déléguée

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DelegationStatus status = DelegationStatus.PENDING;

    private LocalDate validFrom;
    private LocalDate validTo;
    private boolean active = true;

    public TontineGroup getGroup() { return group; }
    public void setGroup(TontineGroup group) { this.group = group; }
    public Person getGrantor() { return grantor; }
    public void setGrantor(Person grantor) { this.grantor = grantor; }
    public Person getProxy() { return proxy; }
    public void setProxy(Person proxy) { this.proxy = proxy; }
    public DelegationStatus getStatus() { return status; }
    public void setStatus(DelegationStatus status) { this.status = status; }
    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }
    public LocalDate getValidTo() { return validTo; }
    public void setValidTo(LocalDate validTo) { this.validTo = validTo; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}