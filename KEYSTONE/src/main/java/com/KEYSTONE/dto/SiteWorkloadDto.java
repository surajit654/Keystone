package com.KEYSTONE.dto;

public class SiteWorkloadDto {

    private Long siteId;
    private String siteName;
    private String customerName;
    private long activeJobs;

    public SiteWorkloadDto() {
    }

    public SiteWorkloadDto(Long siteId, String siteName, String customerName, long activeJobs) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.customerName = customerName;
        this.activeJobs = activeJobs;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public long getActiveJobs() {
        return activeJobs;
    }

    public void setActiveJobs(long activeJobs) {
        this.activeJobs = activeJobs;
    }
}
