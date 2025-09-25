/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodordering.order_service.valueobjects;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {
    private String restaurantId;
    private String name;
    private String location;
    private Double rating;
}