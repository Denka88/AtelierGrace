package com.denka88.ateliergrace.model;


import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class OrganizationMaterial {
    
    @EmbeddedId
    private OrganizationMaterialKey id;
    
    @ManyToOne
    @MapsId("materialId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Material material;
    
    @ManyToOne
    @MapsId("organizationId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Organization organization;
    
    private float cost;
    private int value;

    public OrganizationMaterial() {
    }

    public OrganizationMaterial(OrganizationMaterialKey id, Material material, Organization organization, float cost, int value) {
        this.id = id;
        this.material = material;
        this.organization = organization;
        this.cost = cost;
        this.value = value;
    }

    public OrganizationMaterialKey getId() {
        return id;
    }

    public void setId(OrganizationMaterialKey id) {
        this.id = id;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
