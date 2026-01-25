package com.example.pariba.models;

import com.example.pariba.enums.AdPlacement;
import jakarta.persistence.*;

@Entity
@Table(name = "advertisements", indexes = { @Index(columnList = "placement") })
public class Advertisement extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdPlacement placement;

    @Column(nullable = false)
    private String imageUrl;

    private String linkUrl;
    private String videoUrl; // URL de la vidéo (uniquement pour FULLSCREEN)
    private String targetingJson; // pays, plan, etc.
    private String title;
    private String description;
    private String targetingCriteria;
    private Integer impressions = 0;
    private Integer clicks = 0;
    private java.time.LocalDateTime startDate;
    private java.time.LocalDateTime endDate;
    private boolean active = true;

    public AdPlacement getPlacement() { return placement; }
    public void setPlacement(AdPlacement placement) { this.placement = placement; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getLinkUrl() { return linkUrl; }
    public void setLinkUrl(String linkUrl) { 
        if (linkUrl != null && !linkUrl.isEmpty()) {
            // Décoder les entités HTML (&#x2F; -> /, etc.)
            String decodedUrl = linkUrl
                .replace("&#x2F;", "/")
                .replace("&#x3A;", ":")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'");
            
            // Supprimer le doublon https://https:// si présent
            if (decodedUrl.startsWith("https://https://")) {
                decodedUrl = decodedUrl.substring(8); // Enlever le premier "https://"
            } else if (decodedUrl.startsWith("http://http://")) {
                decodedUrl = decodedUrl.substring(7); // Enlever le premier "http://"
            }
            
            this.linkUrl = decodedUrl.trim();
        } else {
            this.linkUrl = linkUrl;
        }
    }
    public String getTargetingJson() { return targetingJson; }
    public void setTargetingJson(String targetingJson) { this.targetingJson = targetingJson; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getTargetingCriteria() { return targetingCriteria; }
    public void setTargetingCriteria(String targetingCriteria) { this.targetingCriteria = targetingCriteria; }
    public Integer getImpressions() { return impressions; }
    public void setImpressions(Integer impressions) { this.impressions = impressions; }
    public Integer getClicks() { return clicks; }
    public void setClicks(Integer clicks) { this.clicks = clicks; }
    public java.time.LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(java.time.LocalDateTime startDate) { this.startDate = startDate; }
    public java.time.LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(java.time.LocalDateTime endDate) { this.endDate = endDate; }
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean getActive() { return active; }
}