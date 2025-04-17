package com.denka88.ateliergrace.service;

import com.denka88.ateliergrace.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientService {

    List<Client> findAll();

    Optional<Client> findById(Long id);

    Client save(Client client);

    void delete(Long id);

    void update(Client client);

}
