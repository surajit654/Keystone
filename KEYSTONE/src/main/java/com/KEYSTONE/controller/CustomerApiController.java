package com.KEYSTONE.controller;

import com.KEYSTONE.dto.CreateCustomerRequest;
import com.KEYSTONE.dto.CreateSiteRequest;
import com.KEYSTONE.dto.CustomerResponse;
import com.KEYSTONE.dto.SiteResponse;
import com.KEYSTONE.service.CustomerService;
import com.KEYSTONE.service.SiteService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerApiController {

    private final CustomerService customerService;
    private final SiteService siteService;

    public CustomerApiController(CustomerService customerService, SiteService siteService) {
        this.customerService = customerService;
        this.siteService = siteService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        CustomerResponse response = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<CustomerResponse>> getAllCustomers(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(customerService.getAllCustomers(search, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CreateCustomerRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @PostMapping("/{id}/sites")
    public ResponseEntity<SiteResponse> createSite(
            @PathVariable Long id,
            @Valid @RequestBody CreateSiteRequest request) {
        SiteResponse response = siteService.createSite(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/sites")
    public ResponseEntity<List<SiteResponse>> getCustomerSites(@PathVariable Long id) {
        return ResponseEntity.ok(siteService.getSitesByCustomerId(id));
    }
}
