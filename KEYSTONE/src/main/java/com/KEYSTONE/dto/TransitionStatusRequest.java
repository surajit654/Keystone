package com.KEYSTONE.dto;

import jakarta.validation.constraints.NotBlank;

public class TransitionStatusRequest {

    @NotBlank(message = "Target status is required")
    private String status;

    private String note;

    public TransitionStatusRequest() {
    }

    public TransitionStatusRequest(String status, String note) {
        this.status = status;
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
