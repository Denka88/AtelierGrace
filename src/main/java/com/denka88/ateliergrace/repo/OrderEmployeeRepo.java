package com.denka88.ateliergrace.repo;

import com.denka88.ateliergrace.model.OrderEmployee;
import com.denka88.ateliergrace.model.OrderEmployeeKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderEmployeeRepo extends JpaRepository<OrderEmployee, OrderEmployeeKey> {
    OrderEmployee findByEmployee_OrdersEmpty();
}
