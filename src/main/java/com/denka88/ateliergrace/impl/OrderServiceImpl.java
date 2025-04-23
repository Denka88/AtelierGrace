package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.Order;
import com.denka88.ateliergrace.repo.OrderRepo;
import com.denka88.ateliergrace.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepo orderRepo;

    public OrderServiceImpl(OrderRepo orderRepo) {
        this.orderRepo = orderRepo;
    }

    @Override
    public List<Order> findAll() {
        return orderRepo.findAllWithEmployeesAndMaterials();
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepo.findById(id);
    }

    @Override
    public Order save(Order order) {
        return orderRepo.save(order);
    }

    @Override
    public void delete(Long id) {
        orderRepo.deleteById(id);
    }

    @Override
    public void update(Order order) {
        orderRepo.save(order);
    }

    @Override
    public Optional<Order> findByClientId(Long clientId) {
        return orderRepo.findByClientId(clientId);
    }
}
