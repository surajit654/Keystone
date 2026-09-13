package com.KEYSTONE.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CreateCustomerRequest {

    @NotBlank(message = "Customer name is required")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    private String phone;

    public CreateCustomerRequest() {
    }

    public CreateCustomerRequest(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
