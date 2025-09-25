/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodordering.customer_service.dto;

import lombok.*;
import com.foodordering.customer_service.models.CustomerStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private String customerId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private CustomerStatus status;
}
