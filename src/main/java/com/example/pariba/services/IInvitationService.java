package com.example.pariba.services;

import com.example.pariba.dtos.requests.InviteMemberRequest;
import com.example.pariba.dtos.responses.InvitationResponse;

import java.util.List;

public interface IInvitationService {
    InvitationResponse inviteMember(String inviterId, InviteMemberRequest request);
    void acceptInvitation(String personId, String linkCode);
    List<InvitationResponse> getInvitationsByGroup(String groupId);
    void cleanupExpiredInvitations();
}
