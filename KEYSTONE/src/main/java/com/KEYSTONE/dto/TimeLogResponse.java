package com.KEYSTONE.dto;

import com.KEYSTONE.model.TimeLog;

public class TimeLogResponse {

    private Long id;
    private Long workOrderId;
    private Long technicianId;
    private String technicianEmail;
    private Integer minutes;
    private String note;

    public TimeLogResponse() {
    }

    public static TimeLogResponse fromEntity(TimeLog timeLog) {
        TimeLogResponse res = new TimeLogResponse();
        res.setId(timeLog.getId());
        res.setWorkOrderId(timeLog.getWorkOrder() != null ? timeLog.getWorkOrder().getId() : null);
        if (timeLog.getTechnician() != null) {
            res.setTechnicianId(timeLog.getTechnician().getId());
            res.setTechnicianEmail(timeLog.getTechnician().getEmail());
        }
        res.setMinutes(timeLog.getMinutes());
        res.setNote(timeLog.getNote());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public Long getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }

    public String getTechnicianEmail() {
        return technicianEmail;
    }

    public void setTechnicianEmail(String technicianEmail) {
        this.technicianEmail = technicianEmail;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
