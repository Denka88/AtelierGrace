package com.denka88.ateliergrace.service;

import com.denka88.ateliergrace.model.Employee;
import com.denka88.ateliergrace.model.OrderEmployee;
import com.denka88.ateliergrace.model.OrderEmployeeKey;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderEmployeeService {
    
    List<OrderEmployee> findAll();
    
    OrderEmployee save(OrderEmployee orderEmployee);
    
    void update(OrderEmployee orderEmployee);
    
    void setReadyDate(Long orderId, Long employeeId, LocalDate readyDate);

    void createAssignment(Long orderId, Long employeeId, LocalDate readyDate);
    
    Long getInProgressCount(Long employeeId);
}
