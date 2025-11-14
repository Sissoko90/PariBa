package com.example.pariba.services;

import com.example.pariba.dtos.requests.CreateDelegationRequest;
import com.example.pariba.dtos.responses.DelegationResponse;

import java.util.List;

public interface IDelegationService {
    DelegationResponse createDelegation(String personId, CreateDelegationRequest request);
    DelegationResponse getDelegationById(String delegationId);
    List<DelegationResponse> getDelegationsByGroup(String groupId);
    List<DelegationResponse> getActiveDelegations(String groupId);
    void revokeDelegation(String delegationId, String personId);
    void expireOldDelegations();
}
