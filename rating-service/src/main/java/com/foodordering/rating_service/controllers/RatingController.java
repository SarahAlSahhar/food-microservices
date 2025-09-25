/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodordering.rating_service.controllers;

import com.foodordering.rating_service.dto.*;
import com.foodordering.rating_service.services.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/ratings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RatingController {
    
    private final RatingService ratingService;
    
    @PostMapping
    public ResponseEntity<RatingResponse> submitRating(@Valid @RequestBody RatingRequest request) {
        RatingResponse response = ratingService.submitRating(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/{ratingId}")
    public ResponseEntity<RatingResponse> getRating(@PathVariable String ratingId) {
        RatingResponse response = ratingService.getRatingById(ratingId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<RatingResponse>> getRatingsByOrder(@PathVariable String orderId) {
        List<RatingResponse> ratings = ratingService.getRatingsByOrderId(orderId);
        return ResponseEntity.ok(ratings);
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<RatingResponse>> getRatingsByCustomer(@PathVariable String customerId) {
        List<RatingResponse> ratings = ratingService.getRatingsByCustomerId(customerId);
        return ResponseEntity.ok(ratings);
    }
    
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<RatingResponse>> getRestaurantRatings(@PathVariable String restaurantId) {
        List<RatingResponse> ratings = ratingService.getRestaurantRatings(restaurantId);
        return ResponseEntity.ok(ratings);
    }
    
    @GetMapping("/delivery/{driverId}")
    public ResponseEntity<List<RatingResponse>> getDeliveryRatings(@PathVariable String driverId) {
        List<RatingResponse> ratings = ratingService.getDeliveryRatings(driverId);
        return ResponseEntity.ok(ratings);
    }
    
    @GetMapping("/restaurant/{restaurantId}/statistics")
    public ResponseEntity<RatingStatisticsResponse> getRestaurantStatistics(@PathVariable String restaurantId) {
        RatingStatisticsResponse stats = ratingService.getRestaurantStatistics(restaurantId);
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/driver/{driverId}/statistics")
    public ResponseEntity<RatingStatisticsResponse> getDriverStatistics(@PathVariable String driverId) {
        RatingStatisticsResponse stats = ratingService.getDriverStatistics(driverId);
        return ResponseEntity.ok(stats);
    }
}
