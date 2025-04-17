package com.denka88.ateliergrace.service;

import com.denka88.ateliergrace.model.Organization;

import java.util.List;
import java.util.Optional;

public interface OrganizationService {

    List<Organization> findAll();

    Optional<Organization> findById(Long id);

    Organization save(Organization organization);

    void delete(Long id);

    void update(Organization organization);

}
