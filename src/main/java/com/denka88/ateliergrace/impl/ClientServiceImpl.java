package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.Client;
import com.denka88.ateliergrace.repo.AuthRepo;
import com.denka88.ateliergrace.repo.ClientRepo;
import com.denka88.ateliergrace.repo.OrderRepo;
import com.denka88.ateliergrace.service.AuthService;
import com.denka88.ateliergrace.service.ClientService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepo clientRepo;
    private final AuthService authService;

    public ClientServiceImpl(ClientRepo clientRepo, AuthService authService) {
        this.clientRepo = clientRepo;
        this.authService = authService;
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
        Client client = clientRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Клиент не найден"));
        
        authService.delete(client.getId());
        
        clientRepo.delete(client);
    }

    @Override
    public void update(Client client) {
        clientRepo.save(client);
    }
}
