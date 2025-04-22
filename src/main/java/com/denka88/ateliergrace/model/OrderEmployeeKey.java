package com.denka88.ateliergrace.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

import java.io.Serializable;
import java.util.Objects;


@Embeddable
public class OrderEmployeeKey implements Serializable {

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "employee_id")
    private Long employeeId;

    public OrderEmployeeKey() {
    }

    public OrderEmployeeKey(Long orderId, Long employeeId) {
        this.orderId = orderId;
        this.employeeId = employeeId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderEmployeeKey that = (OrderEmployeeKey) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(employeeId, that.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, employeeId);
    }
}
