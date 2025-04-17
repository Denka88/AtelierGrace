package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.Client;
import com.denka88.ateliergrace.repo.ClientRepo;
import com.denka88.ateliergrace.service.ClientService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepo clientRepo;

    public ClientServiceImpl(ClientRepo clientRepo) {
        this.clientRepo = clientRepo;
    }

    @Override
    public List<Client> findAll() {
        return clientRepo.findAll();
    }

    @Override
    public Optional<Client> findById(Long id) {
        return clientRepo.findById(id);
    }

    @Override
    public Client save(Client client) {
        return clientRepo.save(client);
    }

    @Override
    public void delete(Long id) {
        clientRepo.deleteById(id);
    }

    @Override
    public void update(Client client) {
        clientRepo.save(client);
    }
}
