package com.example.pariba.services;

import com.example.pariba.dtos.requests.CreateJoinRequestRequest;
import com.example.pariba.dtos.requests.ReviewJoinRequestRequest;
import com.example.pariba.dtos.responses.JoinRequestResponse;

import java.util.List;

public interface IJoinRequestService {
    JoinRequestResponse createJoinRequest(String personId, CreateJoinRequestRequest request);
    JoinRequestResponse reviewJoinRequest(String requestId, String adminId, ReviewJoinRequestRequest request);
    void cancelJoinRequest(String requestId, String personId);
    List<JoinRequestResponse> getGroupJoinRequests(String groupId, String adminId);
    List<JoinRequestResponse> getMyJoinRequests(String personId);
    long countPendingJoinRequests(String groupId, String adminId);
}
