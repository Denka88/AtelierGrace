package com.denka88.ateliergrace.repo;

import com.denka88.ateliergrace.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepo extends JpaRepository<Order, Long> {
}
