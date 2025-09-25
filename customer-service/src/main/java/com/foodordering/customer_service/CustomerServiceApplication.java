package com.foodordering.customer_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@SpringBootApplication
public class CustomerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerServiceApplication.class, args);
	}



@RestController
public class CustomerServiceController {
    
    @GetMapping("/test")
    public String test() {
        return "Hello World - Customer Service Working!";
    }
}}
