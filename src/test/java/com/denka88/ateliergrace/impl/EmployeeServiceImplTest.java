package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.Employee;
import com.denka88.ateliergrace.repo.EmployeeRepo;
import com.denka88.ateliergrace.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepo employeeRepo;

    @Mock
    private AuthService authService;

    @InjectMocks
    private EmployeeServiceImpl service;

    @Test
    void findAll() {
        List<Employee> list = List.of(new Employee());
        when(employeeRepo.findAll()).thenReturn(list);
        assertEquals(list, service.findAll());
    }

    @Test
    void findById() {
        Employee employee = new Employee();
        employee.setId(1L);
        when(employeeRepo.findById(1L)).thenReturn(Optional.of(employee));
        assertEquals(employee, service.findById(1L).orElse(null));
    }

    @Test
    void save() {
        Employee employee = new Employee();
        service.save(employee);
        verify(employeeRepo).save(employee);
    }

    @Test
    void update() {
        Employee employee = new Employee();
        service.update(employee);
        verify(employeeRepo).save(employee);
    }

    @Test
    void delete() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setOrders(new HashSet<>());
        when(employeeRepo.findById(1L)).thenReturn(Optional.of(employee));

        service.delete(1L);

        verify(authService).delete(1L);
        verify(employeeRepo).deleteById(1L);
    }
}