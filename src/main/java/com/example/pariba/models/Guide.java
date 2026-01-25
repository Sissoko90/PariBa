package com.example.pariba.models;

import com.example.pariba.enums.GuideCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "guides")
public class Guide extends BaseEntity {
    
    @NotBlank(message = "Le titre est requis")
    @Size(min = 5, max = 200, message = "Le titre doit contenir entre 5 et 200 caractères")
    @Column(nullable = false, length = 200)
    private String title;
    
    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
    @Column(length = 500)
    private String description;
    
    @NotBlank(message = "Le contenu est requis")
    @Size(min = 10, message = "Le contenu doit contenir au moins 10 caractères")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @NotNull(message = "La catégorie est requise")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private GuideCategory category;
    
    @Min(value = 0, message = "L'ordre d'affichage doit être positif")
    @Column(name = "display_order")
    private Integer displayOrder = 0;
    
    @NotNull(message = "Le statut actif est requis")
    @Column(nullable = false)
    private Boolean active = true;
    
    @Min(value = 0, message = "Le nombre de vues doit être positif")
    @Column(name = "view_count")
    private Integer viewCount = 0;
    
    @Column(name = "icon_name", length = 50)
    private String iconName;
    
    @Min(value = 1, message = "Le temps de lecture estimé doit être au moins 1 minute")
    @Column(name = "estimated_read_time")
    private Integer estimatedReadTime;
    
    // Constructors
    public Guide() {}
    
    // Getters and Setters
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
}
