/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodservice.order_service.valueobjects;


import com.foodservice.order_service.models.Order;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseTemplate {
    private Order order;
    private Customer customer;
    
    
    public void setOrder(Order order) {
        this.order = order;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public Order getOrder() {
        return order;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
}
