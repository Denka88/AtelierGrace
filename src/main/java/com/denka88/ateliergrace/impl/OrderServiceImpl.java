package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.Client;
import com.denka88.ateliergrace.model.Order;
import com.denka88.ateliergrace.model.Status;
import com.denka88.ateliergrace.repo.OrderRepo;
import com.denka88.ateliergrace.service.CurrentUserService;
import com.denka88.ateliergrace.service.OrderEmployeeService;
import com.denka88.ateliergrace.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepo orderRepo;
    private final CurrentUserService currentUserService;
    private final OrderEmployeeService orderEmployeeService;

    public OrderServiceImpl(OrderRepo orderRepo, CurrentUserService currentUserService, OrderEmployeeService orderEmployeeService) {
        this.orderRepo = orderRepo;
        this.currentUserService = currentUserService;
        this.orderEmployeeService = orderEmployeeService;
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
    @Transactional
    public Order save(Order order) {

        Client currentClient = currentUserService.getCurrentClient()
                .orElseThrow(() -> new IllegalStateException("Клиент не найден"));
        
        order.setClient(currentClient);
        order.setOrderDate(LocalDate.now());
        order.setStatus(Status.PROGRESS);

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
    public List<Order> findByClientId(Long clientId) {
        return orderRepo.findByClientId(clientId);
    }
}
