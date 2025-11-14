package com.example.pariba.services;

import com.example.pariba.dtos.requests.CreateGroupRequest;
import com.example.pariba.dtos.requests.UpdateGroupRequest;
import com.example.pariba.dtos.responses.GroupResponse;

import java.util.List;

public interface ITontineGroupService {
    GroupResponse createGroup(String creatorId, CreateGroupRequest request);
    GroupResponse updateGroup(String groupId, String personId, UpdateGroupRequest request);
    GroupResponse getGroupById(String groupId);
    List<GroupResponse> getGroupsByPerson(String personId);
    List<GroupResponse> getGroupsCreatedByPerson(String personId);
    void deleteGroup(String groupId, String personId);
    void leaveGroup(String groupId, String personId);
    void checkIsAdmin(String groupId, String personId);
    boolean isMember(String groupId, String personId);
}
