package com.denka88.ateliergrace.service;

import com.denka88.ateliergrace.model.OrganizationMaterial;
import com.denka88.ateliergrace.model.OrganizationMaterialKey;

import java.util.List;
import java.util.Optional;

public interface OrganizationMaterialService {
    
    List<OrganizationMaterial> findAll();
    
    OrganizationMaterial save(OrganizationMaterial organizationMaterial);
    
    void update(OrganizationMaterial organizationMaterial);
    
    Optional<OrganizationMaterial> findById(OrganizationMaterialKey id);
}
