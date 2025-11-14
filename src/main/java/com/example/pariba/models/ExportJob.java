package com.example.pariba.models;

import com.example.pariba.enums.ExportStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "export_jobs", indexes = { @Index(columnList = "group_id"), @Index(columnList = "status") })
public class ExportJob extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private TontineGroup group;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by_id")
    private Person requestedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExportStatus status = ExportStatus.QUEUED;
    
    private String exportType; // CONTRIBUTIONS, PAYMENTS, etc.
    private String format; // PDF, XLSX
    private String downloadUrl; // rempli quand DONE
    private String parametersJson; // période, filtres…
    private String filters; // Filtres JSON
    private String errorMessage; // Message d'erreur en cas d'échec

    public TontineGroup getGroup() { return group; }
    public void setGroup(TontineGroup group) { this.group = group; }
    public ExportStatus getStatus() { return status; }
    public void setStatus(ExportStatus status) { this.status = status; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    public String getParametersJson() { return parametersJson; }
    public void setParametersJson(String parametersJson) { this.parametersJson = parametersJson; }
    public Person getRequestedBy() { return requestedBy; }
    public void setRequestedBy(Person requestedBy) { this.requestedBy = requestedBy; }
    public String getExportType() { return exportType; }
    public void setExportType(String exportType) { this.exportType = exportType; }
    public String getFilters() { return filters; }
    public void setFilters(String filters) { this.filters = filters; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}