package com.example.pariba.services;

import com.example.pariba.dtos.requests.CreateGroupRequest;
import com.example.pariba.dtos.requests.UpdateGroupRequest;
import com.example.pariba.dtos.responses.GroupResponse;
import com.example.pariba.dtos.responses.GroupShareLinkResponse;
import com.example.pariba.models.GroupMembership;

import java.util.List;

public interface ITontineGroupService {
    GroupResponse createGroup(String creatorId, CreateGroupRequest request);
    GroupResponse updateGroup(String groupId, String personId, UpdateGroupRequest request);
    GroupResponse getGroupById(String groupId, String personId);
    List<GroupResponse> getGroupsByPerson(String personId);
    List<GroupResponse> getGroupsCreatedByPerson(String personId);
    void deleteGroup(String groupId, String personId);
    void leaveGroup(String groupId, String personId);
    GroupMembership checkIsAdmin(String groupId, String personId);
    boolean isMember(String groupId, String personId);
    GroupShareLinkResponse generateShareLink(String groupId, String personId);
}
