package com.example.pariba.dtos.responses;

public class GroupShareLinkResponse {
    private String groupId;
    private String groupName;
    private String shareLink;
    private String shareText;

    public GroupShareLinkResponse() {}

    public GroupShareLinkResponse(String groupId, String groupName, String shareLink, String shareText) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.shareLink = shareLink;
        this.shareText = shareText;
    }

    // Getters et Setters
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }

    public String getShareLink() { return shareLink; }
    public void setShareLink(String shareLink) { this.shareLink = shareLink; }

    public String getShareText() { return shareText; }
    public void setShareText(String shareText) { this.shareText = shareText; }
}
