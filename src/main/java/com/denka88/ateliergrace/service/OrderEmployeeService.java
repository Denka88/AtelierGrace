package com.denka88.ateliergrace.service;

import com.denka88.ateliergrace.model.OrderEmployee;
import com.denka88.ateliergrace.model.OrderEmployeeKey;

import java.util.List;
import java.util.Optional;

public interface OrderEmployeeService {
    
    List<OrderEmployee> findAll();
    
    OrderEmployee save(OrderEmployee orderEmployee);
    
    void update(OrderEmployee orderEmployee);
    
}
