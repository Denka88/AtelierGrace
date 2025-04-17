package com.denka88.ateliergrace.repo;

import com.denka88.ateliergrace.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepo extends JpaRepository<Material, Long> {
}