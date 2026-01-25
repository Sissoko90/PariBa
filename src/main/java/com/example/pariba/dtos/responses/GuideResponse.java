package com.example.pariba.dtos.responses;

import com.example.pariba.enums.GuideCategory;

import java.time.Instant;

public class GuideResponse {
    
    private String id;
    private String title;
    private String description;
    private String content;
    private GuideCategory category;
    private Integer displayOrder;
    private Boolean active;
    private Integer viewCount;
    private String iconName;
    private Integer estimatedReadTime;
    private Instant createdAt;
    private Instant updatedAt;
    
    // Constructors
    public GuideResponse() {}
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public GuideCategory getCategory() {
        return category;
    }
    
    public void setCategory(GuideCategory category) {
        this.category = category;
    }
    
    public Integer getDisplayOrder() {
        return displayOrder;
    }
    
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public Integer getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }
    
    public String getIconName() {
        return iconName;
    }
    
    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
    
    public Integer getEstimatedReadTime() {
        return estimatedReadTime;
    }
    
    public void setEstimatedReadTime(Integer estimatedReadTime) {
        this.estimatedReadTime = estimatedReadTime;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
