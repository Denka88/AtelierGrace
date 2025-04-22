package com.denka88.ateliergrace.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "organizations")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    
    /*@OneToMany
    private List<Material> materials;*/

    public Organization() {
    }

    public Organization(Long id, String name, String address, List<Material> materials) {
        this.id = id;
        this.name = name;
        this.address = address;
//        this.materials = materials;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /*public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }*/

    @Override
    public String toString() {
        return name;
    }
}
