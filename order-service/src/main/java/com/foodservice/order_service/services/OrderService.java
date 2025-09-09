package com.foodservice.order_service.services;

import com.foodservice.order_service.models.Order;
import com.foodservice.order_service.repositories.OrderRepository;
import com.foodservice.order_service.valueobjects.Customer;
import com.foodservice.order_service.valueobjects.ResponseTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).get();
    }
    
    public Order addOrder(Order order) {
        return orderRepository.save(order);
    }
    
    public ResponseTemplate getOrderWithCustomer(Long orderId) {
        Order order = orderRepository.findById(orderId).get();
        
        Customer customer = restTemplate.getForObject(
            "http://localhost:8082/customers/" + order.getCustomerId(), 
            Customer.class
        );
        
        ResponseTemplate vo = new ResponseTemplate();
        vo.setOrder(order);
        vo.setCustomer(customer);
        
        return vo;
    }
}