/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.foodordering.restaurant_service.repositories;

import com.foodordering.restaurant_service.models.MenuItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    Optional<MenuItem> findByItemId(String itemId);
    List<MenuItem> findByRestaurant_Id(Long restaurantId);
List<MenuItem> findByRestaurant_RestaurantId(String restaurantId);
}
