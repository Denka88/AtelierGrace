package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.OrganizationMaterial;
import com.denka88.ateliergrace.repo.OrganizationMaterialRepo;
import com.denka88.ateliergrace.service.OrganizationMaterialService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationMaterialServiceImpl implements OrganizationMaterialService {
    
    private final OrganizationMaterialRepo organizationMaterialRepo;

    public OrganizationMaterialServiceImpl(OrganizationMaterialRepo organizationMaterialRepo) {
        this.organizationMaterialRepo = organizationMaterialRepo;
    }

    @Override
    public List<OrganizationMaterial> findAll() {
        return organizationMaterialRepo.findAll();
    }

    @Override
    public OrganizationMaterial save(OrganizationMaterial organizationMaterial) {
        return organizationMaterialRepo.save(organizationMaterial);
    }

    @Override
    public void update(OrganizationMaterial organizationMaterial) {
        organizationMaterialRepo.save(organizationMaterial);
    }
}
