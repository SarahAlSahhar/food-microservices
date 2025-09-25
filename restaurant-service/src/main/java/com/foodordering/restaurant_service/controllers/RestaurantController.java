/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodordering.restaurant_service.controllers;

import com.foodordering.restaurant_service.models.MenuItem;
import com.foodordering.restaurant_service.models.Restaurant;
import com.foodordering.restaurant_service.repositories.MenuItemRepository;
import com.foodordering.restaurant_service.repositories.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    // GET all restaurants
    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantRepository.findAll());
    }

    // GET restaurant by DB id (Long)
    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));
        return ResponseEntity.ok(restaurant);
    }

    // (Optional) GET restaurant by business id (String), e.g. REST001
    @GetMapping("/by-business/{restaurantId}")
    public ResponseEntity<Restaurant> getByBusinessId(@PathVariable String restaurantId) {
        Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));
        return ResponseEntity.ok(restaurant);
    }

    // POST create restaurant (wires back-references if menuItems provided)
    @PostMapping("/add")
    public ResponseEntity<Restaurant> addRestaurant(@RequestBody Restaurant restaurant) {
        if (restaurant.getMenuItems() != null) {
            restaurant.getMenuItems().forEach(mi -> {
                mi.setId(null); // ensure create
                if (mi.getAvailability() == null) {
                    mi.setAvailability(true);
                }
                mi.setRestaurant(restaurant); // IMPORTANT: own side
            });
        }
        Restaurant saved = restaurantRepository.save(restaurant);
        return ResponseEntity.created(URI.create("/restaurants/" + saved.getId())).body(saved);
    }

    // POST create one menu item under a restaurant (by DB id)
    @PostMapping("/{restaurantId}/menu/add")
    public ResponseEntity<MenuItem> addMenuItem(@PathVariable Long restaurantId,
            @RequestBody MenuItem menuItem) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));

        menuItem.setId(null);
        if (menuItem.getAvailability() == null) {
            menuItem.setAvailability(true);
        }
        menuItem.setRestaurant(restaurant);

        MenuItem saved = menuItemRepository.save(menuItem);
        return ResponseEntity.created(URI.create("/restaurants/" + restaurantId + "/menu/" + saved.getId()))
                .body(saved);
    }

    // GET menu items for a restaurant (by DB id) – avoids lazy issues using repository method
    @GetMapping("/{restaurantId}/menu")
    public ResponseEntity<List<MenuItem>> getRestaurantMenu(@PathVariable Long restaurantId) {
        restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));
        List<MenuItem> items = menuItemRepository.findByRestaurant_Id(restaurantId);
        return ResponseEntity.ok(items);
    }

    // (Optional) GET menu by business id, e.g. /restaurants/REST001/menu
    @GetMapping("/business/{restaurantBusinessId}/menu")
    public ResponseEntity<List<MenuItem>> getMenuByBusinessId(@PathVariable String restaurantBusinessId) {
        restaurantRepository.findByRestaurantId(restaurantBusinessId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));
        List<MenuItem> items = menuItemRepository.findByRestaurant_RestaurantId(restaurantBusinessId);
        return ResponseEntity.ok(items);
    }

    // Seed fixed test data quickly
    @PostMapping("/init")
    public ResponseEntity<String> initTestData() {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantId("REST001");
        restaurant.setName("Italiano");
        restaurant.setLocation("Gaza City");
        restaurant.setRating(4.5);

        MenuItem item = new MenuItem();
        item.setItemId("ITEM01");
        item.setName("Margherita Pizza");
        item.setPrice(new BigDecimal("30.00"));
        item.setAvailability(true);
        item.setRestaurant(restaurant);

        restaurant.setMenuItems(List.of(item));
        restaurantRepository.save(restaurant);

        return new ResponseEntity<>("Test data created!", HttpStatus.CREATED);
    }

    @GetMapping("/by-restaurant-id/{restaurantId}")
    public ResponseEntity<Restaurant> getByRestaurantId(@PathVariable String restaurantId) {
        Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));
        return ResponseEntity.ok(restaurant);
    }
}