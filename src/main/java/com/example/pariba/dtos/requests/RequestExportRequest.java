package com.example.pariba.dtos.requests;

import com.example.pariba.constants.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RequestExportRequest {
    
    @NotBlank(message = ValidationMessages.REQUIRED_GROUP_ID)
    private String groupId;
    
    @NotBlank(message = ValidationMessages.REQUIRED_FORMAT)
    @Pattern(regexp = "^(PDF|XLSX)$", message = ValidationMessages.INVALID_EXPORT_FORMAT)
    private String format;
    
    private String exportType; // CONTRIBUTIONS, PAYMENTS, MEMBERS, etc.
    private String period; // ex: "2025-10" pour octobre 2025
    private String parametersJson; // Filtres additionnels
    private String filters; // Filtres JSON

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public String getParametersJson() { return parametersJson; }
    public void setParametersJson(String parametersJson) { this.parametersJson = parametersJson; }
    public String getExportType() { return exportType; }
    public void setExportType(String exportType) { this.exportType = exportType; }
    public String getFilters() { return filters; }
    public void setFilters(String filters) { this.filters = filters; }
}
