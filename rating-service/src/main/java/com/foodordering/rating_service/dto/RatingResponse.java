/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodordering.rating_service.dto;

import com.foodordering.rating_service.models.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingResponse {
    
    private String ratingId;
    private String customerId;
    private String orderId;
    private String restaurantId;
    private String driverId;
    private RatingType ratingType;
    private Integer ratingValue;
    private String review;
    private String feedback;
    private RatingStatus status;
    private LocalDateTime createdAt;
}
