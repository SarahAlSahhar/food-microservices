/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodordering.customer_service.services;

import com.foodordering.customer_service.models.Customer;
import com.foodordering.customer_service.models.CustomerStatus;
import com.foodordering.customer_service.repositories.CustomerRepository;
import com.foodordering.customer_service.dto.*;
import com.foodordering.customer_service.exceptions.DuplicateEmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return convertToResponse(customer);
    }
    
    public CustomerResponse getCustomerByCustomerId(String customerId) {
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return convertToResponse(customer);
    }
    
    public CustomerResponse registerCustomer(CustomerRequest request) {
        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email already exists: " + request.getEmail());
        }
        
        Customer customer = new Customer();
        customer.setCustomerId(generateCustomerId()); 
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setStatus(CustomerStatus.ACTIVE);
        
        Customer saved = customerRepository.save(customer);
        return convertToResponse(saved);
    }
    
    private String generateCustomerId() {
        return "CUST" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
    
    private CustomerResponse convertToResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setCustomerId(customer.getCustomerId());
        response.setName(customer.getName());
        response.setEmail(customer.getEmail());
        response.setPhone(customer.getPhone());
        response.setAddress(customer.getAddress());
        response.setStatus(customer.getStatus());
        return response;
    }
}
