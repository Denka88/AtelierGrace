package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.Organization;
import com.denka88.ateliergrace.repo.OrganizationRepo;
import com.denka88.ateliergrace.service.OrganizationService;

import java.util.List;
import java.util.Optional;

public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepo organizationRepo;

    public OrganizationServiceImpl(OrganizationRepo organizationRepo) {
        this.organizationRepo = organizationRepo;
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
    public void delete(Long id) {
        organizationRepo.deleteById(id);
    }

    @Override
    public void update(Organization organization) {
        organizationRepo.save(organization);
    }
}
