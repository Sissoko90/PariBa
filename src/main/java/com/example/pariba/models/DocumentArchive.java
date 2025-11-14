package com.example.pariba.models;

import com.example.pariba.enums.DocumentType;
import jakarta.persistence.*;

@Entity
@Table(name = "documents", indexes = { @Index(columnList = "group_id"), @Index(columnList = "type") })
public class DocumentArchive extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private TontineGroup group;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType type;

    @Column(nullable = false)
    private String url; // stockage
    
    private String fileName;
    private String downloadUrl;
    private Long fileSize;
    private String mimeType;
    private String metadata;
    private java.time.Instant expiresAt;

    private String metaJson; // ex: { "cycle":1, "member":"..." }

    public TontineGroup getGroup() { return group; }
    public void setGroup(TontineGroup group) { this.group = group; }
    public DocumentType getType() { return type; }
    public void setType(DocumentType type) { this.type = type; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    public java.time.Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(java.time.Instant expiresAt) { this.expiresAt = expiresAt; }
    public String getMetaJson() { return metaJson; }
    public void setMetaJson(String metaJson) { this.metaJson = metaJson; }
}