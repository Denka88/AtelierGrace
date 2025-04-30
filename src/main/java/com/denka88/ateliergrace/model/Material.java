package com.denka88.ateliergrace.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "materials")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int value;

    @ManyToMany(mappedBy = "materials", fetch = FetchType.LAZY)
    private Set<Order> orders;

    public Material() {
    }

    public Material(Long id, String name, int value, Set<Order> orders) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.orders = orders;
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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Material material = (Material) o;
        return id != null && id.equals(material.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
