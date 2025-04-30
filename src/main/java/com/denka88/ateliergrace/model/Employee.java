package com.denka88.ateliergrace.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "employees")
public class Employee {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String post;

    private String name;
    private String surname;
    private String patronymic;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderEmployee> orders;

    public Employee() {
    }

    public Employee(Long id, String post, String name, String surname, String patronymic, Set<OrderEmployee> orders) {
        this.id = id;
        this.post = post;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.orders = orders;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    @Override
    public String toString() {
        return name;
    }

    public Set<OrderEmployee> getOrders() {
        return orders;
    }

    public void setOrders(Set<OrderEmployee> orders) {
        this.orders = orders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id != null && id.equals(employee.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
