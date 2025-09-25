/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodordering.rating_service.dto;

import com.foodordering.rating_service.models.RatingType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingRequest {
    
    @NotBlank(message = "Customer ID is required")
    private String customerId;
    
    @NotBlank(message = "Order ID is required")
    private String orderId;
    
    private String restaurantId;
    
    private String driverId;
    
    @NotNull(message = "Rating type is required")
    private RatingType ratingType;
    
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    @NotNull(message = "Rating value is required")
    private Integer ratingValue;
    
    @Size(max = 1000, message = "Review cannot exceed 1000 characters")
    private String review;
    
    @Size(max = 500, message = "Feedback cannot exceed 500 characters")
    private String feedback;
}
