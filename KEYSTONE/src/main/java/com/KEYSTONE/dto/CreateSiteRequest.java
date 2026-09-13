package com.KEYSTONE.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateSiteRequest {

    @NotBlank(message = "Site name is required")
    private String name;

    private String address;

    private String city;

    public CreateSiteRequest() {
    }

    public CreateSiteRequest(String name, String address, String city) {
        this.name = name;
        this.address = address;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
