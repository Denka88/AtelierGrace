package com.denka88.ateliergrace.repo;

import com.denka88.ateliergrace.model.OrganizationMaterial;
import com.denka88.ateliergrace.model.OrganizationMaterialKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationMaterialRepo extends JpaRepository<OrganizationMaterial, OrganizationMaterialKey> {
}
