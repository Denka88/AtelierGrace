package com.denka88.ateliergrace.service;

import com.denka88.ateliergrace.model.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    
    List<Employee> findAll();
    
    Optional<Employee> findById(Long id);
    
    Employee save(Employee employee);
    
    void delete(Long id);
    
    void update(Employee employee);

    List<Employee> findAllOrderByInProgressOrdersCount();
    
    
}
