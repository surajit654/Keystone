package com.KEYSTONE.service;

import com.KEYSTONE.dto.CreateSiteRequest;
import com.KEYSTONE.dto.SiteResponse;
import com.KEYSTONE.model.Customer;
import com.KEYSTONE.model.Site;
import com.KEYSTONE.repository.CustomerRepository;
import com.KEYSTONE.repository.SiteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SiteService {

    private final SiteRepository siteRepository;
    private final CustomerRepository customerRepository;

    public SiteService(SiteRepository siteRepository, CustomerRepository customerRepository) {
        this.siteRepository = siteRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public SiteResponse createSite(Long customerId, CreateSiteRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));

        Site site = new Site(
                request.getName(),
                request.getAddress(),
                request.getCity(),
                customer
        );

        Site saved = siteRepository.save(site);
        return SiteResponse.fromEntity(saved);
    }

    public List<SiteResponse> getSitesByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new IllegalArgumentException("Customer not found with ID: " + customerId);
        }
        return siteRepository.findByCustomerId(customerId)
                .stream()
                .map(SiteResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public SiteResponse getSiteById(Long siteId) {
        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new IllegalArgumentException("Site not found with ID: " + siteId));
        return SiteResponse.fromEntity(site);
    }
}
