package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.OrganizationMaterial;
import com.denka88.ateliergrace.model.OrganizationMaterialKey;
import com.denka88.ateliergrace.repo.OrganizationMaterialRepo;
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
class OrganizationMaterialServiceImplTest {

    @Mock
    private OrganizationMaterialRepo repo;

    @InjectMocks
    private OrganizationMaterialServiceImpl service;

    @Test
    void findAll() {
        when(repo.findAll()).thenReturn(List.of(new OrganizationMaterial()));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void save() {
        OrganizationMaterial om = new OrganizationMaterial();
        service.save(om);
        verify(repo).save(om);
    }

    @Test
    void update() {
        OrganizationMaterial om = new OrganizationMaterial();
        service.update(om);
        verify(repo).save(om);
    }

    @Test
    void findById() {
        OrganizationMaterialKey key = new OrganizationMaterialKey(1L, 1L);
        when(repo.findById(key)).thenReturn(Optional.of(new OrganizationMaterial()));
        assertTrue(service.findById(key).isPresent());
    }
}