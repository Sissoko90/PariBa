package com.example.pariba.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GroupMembershipId implements Serializable {
    private String groupId;
    private String personId;

    public GroupMembershipId() {}
    public GroupMembershipId(String groupId, String personId) { this.groupId = groupId; this.personId = personId; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getPersonId() { return personId; }
    public void setPersonId(String personId) { this.personId = personId; }

    @Override public boolean equals(Object o){ if(this==o)return true; if(!(o instanceof GroupMembershipId g))return false; return Objects.equals(groupId,g.groupId)&&Objects.equals(personId,g.personId);}
    @Override public int hashCode(){ return Objects.hash(groupId,personId); }
}