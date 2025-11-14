package com.example.pariba.dtos.responses;

import com.example.pariba.enums.ExportStatus;
import com.example.pariba.models.ExportJob;

import java.time.Instant;

public class ExportJobResponse {
    
    private String id;
    private String groupId;
    private ExportStatus status;
    private String format;
    private String downloadUrl;
    private String parametersJson;
    private Instant createdAt;
    private Instant updatedAt;

    public ExportJobResponse() {}

    public ExportJobResponse(ExportJob exportJob) {
        this.id = exportJob.getId();
        this.groupId = exportJob.getGroup() != null ? exportJob.getGroup().getId() : null;
        this.status = exportJob.getStatus();
        this.format = exportJob.getFormat();
        this.downloadUrl = exportJob.getDownloadUrl();
        this.parametersJson = exportJob.getParametersJson();
        this.createdAt = exportJob.getCreatedAt();
        this.updatedAt = exportJob.getUpdatedAt();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public ExportStatus getStatus() { return status; }
    public void setStatus(ExportStatus status) { this.status = status; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    public String getParametersJson() { return parametersJson; }
    public void setParametersJson(String parametersJson) { this.parametersJson = parametersJson; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
