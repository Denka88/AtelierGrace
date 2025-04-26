package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.Employee;
import com.denka88.ateliergrace.repo.EmployeeRepo;
import com.denka88.ateliergrace.service.AuthService;
import com.denka88.ateliergrace.service.EmployeeService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    
    private final EmployeeRepo employeeRepo;
    private final AuthService authService;

    public EmployeeServiceImpl(EmployeeRepo employeeRepo, AuthService authService) {
        this.employeeRepo = employeeRepo;
        this.authService = authService;
    }

    @Override
    public List<Employee> findAll() {
        return employeeRepo.findAll();
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return employeeRepo.findById(id);
    }

    @Override
    public Employee save(Employee employee) {
        return employeeRepo.save(employee);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Employee employee = employeeRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Сотрудник не найден"));

        authService.delete(employee.getId());
        
        employee.getOrders().clear();
        employeeRepo.deleteById(id);
    }

    @Override
    public void update(Employee employee) {
        employeeRepo.save(employee);
    }

    @Override
    public boolean hasOrder(Employee employee) {
        return employee.getOrders() != null;
    }
}
