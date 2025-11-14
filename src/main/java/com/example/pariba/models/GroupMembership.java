package com.example.pariba.models;

import com.example.pariba.enums.GroupRole;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "group_memberships",
       uniqueConstraints = @UniqueConstraint(columnNames = {"group_id","person_id"}))
public class GroupMembership {

    @EmbeddedId
    private GroupMembershipId id = new GroupMembershipId();

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("groupId")
    @JoinColumn(name = "group_id", nullable = false)
    private TontineGroup group;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("personId")
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupRole role = GroupRole.MEMBER;

    private LocalDate joinedAt = LocalDate.now();

    public GroupMembershipId getId() { return id; }
    public void setId(GroupMembershipId id) { this.id = id; }
    public TontineGroup getGroup() { return group; }
    public void setGroup(TontineGroup group) { this.group = group; }
    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }
    public GroupRole getRole() { return role; }
    public void setRole(GroupRole role) { this.role = role; }
    public LocalDate getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDate joinedAt) { this.joinedAt = joinedAt; }
}