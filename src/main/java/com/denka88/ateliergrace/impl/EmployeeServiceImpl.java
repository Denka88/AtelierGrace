package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.Employee;
import com.denka88.ateliergrace.repo.EmployeeRepo;
import com.denka88.ateliergrace.repo.OrderEmployeeRepo;
import com.denka88.ateliergrace.service.AuthService;
import com.denka88.ateliergrace.service.EmployeeService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    
    private final EmployeeRepo employeeRepo;
    private final AuthService authService;
    private final OrderEmployeeRepo orderEmployeeRepo;

    public EmployeeServiceImpl(EmployeeRepo employeeRepo, AuthService authService, OrderEmployeeRepo orderEmployeeRepo) {
        this.employeeRepo = employeeRepo;
        this.authService = authService;
        this.orderEmployeeRepo = orderEmployeeRepo;
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

    @Override
    public List<Employee> findAllOrderByInProgressOrdersCount() {
        Map<Long, Long> employeeOrderCounts = orderEmployeeRepo.countInProgressOrdersPerEmployee().stream().collect(Collectors.toMap(arr->(Long) arr[0], arr->(Long) arr[1]));
        
        List<Employee> allEmployees = employeeRepo.findAll();
        
        allEmployees.sort(Comparator.comparingLong(employee->employeeOrderCounts.getOrDefault(employee.getId(), 0L)));
        
        return allEmployees;
    }
}
