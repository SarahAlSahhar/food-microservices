/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.foodordering.rating_service.repositories;

import com.foodordering.rating_service.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RatingStatisticsRepository extends JpaRepository<RatingStatistics, Long> {
    
    Optional<RatingStatistics> findByEntityIdAndEntityType(String entityId, RatingType entityType);
}
