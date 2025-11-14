package com.example.pariba.dtos.requests;

import com.example.pariba.constants.ValidationMessages;
import com.example.pariba.enums.GroupRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateMemberRoleRequest {
    
    @NotBlank(message = ValidationMessages.REQUIRED_GROUP_ID)
    private String groupId;
    
    @NotBlank(message = ValidationMessages.REQUIRED_PERSON_ID)
    private String personId;
    
    @NotNull(message = ValidationMessages.REQUIRED_NEW_ROLE)
    private GroupRole newRole;

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getPersonId() { return personId; }
    public void setPersonId(String personId) { this.personId = personId; }
    public GroupRole getNewRole() { return newRole; }
    public void setNewRole(GroupRole newRole) { this.newRole = newRole; }
}
