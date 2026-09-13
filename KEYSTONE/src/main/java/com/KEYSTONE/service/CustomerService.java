package com.KEYSTONE.service;

import com.KEYSTONE.dto.CreateCustomerRequest;
import com.KEYSTONE.dto.CustomerResponse;
import com.KEYSTONE.model.Customer;
import com.KEYSTONE.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            customerRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
                throw new IllegalArgumentException("Customer with email " + request.getEmail() + " already exists");
            });
        }

        Customer customer = new Customer(
                request.getName(),
                request.getEmail(),
                request.getPhone()
        );

        Customer saved = customerRepository.save(customer);
        return CustomerResponse.fromEntity(saved);
    }

    public Page<CustomerResponse> getAllCustomers(String search, Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            String term = search.trim();
            return customerRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(term, term, pageable)
                    .map(CustomerResponse::fromEntity);
        }
        return customerRepository.findAll(pageable)
                .map(CustomerResponse::fromEntity);
    }

    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));
        return CustomerResponse.fromEntity(customer);
    }

    @Transactional
    public CustomerResponse updateCustomer(Long id, CreateCustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + id));

        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());

        Customer updated = customerRepository.save(customer);
        return CustomerResponse.fromEntity(updated);
    }
}
