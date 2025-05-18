package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.Client;
import com.denka88.ateliergrace.repo.ClientRepo;
import com.denka88.ateliergrace.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepo clientRepo;

    @Mock
    private AuthService authService;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    void findAll() {
        List<Client> clients = List.of(new Client());
        when(clientRepo.findAll()).thenReturn(clients);

        assertEquals(clients, clientService.findAll());
    }

    @Test
    void findById() {
        Client client = new Client();
        client.setId(1L);
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));

        Optional<Client> result = clientService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void save() {
        Client client = new Client();
        clientService.save(client);
        verify(clientRepo).save(client);
    }

    @Test
    void delete() {
        Client client = new Client();
        client.setId(1L);
        when(clientRepo.findById(1L)).thenReturn(Optional.of(client));

        clientService.delete(1L);

        verify(authService).delete(1L);
        verify(clientRepo).delete(client);
    }

    @Test
    void update() {
        Client client = new Client();
        clientService.update(client);
        verify(clientRepo).save(client);
    }
}