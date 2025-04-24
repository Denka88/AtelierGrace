package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.OrderEmployee;
import com.denka88.ateliergrace.repo.OrderEmployeeRepo;
import com.denka88.ateliergrace.service.OrderEmployeeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderEmployeeServiceImpl implements OrderEmployeeService {
    
    private final OrderEmployeeRepo orderEmployeeRepo;

    public OrderEmployeeServiceImpl(OrderEmployeeRepo orderEmployeeRepo) {
        this.orderEmployeeRepo = orderEmployeeRepo;
    }

    @Override
    public List<OrderEmployee> findAll() {
        return orderEmployeeRepo.findAll();
    }

    @Override
    public OrderEmployee save(OrderEmployee orderEmployee) {
        return orderEmployeeRepo.save(orderEmployee);
    }

    @Override
    public void update(OrderEmployee orderEmployee) {
        orderEmployeeRepo.save(orderEmployee);
    }

    @Override
    public OrderEmployee findFree() {
        return orderEmployeeRepo.findByEmployee_OrdersEmpty();
    }
}
