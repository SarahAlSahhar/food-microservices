/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodordering.delivery_service.controllers;

import com.foodordering.delivery_service.dtos.*;
import com.foodordering.delivery_service.dtos.DeliveryRequest;
import com.foodordering.delivery_service.models.DeliveryStatus;
import com.foodordering.delivery_service.services.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DeliveryController {
    
    private final DeliveryService deliveryService;
    
    @PostMapping
    public ResponseEntity<DeliveryResponse> createDelivery(@Valid @RequestBody DeliveryRequest request) {
        DeliveryResponse response = deliveryService.createDelivery(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PostMapping("/{deliveryId}/assign")
    public ResponseEntity<DeliveryResponse> assignDriver(@PathVariable String deliveryId) {
        DeliveryResponse response = deliveryService.assignDriver(deliveryId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{deliveryId}/status")
    public ResponseEntity<DeliveryResponse> updateStatus(
            @PathVariable String deliveryId,
            @RequestParam DeliveryStatus status) {
        DeliveryResponse response = deliveryService.updateDeliveryStatus(deliveryId, status);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{deliveryId}")
    public ResponseEntity<DeliveryResponse> getDelivery(@PathVariable String deliveryId) {
        DeliveryResponse response = deliveryService.getDeliveryById(deliveryId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesByOrder(@PathVariable String orderId) {
        List<DeliveryResponse> deliveries = deliveryService.getDeliveriesByOrderId(orderId);
        return ResponseEntity.ok(deliveries);
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesByCustomer(@PathVariable String customerId) {
        List<DeliveryResponse> deliveries = deliveryService.getDeliveriesByCustomerId(customerId);
        return ResponseEntity.ok(deliveries);
    }
    
    @GetMapping("/drivers")
    public ResponseEntity<List<DriverResponse>> getAllDrivers() {
        List<DriverResponse> drivers = deliveryService.getAllDrivers();
        return ResponseEntity.ok(drivers);
    }
    
    @GetMapping("/drivers/available")
    public ResponseEntity<List<DriverResponse>> getAvailableDrivers() {
        List<DriverResponse> drivers = deliveryService.getAvailableDrivers();
        return ResponseEntity.ok(drivers);
    }
}