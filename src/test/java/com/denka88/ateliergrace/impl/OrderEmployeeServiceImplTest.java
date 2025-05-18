package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.*;
import com.denka88.ateliergrace.repo.EmployeeRepo;
import com.denka88.ateliergrace.repo.OrderEmployeeRepo;
import com.denka88.ateliergrace.repo.OrderRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderEmployeeServiceImplTest {

    @Mock
    private OrderEmployeeRepo orderEmployeeRepo;

    @Mock
    private EmployeeRepo employeeRepo;

    @Mock
    private OrderRepo orderRepo;

    @InjectMocks
    private OrderEmployeeServiceImpl service;

    @Test
    void findAll() {
        when(orderEmployeeRepo.findAll()).thenReturn(List.of(new OrderEmployee()));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void save() {
        OrderEmployee oe = new OrderEmployee();
        service.save(oe);
        verify(orderEmployeeRepo).save(oe);
    }

    @Test
    void update() {
        OrderEmployee oe = new OrderEmployee();
        service.update(oe);
        verify(orderEmployeeRepo).save(oe);
    }

    @Test
    void setReadyDate() {
        OrderEmployeeKey key = new OrderEmployeeKey(1L, 1L);
        OrderEmployee oe = new OrderEmployee();
        when(orderEmployeeRepo.findById(key)).thenReturn(Optional.of(oe));

        service.setReadyDate(1L, 1L, LocalDate.now());

        verify(orderEmployeeRepo).save(oe);
        assertNotNull(oe.getDateOfReady());
    }

    @Test
    void createAssignment() {
        when(orderRepo.getReferenceById(1L)).thenReturn(new Order());
        when(employeeRepo.getReferenceById(2L)).thenReturn(new Employee());

        service.createAssignment(1L, 2L, LocalDate.now());

        verify(orderEmployeeRepo).save(any(OrderEmployee.class));
    }

    @Test
    void getInProgressCount() {
        when(orderEmployeeRepo.countByEmployeeIdAndOrderStatus(1L, Status.PROGRESS)).thenReturn(3L);
        assertEquals(3L, service.getInProgressCount(1L));
    }
}