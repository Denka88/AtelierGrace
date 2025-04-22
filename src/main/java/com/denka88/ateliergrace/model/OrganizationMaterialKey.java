package com.denka88.ateliergrace.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrganizationMaterialKey implements Serializable {

    @Column(name = "organization_id")
    private Long organizationId;

    @Column(name = "material_id")
    private Long materialId;

    public OrganizationMaterialKey() {
    }

    public OrganizationMaterialKey(Long organizationId, Long materialId) {
        this.organizationId = organizationId;
        this.materialId = materialId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrganizationMaterialKey that = (OrganizationMaterialKey) o;
        return Objects.equals(organizationId, that.organizationId) && Objects.equals(materialId, that.materialId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(organizationId, materialId);
    }
}
