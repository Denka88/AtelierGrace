package com.denka88.ateliergrace.repo;

import com.denka88.ateliergrace.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepo extends JpaRepository<Organization, Long> {
}