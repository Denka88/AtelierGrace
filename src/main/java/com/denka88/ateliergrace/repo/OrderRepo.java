package com.denka88.ateliergrace.repo;

import com.denka88.ateliergrace.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepo extends JpaRepository<Order, Long> {

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderEmployees oe LEFT JOIN FETCH oe.employee LEFT JOIN FETCH o.materials")
    List<Order> findAllWithEmployeesAndMaterials();
    
    Optional<Order> findByClientId(Long clientId);
    
}
