package com.example.pariba.services;

import com.example.pariba.dtos.requests.RequestExportRequest;
import com.example.pariba.dtos.responses.ExportJobResponse;

import java.util.List;

public interface IExportService {
    ExportJobResponse requestExport(String personId, RequestExportRequest request);
    ExportJobResponse getExportJobById(String jobId);
    List<ExportJobResponse> getExportJobsByPerson(String personId);
    void processExportJob(String jobId);
    void cleanupOldExports();
}
