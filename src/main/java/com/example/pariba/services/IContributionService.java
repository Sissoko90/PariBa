package com.example.pariba.services;

import com.example.pariba.dtos.responses.ContributionResponse;

import java.util.List;

public interface IContributionService {
    ContributionResponse getContributionById(String contributionId);
    List<ContributionResponse> getContributionsByGroup(String groupId);
    List<ContributionResponse> getContributionsByTour(String tourId);
    List<ContributionResponse> getContributionsByMember(String personId);
    List<ContributionResponse> getPendingContributions(String groupId);
    void applyLatePenalties();
    void markAsPaid(String contributionId);
}
