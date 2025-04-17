package com.denka88.ateliergrace.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    private String orderName;
    private String type;
    private LocalDate created;
    private float cost;
    private Status status;

    @OneToMany(mappedBy = "order")
    private Set<OrderEmployee> orders;

    public Order(Long id, Client client, String orderName, String type, LocalDate created, float cost, Status status, Set<OrderEmployee> orders) {
        this.id = id;
        this.client = client;
        this.orderName = orderName;
        this.type = type;
        this.created = created;
        this.cost = cost;
        this.status = status;
        this.orders = orders;
    }

    public Order() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<OrderEmployee> getOrders() {
        return orders;
    }

    public void setOrders(Set<OrderEmployee> orders) {
        this.orders = orders;
    }
}
