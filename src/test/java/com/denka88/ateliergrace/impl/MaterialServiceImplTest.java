package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.Material;
import com.denka88.ateliergrace.model.Order;
import com.denka88.ateliergrace.repo.MaterialRepo;
import com.denka88.ateliergrace.repo.OrderRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MaterialServiceImplTest {

    @Mock
    private MaterialRepo materialRepo;

    @Mock
    private OrderRepo orderRepo;

    @InjectMocks
    private MaterialServiceImpl materialService;

    @Test
    void findAll() {
        List<Material> list = List.of(new Material());
        when(materialRepo.findAll()).thenReturn(list);
        assertEquals(list, materialService.findAll());
    }

    @Test
    void findById() {
        Material material = new Material();
        material.setId(1L);
        when(materialRepo.findById(1L)).thenReturn(Optional.of(material));

        Optional<Material> result = materialService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void save() {
        Material material = new Material();
        materialService.save(material);
        verify(materialRepo).save(material);
    }

    @Test
    void delete() {
        Long id = 1L;
        Material material = new Material();
        material.setId(id);
        Order order = new Order();
        order.setMaterials(new HashSet<>(Set.of(material)));

        when(materialRepo.findById(id)).thenReturn(Optional.of(material));
        when(orderRepo.findByMaterialsId(id)).thenReturn(List.of(order));

        materialService.delete(id);

        verify(materialRepo).deleteById(id);
    }

    @Test
    void update() {
        Material material = new Material();
        materialService.update(material);
        verify(materialRepo).save(material);
    }
}