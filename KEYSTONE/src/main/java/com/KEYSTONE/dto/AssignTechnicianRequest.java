package com.KEYSTONE.dto;

public class AssignTechnicianRequest {

    private Long technicianId;

    public AssignTechnicianRequest() {
    }

    public AssignTechnicianRequest(Long technicianId) {
        this.technicianId = technicianId;
    }

    public Long getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }
}
