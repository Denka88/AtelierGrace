package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.Organization;
import com.denka88.ateliergrace.repo.OrganizationMaterialRepo;
import com.denka88.ateliergrace.repo.OrganizationRepo;
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
class OrganizationServiceImplTest {

    @Mock
    private OrganizationRepo organizationRepo;

    @Mock
    private OrganizationMaterialRepo organizationMaterialRepo;

    @InjectMocks
    private OrganizationServiceImpl service;

    @Test
    void findAll() {
        when(organizationRepo.findAll()).thenReturn(List.of(new Organization()));
        assertEquals(1, service.findAll().size());
    }

    @Test
    void findById() {
        Organization organization = new Organization();
        organization.setId(1L);
        when(organizationRepo.findById(1L)).thenReturn(Optional.of(organization));
        assertEquals(organization, service.findById(1L).orElse(null));
    }

    @Test
    void save() {
        Organization organization = new Organization();
        service.save(organization);
        verify(organizationRepo).save(organization);
    }

    @Test
    void update() {
        Organization organization = new Organization();
        service.update(organization);
        verify(organizationRepo).save(organization);
    }

    @Test
    void delete() {
        Organization organization = new Organization();
        organization.setId(1L);
        when(organizationRepo.findById(1L)).thenReturn(Optional.of(organization));

        service.delete(1L);

        verify(organizationMaterialRepo).deleteByOrganizationId(1L);
        verify(organizationRepo).deleteById(1L);
    }
}