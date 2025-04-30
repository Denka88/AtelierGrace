package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.Organization;
import com.denka88.ateliergrace.repo.OrganizationMaterialRepo;
import com.denka88.ateliergrace.repo.OrganizationRepo;
import com.denka88.ateliergrace.service.OrganizationService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepo organizationRepo;
    private final OrganizationMaterialRepo organizationMaterialRepo;

    public OrganizationServiceImpl(OrganizationRepo organizationRepo, OrganizationMaterialRepo organizationMaterialRepo) {
        this.organizationRepo = organizationRepo;
        this.organizationMaterialRepo = organizationMaterialRepo;
    }

    @Override
    public List<Organization> findAll() {
        return organizationRepo.findAll();
    }

    @Override
    public Optional<Organization> findById(Long id) {
        return organizationRepo.findById(id);
    }

    @Override
    public Organization save(Organization organization) {
        return organizationRepo.save(organization);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Organization organization = organizationRepo.findById(id).orElseThrow(()-> new RuntimeException("Поставщик не найден"));
        organizationMaterialRepo.deleteByOrganizationId(id);
        organizationRepo.deleteById(id);
    }

    @Override
    public void update(Organization organization) {
        organizationRepo.save(organization);
    }
}
