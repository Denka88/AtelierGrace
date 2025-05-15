package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.Employee;
import com.denka88.ateliergrace.model.OrderEmployee;
import com.denka88.ateliergrace.model.OrderEmployeeKey;
import com.denka88.ateliergrace.model.Status;
import com.denka88.ateliergrace.repo.EmployeeRepo;
import com.denka88.ateliergrace.repo.OrderEmployeeRepo;
import com.denka88.ateliergrace.repo.OrderRepo;
import com.denka88.ateliergrace.service.OrderEmployeeService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OrderEmployeeServiceImpl implements OrderEmployeeService {
    
    private final OrderEmployeeRepo orderEmployeeRepo;
    private final EmployeeRepo employeeRepo;
    private final OrderRepo orderRepo;

    public OrderEmployeeServiceImpl(OrderEmployeeRepo orderEmployeeRepo, EmployeeRepo employeeRepo, OrderRepo orderRepo) {
        this.orderEmployeeRepo = orderEmployeeRepo;
        this.employeeRepo = employeeRepo;
        this.orderRepo = orderRepo;
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

    @Transactional
    public void setReadyDate(Long orderId, Long employeeId, LocalDate readyDate) {
        OrderEmployeeKey key = new OrderEmployeeKey(orderId, employeeId);
        OrderEmployee orderEmployee = orderEmployeeRepo.findById(key)
                .orElseThrow(() -> new IllegalArgumentException("Связь заказа и сотрудника не найдена"));
        orderEmployee.setDateOfReady(readyDate);
        orderEmployeeRepo.save(orderEmployee);
    }

    @Override
    public void createAssignment(Long orderId, Long employeeId, LocalDate readyDate) {
        OrderEmployee assignment = new OrderEmployee();
        assignment.setId(new OrderEmployeeKey(orderId, employeeId));
        assignment.setOrder(orderRepo.getReferenceById(orderId));
        assignment.setEmployee(employeeRepo.getReferenceById(employeeId));
        assignment.setDateOfReady(readyDate);

        orderEmployeeRepo.save(assignment);
    }

    @Override
    public Long getInProgressCount(Long employeeId) {
        return orderEmployeeRepo.countByEmployeeIdAndOrderStatus(
                employeeId,
                Status.PROGRESS
        );
    }
}
