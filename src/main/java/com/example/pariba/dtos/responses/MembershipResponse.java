package com.example.pariba.dtos.responses;

import com.example.pariba.enums.GroupRole;
import com.example.pariba.models.GroupMembership;

import java.time.LocalDate;

public class MembershipResponse {
    
    private PersonResponse person;
    private GroupRole role;
    private LocalDate joinedAt;
    private String groupId;
    private String groupName;

    public MembershipResponse() {}

    public MembershipResponse(GroupMembership membership) {
        this.person = new PersonResponse(membership.getPerson());
        this.role = membership.getRole();
        this.joinedAt = membership.getJoinedAt();
        this.groupId = membership.getGroup().getId();
        this.groupName = membership.getGroup().getNom();
    }

    public PersonResponse getPerson() { return person; }
    public void setPerson(PersonResponse person) { this.person = person; }
    public GroupRole getRole() { return role; }
    public void setRole(GroupRole role) { this.role = role; }
    public LocalDate getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDate joinedAt) { this.joinedAt = joinedAt; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
}
