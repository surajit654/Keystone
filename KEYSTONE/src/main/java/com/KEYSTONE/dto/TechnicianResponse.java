package com.KEYSTONE.dto;

import com.KEYSTONE.model.Role;
import com.KEYSTONE.model.User;

public class TechnicianResponse {

    private Long id;
    private String email;
    private Role role;

    public TechnicianResponse() {
    }

    public TechnicianResponse(Long id, String email, Role role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public static TechnicianResponse fromUser(User user) {
        return new TechnicianResponse(user.getId(), user.getEmail(), user.getRole());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
