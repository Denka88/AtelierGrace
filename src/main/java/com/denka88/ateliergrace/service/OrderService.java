package com.denka88.ateliergrace.service;

import com.denka88.ateliergrace.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    
    List<Order> findAll();
    
    Optional<Order> findById(Long id);
    
    Order save(Order order);
    
    void delete(Long id);
    
    void update(Order order);
    
    List<Order> findByClientId(Long clientId);

    void completeOrder(Long orderId);
}
