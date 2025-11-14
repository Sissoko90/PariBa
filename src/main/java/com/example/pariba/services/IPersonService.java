package com.example.pariba.services;

import com.example.pariba.dtos.requests.UpdateProfileRequest;
import com.example.pariba.dtos.responses.PersonResponse;
import com.example.pariba.dtos.responses.PersonalStatisticsResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IPersonService {
    PersonResponse getPersonById(String personId);
    PersonResponse updateProfile(String personId, UpdateProfileRequest request);
    PersonResponse uploadPhoto(String personId, MultipartFile file);
    void deleteAccount(String personId);
    void deletePhoto(String personId);
    PersonalStatisticsResponse getPersonalStatistics(String personId);
}
