package com.denka88.ateliergrace.repo;

import com.denka88.ateliergrace.model.OrganizationMaterial;
import com.denka88.ateliergrace.model.OrganizationMaterialKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrganizationMaterialRepo extends JpaRepository<OrganizationMaterial, OrganizationMaterialKey> {

    @Modifying
    @Query("DELETE FROM OrganizationMaterial om WHERE om.organization.id = :organizationId")
    void deleteByOrganizationId(@Param("organizationId") Long organizationId);
    
}
