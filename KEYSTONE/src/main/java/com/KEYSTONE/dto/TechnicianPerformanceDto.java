package com.KEYSTONE.dto;

public class TechnicianPerformanceDto {

    private Long technicianId;
    private String email;
    private long activeJobs;
    private long completedJobs;

    public TechnicianPerformanceDto() {
    }

    public TechnicianPerformanceDto(Long technicianId, String email, long activeJobs, long completedJobs) {
        this.technicianId = technicianId;
        this.email = email;
        this.activeJobs = activeJobs;
        this.completedJobs = completedJobs;
    }

    public Long getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getActiveJobs() {
        return activeJobs;
    }

    public void setActiveJobs(long activeJobs) {
        this.activeJobs = activeJobs;
    }

    public long getCompletedJobs() {
        return completedJobs;
    }

    public void setCompletedJobs(long completedJobs) {
        this.completedJobs = completedJobs;
    }
}
