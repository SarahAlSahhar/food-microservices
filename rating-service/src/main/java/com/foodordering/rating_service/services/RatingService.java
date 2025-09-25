/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodordering.rating_service.services;

import com.foodordering.rating_service.dto.*;
import com.foodordering.rating_service.models.*;
import com.foodordering.rating_service.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingService {
    
    private final RatingRepository ratingRepository;
    private final RatingStatisticsRepository statisticsRepository;
    
    @Transactional
    public RatingResponse submitRating(RatingRequest request) {
        log.info("Submitting rating for order: {}", request.getOrderId());
        
        // Validate rating type and entity IDs
        validateRatingRequest(request);
        
        Rating rating = Rating.builder()
                .ratingId(generateRatingId())
                .customerId(request.getCustomerId())
                .orderId(request.getOrderId())
                .restaurantId(request.getRestaurantId())
                .driverId(request.getDriverId())
                .ratingType(request.getRatingType())
                .ratingValue(request.getRatingValue())
                .review(request.getReview())
                .feedback(request.getFeedback())
                .status(RatingStatus.ACTIVE)
                .build();
        
        rating = ratingRepository.save(rating);
        
        // Update statistics
        updateRatingStatistics(rating);
        
        log.info("Rating submitted successfully: {}", rating.getRatingId());
        return convertToResponse(rating);
    }
    
    public RatingResponse getRatingById(String ratingId) {
        Rating rating = ratingRepository.findByRatingId(ratingId)
                .orElseThrow(() -> new RuntimeException("Rating not found: " + ratingId));
        return convertToResponse(rating);
    }
    
    public List<RatingResponse> getRatingsByOrderId(String orderId) {
        return ratingRepository.findByOrderId(orderId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<RatingResponse> getRatingsByCustomerId(String customerId) {
        return ratingRepository.findByCustomerId(customerId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<RatingResponse> getRestaurantRatings(String restaurantId) {
        return ratingRepository.findByRestaurantIdAndRatingType(restaurantId, RatingType.RESTAURANT)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<RatingResponse> getDeliveryRatings(String driverId) {
        return ratingRepository.findByDriverIdAndRatingType(driverId, RatingType.DELIVERY)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public RatingStatisticsResponse getRestaurantStatistics(String restaurantId) {
        return statisticsRepository.findByEntityIdAndEntityType(restaurantId, RatingType.RESTAURANT)
                .map(this::convertToStatisticsResponse)
                .orElse(createEmptyStatistics(restaurantId, RatingType.RESTAURANT));
    }
    
    public RatingStatisticsResponse getDriverStatistics(String driverId) {
        return statisticsRepository.findByEntityIdAndEntityType(driverId, RatingType.DELIVERY)
                .map(this::convertToStatisticsResponse)
                .orElse(createEmptyStatistics(driverId, RatingType.DELIVERY));
    }
    
    private void validateRatingRequest(RatingRequest request) {
        if (request.getRatingType() == RatingType.RESTAURANT && 
            (request.getRestaurantId() == null || request.getRestaurantId().trim().isEmpty())) {
            throw new RuntimeException("Restaurant ID is required for restaurant ratings");
        }
        
        if (request.getRatingType() == RatingType.DELIVERY && 
            (request.getDriverId() == null || request.getDriverId().trim().isEmpty())) {
            throw new RuntimeException("Driver ID is required for delivery ratings");
        }
    }
    
    private void updateRatingStatistics(Rating rating) {
        String entityId = rating.getRatingType() == RatingType.RESTAURANT ? 
                         rating.getRestaurantId() : rating.getDriverId();
        
        if (entityId == null) return;
        
        RatingStatistics stats = statisticsRepository
                .findByEntityIdAndEntityType(entityId, rating.getRatingType())
                .orElse(RatingStatistics.builder()
                        .entityId(entityId)
                        .entityType(rating.getRatingType())
                        .totalRatings(0)
                        .fiveStars(0)
                        .fourStars(0)
                        .threeStars(0)
                        .twoStars(0)
                        .oneStar(0)
                        .build());
        
        // Update counts
        stats.setTotalRatings(stats.getTotalRatings() + 1);
        
        switch (rating.getRatingValue()) {
            case 5: stats.setFiveStars(stats.getFiveStars() + 1); break;
            case 4: stats.setFourStars(stats.getFourStars() + 1); break;
            case 3: stats.setThreeStars(stats.getThreeStars() + 1); break;
            case 2: stats.setTwoStars(stats.getTwoStars() + 1); break;
            case 1: stats.setOneStar(stats.getOneStar() + 1); break;
        }
        
        // Calculate average
        double totalPoints = (stats.getFiveStars() * 5) + (stats.getFourStars() * 4) + 
                           (stats.getThreeStars() * 3) + (stats.getTwoStars() * 2) + 
                           (stats.getOneStar() * 1);
        
        stats.setAverageRating(totalPoints / stats.getTotalRatings());
        
        statisticsRepository.save(stats);
    }
    
    private String generateRatingId() {
        return "RAT" + String.format("%08d", (int)(Math.random() * 100000000));
    }
    
    private RatingResponse convertToResponse(Rating rating) {
        return RatingResponse.builder()
                .ratingId(rating.getRatingId())
                .customerId(rating.getCustomerId())
                .orderId(rating.getOrderId())
                .restaurantId(rating.getRestaurantId())
                .driverId(rating.getDriverId())
                .ratingType(rating.getRatingType())
                .ratingValue(rating.getRatingValue())
                .review(rating.getReview())
                .feedback(rating.getFeedback())
                .status(rating.getStatus())
                .createdAt(rating.getCreatedAt())
                .build();
    }
    
    private RatingStatisticsResponse convertToStatisticsResponse(RatingStatistics stats) {
        return RatingStatisticsResponse.builder()
                .entityId(stats.getEntityId())
                .entityType(stats.getEntityType())
                .averageRating(stats.getAverageRating())
                .totalRatings(stats.getTotalRatings())
                .fiveStars(stats.getFiveStars())
                .fourStars(stats.getFourStars())
                .threeStars(stats.getThreeStars())
                .twoStars(stats.getTwoStars())
                .oneStar(stats.getOneStar())
                .build();
    }
    
    private RatingStatisticsResponse createEmptyStatistics(String entityId, RatingType entityType) {
        return RatingStatisticsResponse.builder()
                .entityId(entityId)
                .entityType(entityType)
                .averageRating(0.0)
                .totalRatings(0)
                .fiveStars(0)
                .fourStars(0)
                .threeStars(0)
                .twoStars(0)
                .oneStar(0)
                .build();
    }
}