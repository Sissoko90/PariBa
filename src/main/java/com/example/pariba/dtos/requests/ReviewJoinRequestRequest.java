package com.example.pariba.dtos.requests;

import com.example.pariba.constants.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ReviewJoinRequestRequest {

    @NotBlank(message = "L'action est requise (APPROVE ou REJECT)")
    private String action; // APPROVE ou REJECT

    @Size(max = 500, message = "La note ne peut pas dépasser 500 caractères")
    private String note;

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
