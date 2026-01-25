package com.example.pariba.models;

import com.example.pariba.enums.FAQCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "faqs")
public class FAQ extends BaseEntity {
    
    @NotBlank(message = "La question est requise")
    @Size(min = 5, max = 500, message = "La question doit contenir entre 5 et 500 caractères")
    @Column(nullable = false, length = 500)
    private String question;
    
    @NotBlank(message = "La réponse est requise")
    @Size(min = 10, max = 5000, message = "La réponse doit contenir entre 10 et 5000 caractères")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String answer;
    
    @NotNull(message = "La catégorie est requise")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private FAQCategory category;
    
    @Min(value = 0, message = "L'ordre d'affichage doit être positif")
    @Column(name = "display_order")
    private Integer displayOrder = 0;
    
    @NotNull(message = "Le statut actif est requis")
    @Column(nullable = false)
    private Boolean active = true;
    
    @Min(value = 0, message = "Le nombre de vues doit être positif")
    @Column(name = "view_count")
    private Integer viewCount = 0;
    
    // Constructors
    public FAQ() {}
    
    // Getters and Setters
    public String getQuestion() {
        return question;
    }
    
    public void setQuestion(String question) {
        this.question = question;
    }
    
    public String getAnswer() {
        return answer;
    }
    
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    
    public FAQCategory getCategory() {
        return category;
    }
    
    public void setCategory(FAQCategory category) {
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
}
