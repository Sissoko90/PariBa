package com.example.pariba.dtos.requests;

import com.example.pariba.constants.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateJoinRequestRequest {

    @NotBlank(message = ValidationMessages.REQUIRED_GROUP_ID)
    private String groupId;

    @Size(max = 500, message = "Le message ne peut pas dépasser 500 caractères")
    private String message;

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
