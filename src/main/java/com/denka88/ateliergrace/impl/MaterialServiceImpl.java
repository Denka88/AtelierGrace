package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.Material;
import com.denka88.ateliergrace.model.Order;
import com.denka88.ateliergrace.repo.MaterialRepo;
import com.denka88.ateliergrace.repo.OrderRepo;
import com.denka88.ateliergrace.service.MaterialService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaterialServiceImpl implements MaterialService {
    
    private final MaterialRepo materialRepo;
    private final OrderRepo orderRepo;

    public MaterialServiceImpl(MaterialRepo materialRepo, OrderRepo orderRepo) {
        this.materialRepo = materialRepo;
        this.orderRepo = orderRepo;
    }

    @Override
    public List<Material> findAll() {
        return materialRepo.findAll();
    }

    @Override
    public Optional<Material> findById(Long id) {
        return materialRepo.findById(id);
    }

    @Override
    public Material save(Material material) {
        return materialRepo.save(material);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID материала не может быть null");
        }
        
        Material material = materialRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Материал с ID " + id + " не найден"));
        
        List<Order> orders = orderRepo.findByMaterialsId(id);

        for (Order order : orders) {
            order.getMaterials().remove(material);
            orderRepo.save(order);
        }

        materialRepo.deleteById(id);
    }

    @Override
    @Transactional
    public void update(Material material) {
        materialRepo.save(material);
    }
}
