package com.denka88.ateliergrace.repo;

import com.denka88.ateliergrace.model.OrderEmployee;
import com.denka88.ateliergrace.model.OrderEmployeeKey;
import com.denka88.ateliergrace.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderEmployeeRepo extends JpaRepository<OrderEmployee, OrderEmployeeKey> {

    @Query("SELECT oe.employee.id, COUNT(oe) FROM OrderEmployee oe " +
            "WHERE oe.order.status = com.denka88.ateliergrace.model.Status.PROGRESS " +
            "GROUP BY oe.employee.id")
    List<Object[]> countInProgressOrdersPerEmployee();
    
    @Query("SELECT COUNT(oe) FROM OrderEmployee oe " +
            "WHERE oe.employee.id = :employeeId " +
            "AND oe.order.status = com.denka88.ateliergrace.model.Status.PROGRESS")
    Long countByEmployeeIdAndOrderStatus(@Param("employeeId") Long employeeId, Status status);
}
