/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodordering.delivery_service.services;

import com.foodordering.delivery_service.dtos.*;
import com.foodordering.delivery_service.models.*;
import com.foodordering.delivery_service.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {
    
    private final DeliveryRepository deliveryRepository;
    private final DriverRepository driverRepository;
    
    @Transactional
    public DeliveryResponse createDelivery(DeliveryRequest request) {
        log.info("Creating delivery for order: {}", request.getOrderId());
        
        Delivery delivery = Delivery.builder()
                .deliveryId(generateDeliveryId())
                .orderId(request.getOrderId())
                .customerId(request.getCustomerId())
                .deliveryAddress(request.getDeliveryAddress())
                .customerPhone(request.getCustomerPhone())
                .deliveryNotes(request.getDeliveryNotes())
                .status(DeliveryStatus.PENDING)
                .estimatedDeliveryTime(calculateEstimatedTime())
                .build();
        
        delivery = deliveryRepository.save(delivery);
        
        log.info("Delivery created: {}", delivery.getDeliveryId());
        return convertToResponse(delivery);
    }
    
    @Transactional
    public DeliveryResponse assignDriver(String deliveryId) {
        Delivery delivery = deliveryRepository.findByDeliveryId(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found: " + deliveryId));
        
        if (delivery.getStatus() != DeliveryStatus.PENDING) {
            throw new RuntimeException("Delivery already assigned or completed");
        }
        
        Driver driver = findAvailableDriver()
                .orElseThrow(() -> new RuntimeException("No available drivers"));
        
        delivery.setDriverId(driver.getDriverId());
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        delivery.setAssignedAt(LocalDateTime.now());
        
        driver.setStatus(DriverStatus.BUSY);
        driverRepository.save(driver);
        
        delivery = deliveryRepository.save(delivery);
        
        log.info("Driver {} assigned to delivery {}", driver.getDriverId(), deliveryId);
        return convertToResponse(delivery);
    }
    
    @Transactional
    public DeliveryResponse updateDeliveryStatus(String deliveryId, DeliveryStatus newStatus) {
        Delivery delivery = deliveryRepository.findByDeliveryId(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found: " + deliveryId));
        
        delivery.setStatus(newStatus);
        
        if (newStatus == DeliveryStatus.PICKED_UP) {
            delivery.setPickedUpAt(LocalDateTime.now());
        } else if (newStatus == DeliveryStatus.DELIVERED) {
            delivery.setDeliveredAt(LocalDateTime.now());
            delivery.setActualDeliveryTime(LocalDateTime.now().toString());
            
            if (delivery.getDriverId() != null) {
                driverRepository.findByDriverId(delivery.getDriverId())
                        .ifPresent(driver -> {
                            driver.setStatus(DriverStatus.AVAILABLE);
                            driver.setTotalDeliveries((driver.getTotalDeliveries() != null ? driver.getTotalDeliveries() : 0) + 1);
                            driverRepository.save(driver);
                        });
            }
        }
        
        delivery = deliveryRepository.save(delivery);
        
        log.info("Delivery {} status updated to {}", deliveryId, newStatus);
        return convertToResponse(delivery);
    }
    
    public DeliveryResponse getDeliveryById(String deliveryId) {
        Delivery delivery = deliveryRepository.findByDeliveryId(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found: " + deliveryId));
        return convertToResponse(delivery);
    }
    
    public List<DeliveryResponse> getDeliveriesByOrderId(String orderId) {
        return deliveryRepository.findByOrderId(orderId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<DeliveryResponse> getDeliveriesByCustomerId(String customerId) {
        return deliveryRepository.findByCustomerId(customerId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<DriverResponse> getAllDrivers() {
        return driverRepository.findAll().stream()
                .map(this::convertToDriverResponse)
                .collect(Collectors.toList());
    }
    
    public List<DriverResponse> getAvailableDrivers() {
        return driverRepository.findByStatus(DriverStatus.AVAILABLE).stream()
                .map(this::convertToDriverResponse)
                .collect(Collectors.toList());
    }
    
    private Optional<Driver> findAvailableDriver() {
        List<Driver> availableDrivers = driverRepository.findByStatus(DriverStatus.AVAILABLE);
        return availableDrivers.isEmpty() ? Optional.empty() : Optional.of(availableDrivers.get(0));
    }
    
    private String generateDeliveryId() {
        return "DEL" + String.format("%08d", (int)(Math.random() * 100000000));
    }
    
    private String calculateEstimatedTime() {
        return LocalDateTime.now().plusMinutes(30).toString();
    }
    
    private DeliveryResponse convertToResponse(Delivery delivery) {
        return DeliveryResponse.builder()
                .deliveryId(delivery.getDeliveryId())
                .orderId(delivery.getOrderId())
                .customerId(delivery.getCustomerId())
                .driverId(delivery.getDriverId())
                .deliveryAddress(delivery.getDeliveryAddress())
                .customerPhone(delivery.getCustomerPhone())
                .status(delivery.getStatus())
                .estimatedDeliveryTime(delivery.getEstimatedDeliveryTime())
                .actualDeliveryTime(delivery.getActualDeliveryTime())
                .deliveryNotes(delivery.getDeliveryNotes())
                .createdAt(delivery.getCreatedAt())
                .build();
    }
    
    private DriverResponse convertToDriverResponse(Driver driver) {
        return DriverResponse.builder()
                .driverId(driver.getDriverId())
                .name(driver.getName())
                .phone(driver.getPhone())
                .vehicleType(driver.getVehicleType())
                .vehicleNumber(driver.getVehicleNumber())
                .status(driver.getStatus())
                .rating(driver.getRating())
                .totalDeliveries(driver.getTotalDeliveries())
                .build();
    }
}
