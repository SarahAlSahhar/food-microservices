package com.foodordering.order_service.services;

import com.foodordering.order_service.models.*;
import com.foodordering.order_service.repositories.*;
import com.foodordering.order_service.dto.*;
import com.foodordering.order_service.valueobjects.*;
import com.foodordering.order_service.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public OrderResponse getOrderById(String orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        return convertToResponse(order);
    }
    
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // Validate customer
        Customer customer;
        try {
            customer = restTemplate.getForObject(
                "http://localhost:8081/customers/by-customer-id/" + request.getCustomerId(), 
                Customer.class
            );
        } catch (RestClientException ex) {
            throw new CustomerNotFoundException("Customer service unavailable or customer not found: " + request.getCustomerId());
        }
        
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found with ID: " + request.getCustomerId());
        }
        
        Restaurant restaurant;
        try {
            restaurant = restTemplate.getForObject(
                "http://localhost:8082/restaurants/by-restaurant-id/" + request.getRestaurantId(), 
                Restaurant.class
            );
        } catch (RestClientException ex) {
            throw new RestaurantNotFoundException("Restaurant service unavailable or restaurant not found: " + request.getRestaurantId());
        }
        
        if (restaurant == null) {
            throw new RestaurantNotFoundException("Restaurant not found with ID: " + request.getRestaurantId());
        }
        
        // Create order
        Order order = new Order();
        order.setOrderId(generateOrderId());
        order.setCustomerId(request.getCustomerId());
        order.setRestaurantId(request.getRestaurantId());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setStatus(OrderStatus.PENDING);
        
        // Convert and validate items
        List<OrderItem> orderItems = request.getItems().stream()
                .map(item -> {
                    OrderItem orderItem = new OrderItem(item.getItemId(), item.getQuantity());
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toList());
        
        order.setItems(orderItems);
        
        // Calculate total
        BigDecimal total = calculateOrderTotal(request.getItems());
        order.setTotal(total);
        
        Order savedOrder = orderRepository.save(order);
       
        publishOrderPlacedEvent(savedOrder);
        
        requestPaymentProcessing(savedOrder, customer);
        
        return convertToResponse(savedOrder);
    }
        private void requestPaymentProcessing(Order order, Customer customer) {
        try {
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setOrderId(order.getOrderId());
            paymentRequest.setCustomerId(order.getCustomerId());
            paymentRequest.setAmount(order.getTotal());
            paymentRequest.setPaymentMethod(order.getPaymentMethod()); // ãØÇÈÞ ááÜ Payment Service
            paymentRequest.setCardNumber("****-****-****-1234");
            paymentRequest.setCardHolderName(customer.getName());
            paymentRequest.setExpiryDate("12/26");
            paymentRequest.setCvv("123");
            
            // Asynchronous call to Payment Service
            restTemplate.postForObject(
                "http://localhost:8084/payments", 
                paymentRequest, 
                String.class
            );
            
            System.out.println("Payment request sent for order: " + order.getOrderId());
        } catch (Exception ex) {
            System.err.println("Failed to send payment request: " + ex.getMessage());
        }
    }
    
    @Transactional
    public OrderResponse confirmOrder(String orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        order.setStatus(OrderStatus.CONFIRMED);
        Order updatedOrder = orderRepository.save(order);
        
        Customer customer;
        try {
            customer = restTemplate.getForObject(
                "http://localhost:8081/customers/by-customer-id/" + order.getCustomerId(), 
                Customer.class
            );
        } catch (RestClientException ex) {
            customer = null; // fallback
        }
        
        requestDeliveryAssignment(updatedOrder, customer);
        
        publishOrderConfirmedEvent(updatedOrder);
        
        return convertToResponse(updatedOrder);
    }
    
    private void requestDeliveryAssignment(Order order, Customer customer) {
        try {
            DeliveryRequest deliveryRequest = new DeliveryRequest();
            deliveryRequest.setOrderId(order.getOrderId());
            deliveryRequest.setCustomerId(order.getCustomerId());
            deliveryRequest.setDeliveryAddress(customer != null ? customer.getAddress() : "Al-Rimal, Gaza City");
            deliveryRequest.setCustomerPhone(customer != null ? customer.getPhone() : "+970-599-000000");
            deliveryRequest.setDeliveryNotes("Please call upon arrival");
            
            // Asynchronous call to Delivery Service
            restTemplate.postForObject(
                "http://localhost:8085/deliveries", 
                deliveryRequest, 
                String.class
            );
            
            System.out.println("Delivery request sent for order: " + order.getOrderId());
        } catch (Exception ex) {
            System.err.println("Failed to send delivery request: " + ex.getMessage());
        }
    }
    
    public OrderWithDetails getOrderWithDetails(String orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        Customer customer = null;
        try {
            customer = restTemplate.getForObject(
                "http://localhost:8081/customers/by-customer-id/" + order.getCustomerId(), 
                Customer.class
            );
        } catch (RestClientException ex) {
        }
        
        Restaurant restaurant = null;
        try {
            restaurant = restTemplate.getForObject(
                "http://localhost:8082/restaurants/by-restaurant-id/" + order.getRestaurantId(), 
                Restaurant.class
            );
        } catch (RestClientException ex) {
        }
        
        OrderWithDetails result = new OrderWithDetails();
        result.setOrder(order);
        result.setCustomer(customer);
        result.setRestaurant(restaurant);
        
        return result;
    }
    
    public OrderResponse updateOrderStatus(String orderId, OrderStatus status) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        
        return convertToResponse(updatedOrder);
    }
    
    private String generateOrderId() {
        return "ORD" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
    
    private BigDecimal calculateOrderTotal(List<OrderItemRequest> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest item : items) {
            BigDecimal itemPrice = new BigDecimal("25.00"); 
            total = total.add(itemPrice.multiply(new BigDecimal(item.getQuantity())));
        }
        return total;
    }
    
    private void publishOrderPlacedEvent(Order order) {
        System.out.println("EVENT: OrderPlaced - " + order.getOrderId() + 
                          " ? Payment Service, Restaurant Service, Customer Service");
    }
    
    private void publishOrderConfirmedEvent(Order order) {
        System.out.println("EVENT: OrderConfirmed - " + order.getOrderId() + 
                          " ? Delivery Service, Customer Service");
    }
    
    private OrderResponse convertToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderId());
        response.setCustomerId(order.getCustomerId());
        response.setRestaurantId(order.getRestaurantId());
        response.setTotal(order.getTotal());
        response.setStatus(order.getStatus());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setCreatedAt(order.getCreatedAt());
        
        if (order.getItems() != null) {
            List<OrderItemResponse> itemResponses = order.getItems().stream()
                    .map(item -> new OrderItemResponse(item.getItemId(), item.getQuantity()))
                    .collect(Collectors.toList());
            response.setItems(itemResponses);
        }
        
        return response;
    }
}