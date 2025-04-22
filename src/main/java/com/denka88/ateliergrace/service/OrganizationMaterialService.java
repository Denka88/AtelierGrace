package com.denka88.ateliergrace.service;

import com.denka88.ateliergrace.model.OrganizationMaterial;

import java.util.List;

public interface OrganizationMaterialService {
    
    List<OrganizationMaterial> findAll();
    
    OrganizationMaterial save(OrganizationMaterial organizationMaterial);
    
    void update(OrganizationMaterial organizationMaterial);
    
}
