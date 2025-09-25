/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodordering.delivery_service.dtos;

import com.foodordering.delivery_service.models.DriverStatus;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverResponse {
    
    private String driverId;
    private String name;
    private String phone;
    private String vehicleType;
    private String vehicleNumber;
    private DriverStatus status;
    private Double rating;
    private Integer totalDeliveries;
}
