package com.example.pariba.services;

import com.example.pariba.dtos.requests.UpdateMemberRoleRequest;
import com.example.pariba.dtos.responses.MembershipResponse;

import java.util.List;

public interface IMembershipService {
    List<MembershipResponse> getMembersByGroup(String groupId);
    MembershipResponse getMemberByGroupAndPerson(String groupId, String personId);
    List<MembershipResponse> getGroupsByPerson(String personId);
    MembershipResponse updateMemberRole(String requesterId, UpdateMemberRoleRequest request);
    MembershipResponse promoteMemberToAdmin(String groupId, String personId, String requesterId);
    MembershipResponse demoteAdminToMember(String groupId, String personId, String requesterId);
    void removeMember(String groupId, String personId, String requesterId);
    long countMembersByGroup(String groupId);
}
