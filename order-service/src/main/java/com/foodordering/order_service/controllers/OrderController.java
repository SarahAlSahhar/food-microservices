/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodordering.order_service.controllers;

import com.foodordering.order_service.dto.*;
import com.foodordering.order_service.models.OrderStatus;
import com.foodordering.order_service.services.OrderService;
import com.foodordering.order_service.valueobjects.OrderWithDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/orders")
@Validated
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String orderId) {
        OrderResponse order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PutMapping("/{orderId}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(@PathVariable String orderId) {
        OrderResponse order = orderService.confirmOrder(orderId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{orderId}/details")
    public ResponseEntity<OrderWithDetails> getOrderWithDetails(@PathVariable String orderId) {
        OrderWithDetails orderDetails = orderService.getOrderWithDetails(orderId);
        return ResponseEntity.ok(orderDetails);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam OrderStatus status) {
        OrderResponse order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(order);
    }
}

//@GetMapping("/by-restaurant-id/{restaurantId}")
//public ResponseEntity<Restaurant> getByRestaurantId(@PathVariable String restaurantId) {
//   Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId)
//            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));
//    return ResponseEntity.ok(restaurant);
//}
