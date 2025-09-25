/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.foodordering.delivery_service.repositories;

import com.foodordering.delivery_service.models.Delivery;
import com.foodordering.delivery_service.models.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByDeliveryId(String deliveryId);
    List<Delivery> findByOrderId(String orderId);
    List<Delivery> findByCustomerId(String customerId);
    List<Delivery> findByDriverId(String driverId);
    List<Delivery> findByStatus(DeliveryStatus status);
}
