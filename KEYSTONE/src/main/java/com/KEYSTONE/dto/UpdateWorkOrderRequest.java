package com.KEYSTONE.dto;

public class UpdateWorkOrderRequest {

    private String title;
    private String description;
    private String priority;
    private Long siteId;

    public UpdateWorkOrderRequest() {
    }

    public UpdateWorkOrderRequest(String title, String description, String priority, Long siteId) {
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.siteId = siteId;
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}
