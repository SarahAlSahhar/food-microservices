/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodordering.delivery_service.dtos;

import com.foodordering.delivery_service.models.DeliveryStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryResponse {
    
    private String deliveryId;
    private String orderId;
    private String customerId;
    private String driverId;
    private String deliveryAddress;
    private String customerPhone;
    private DeliveryStatus status;
    private String estimatedDeliveryTime;
    private String actualDeliveryTime;
    private String deliveryNotes;
    private LocalDateTime createdAt;
}
