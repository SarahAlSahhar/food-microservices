/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodordering.payment_service.services;

import com.foodordering.payment_service.dto.*;
import com.foodordering.payment_service.models.*;
import com.foodordering.payment_service.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment for order: {}", request.getOrderId());
        
        Payment payment = Payment.builder()
                .paymentId(generatePaymentId())
                .orderId(request.getOrderId())
                .customerId(request.getCustomerId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.PROCESSING)
                .build();
        
        payment = paymentRepository.save(payment);
        
        boolean paymentSuccess = simulatePaymentProcessing(request);
        
        if (paymentSuccess) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setTransactionId(generateTransactionId());
            log.info("Payment successful for order: {}", request.getOrderId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setErrorMessage("Payment processing failed - insufficient funds or invalid card");
            log.error("Payment failed for order: {}", request.getOrderId());
        }
        
        payment = paymentRepository.save(payment);
        
        return convertToResponse(payment);
    }
    
    public PaymentResponse getPaymentById(String paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
        return convertToResponse(payment);
    }
    
    public List<PaymentResponse> getPaymentsByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<PaymentResponse> getPaymentsByCustomerId(String customerId) {
        return paymentRepository.findByCustomerId(customerId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public PaymentResponse refundPayment(String paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
        
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new RuntimeException("Only completed payments can be refunded");
        }
        
        payment.setStatus(PaymentStatus.REFUNDED);
        payment = paymentRepository.save(payment);
        
        log.info("Payment refunded: {}", paymentId);
        return convertToResponse(payment);
    }
    
    // Helper methods
    private boolean simulatePaymentProcessing(PaymentRequest request) {
        try {
            Thread.sleep(1000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
      
        return Math.random() > 0.1;
    }
    
    private String generatePaymentId() {
        return "PAY" + String.format("%08d", (int)(Math.random() * 100000000));
    }
    
    private String generateTransactionId() {
        return "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private PaymentResponse convertToResponse(Payment payment) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .customerId(payment.getCustomerId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .errorMessage(payment.getErrorMessage())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
