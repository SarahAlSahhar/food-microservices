/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodordering.rating_service.repositories;

import com.foodordering.rating_service.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    
    Optional<Rating> findByRatingId(String ratingId);
    
    List<Rating> findByCustomerId(String customerId);
    
    List<Rating> findByOrderId(String orderId);
    
    List<Rating> findByRestaurantIdAndRatingType(String restaurantId, RatingType ratingType);
    
    List<Rating> findByDriverIdAndRatingType(String driverId, RatingType ratingType);
    
    List<Rating> findByRatingType(RatingType ratingType);
    
    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.restaurantId = :restaurantId AND r.ratingType = :ratingType AND r.status = 'ACTIVE'")
    Double findAverageRatingByRestaurantId(@Param("restaurantId") String restaurantId, @Param("ratingType") RatingType ratingType);
    
    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.driverId = :driverId AND r.ratingType = :ratingType AND r.status = 'ACTIVE'")
    Double findAverageRatingByDriverId(@Param("driverId") String driverId, @Param("ratingType") RatingType ratingType);
    
    @Query("SELECT COUNT(r) FROM Rating r WHERE r.restaurantId = :restaurantId AND r.ratingType = :ratingType AND r.status = 'ACTIVE'")
    Long countByRestaurantId(@Param("restaurantId") String restaurantId, @Param("ratingType") RatingType ratingType);
}
