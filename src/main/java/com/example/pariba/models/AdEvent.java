package com.example.pariba.models;

import jakarta.persistence.*;

@Entity
@Table(name = "ad_events", indexes = { @Index(columnList = "ad_id"), @Index(columnList = "person_id") })
public class AdEvent extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ad_id")
    private Advertisement ad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private Person person;

    private String type; // impression/click
    private String metaJson;
    private com.example.pariba.enums.AdEventType eventType;
    private java.time.LocalDateTime eventTimestamp;

    public Advertisement getAd() { return ad; }
    public void setAd(Advertisement ad) { this.ad = ad; }
    public Person getPerson() { return person; }
    public void setPerson(Person person) { this.person = person; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMetaJson() { return metaJson; }
    public void setMetaJson(String metaJson) { this.metaJson = metaJson; }
    public com.example.pariba.enums.AdEventType getEventType() { return eventType; }
    public void setEventType(com.example.pariba.enums.AdEventType eventType) { this.eventType = eventType; }
    public java.time.LocalDateTime getEventTimestamp() { return eventTimestamp; }
    public void setEventTimestamp(java.time.LocalDateTime eventTimestamp) { this.eventTimestamp = eventTimestamp; }
}