package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.Material;
import com.denka88.ateliergrace.repo.MaterialRepo;
import com.denka88.ateliergrace.service.MaterialService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaterialServiceImpl implements MaterialService {
    
    private final MaterialRepo materialRepo;

    public MaterialServiceImpl(MaterialRepo materialRepo) {
        this.materialRepo = materialRepo;
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
    public void delete(Long id) {
        materialRepo.deleteById(id);
    }

    @Override
    public void update(Material material) {
        materialRepo.save(material);
    }
}
