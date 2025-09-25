/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodordering.rating_service.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String ratingId;
    
    @Column(nullable = false)
    private String customerId;
    
    @Column(nullable = false)
    private String orderId;
    
    private String restaurantId;
    
    private String driverId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RatingType ratingType;
    
    @Column(nullable = false)
    private Integer ratingValue; // 1-5 stars
    
    @Column(length = 1000)
    private String review;
    
    @Column(length = 500)
    private String feedback;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RatingStatus status = RatingStatus.ACTIVE;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
