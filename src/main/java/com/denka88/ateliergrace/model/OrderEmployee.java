package com.denka88.ateliergrace.model;

import jakarta.persistence.*;

@Entity
public class OrderEmployee {

    @EmbeddedId
    private OrderEmployeeKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("employeeId")
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private Order order;

    private int period;

    public OrderEmployee() {
    }

    public OrderEmployee(OrderEmployeeKey id, Employee employee, Order order, int period) {
        this.id = id;
        this.employee = employee;
        this.order = order;
        this.period = period;
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

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }
}
