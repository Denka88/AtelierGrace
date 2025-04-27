package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.*;
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
    @Transactional
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

    @Override
    @Transactional
    public void takeOrder(Order order) {
        Employee currentEmployee = currentUserService.getCurrentEmployee()
                        .orElseThrow(() -> new IllegalArgumentException("Сотрудник не найден"));
        
        boolean alreadyTaken = order.getOrderEmployees().stream()
                .anyMatch(oe -> oe.getEmployee().getId().equals(currentEmployee.getId()));

        if (!alreadyTaken) {
            OrderEmployee orderEmployee = new OrderEmployee();
            
            orderEmployee.setOrder(order);
            orderEmployee.setEmployee(currentEmployee);

            orderEmployee.setDateOfReady(LocalDate.now().plusDays(14));
            OrderEmployeeKey key = new OrderEmployeeKey();
            key.setOrderId(order.getId());
            key.setEmployeeId(currentEmployee.getId());
            orderEmployee.setId(key);
            
            orderEmployeeService.save(orderEmployee);

            order.getOrderEmployees().add(orderEmployee);
            orderRepo.save(order);
        }
    }

    @Override
    @Transactional
    public void completeOrder(Long orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ не найден"));
        
        order.setStatus(Status.COMPLETED);
        orderRepo.save(order);
    }
}
