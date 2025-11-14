package com.example.pariba.services;

import com.example.pariba.dtos.requests.GenerateToursRequest;
import com.example.pariba.dtos.responses.TourResponse;

import java.util.List;

public interface ITourService {
    List<TourResponse> generateTours(String personId, GenerateToursRequest request);
    TourResponse getTourById(String tourId);
    List<TourResponse> getToursByGroup(String groupId);
    TourResponse getCurrentTour(String groupId);
    TourResponse getNextTour(String groupId);
    void startTour(String tourId, String personId);
    void completeTour(String tourId, String personId);
}
