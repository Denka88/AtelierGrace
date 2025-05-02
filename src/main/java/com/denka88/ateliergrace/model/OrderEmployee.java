package com.denka88.ateliergrace.model;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.util.Date;

@Entity
public class OrderEmployee {

    @EmbeddedId
    private OrderEmployeeKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("employeeId")
    @JoinColumn(name = "employee_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Order order;
    
    private LocalDate dateOfReady;

    public OrderEmployee() {
    }

    public OrderEmployee(OrderEmployeeKey id, Employee employee, Order order, LocalDate dateOfReady) {
        this.id = id;
        this.employee = employee;
        this.order = order;
        this.dateOfReady = dateOfReady;
    }

    public OrderEmployeeKey getId() {
        return id;
    }

    public void setId(OrderEmployeeKey id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public LocalDate getDateOfReady() {
        return dateOfReady;
    }

    public void setDateOfReady(LocalDate dateOfReady) {
        this.dateOfReady = dateOfReady;
    }

    @Override
    public String toString() {
        return employee.getSurname() + " " + employee.getName() + " " + employee.getPatronymic();
    }
}
