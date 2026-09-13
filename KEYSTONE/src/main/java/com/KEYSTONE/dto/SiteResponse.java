package com.KEYSTONE.dto;

import com.KEYSTONE.model.Site;

public class SiteResponse {

    private Long id;
    private String name;
    private String address;
    private String city;
    private Long customerId;
    private String customerName;

    public SiteResponse() {
    }

    public SiteResponse(Long id, String name, String address, String city, Long customerId, String customerName) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.customerId = customerId;
        this.customerName = customerName;
    }

    public static SiteResponse fromEntity(Site site) {
        return new SiteResponse(
                site.getId(),
                site.getName(),
                site.getAddress(),
                site.getCity(),
                site.getCustomer() != null ? site.getCustomer().getId() : null,
                site.getCustomer() != null ? site.getCustomer().getName() : null
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
