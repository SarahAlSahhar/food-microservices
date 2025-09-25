/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodordering.order_service.valueobjects;

import lombok.*;
import com.foodordering.order_service.models.Order;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderWithDetails {
    private Order order;
    private Customer customer;
    private Restaurant restaurant;
}