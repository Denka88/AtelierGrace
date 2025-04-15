package com.denka88.ateliergrace.repo;

import com.denka88.ateliergrace.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepo extends JpaRepository<Employee, Long> {
    
}
