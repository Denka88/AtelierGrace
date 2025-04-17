package com.denka88.ateliergrace.service;

import com.denka88.ateliergrace.model.Material;

import java.util.List;
import java.util.Optional;

public interface Materialservice {

    List<Material> findAll();

    Optional<Material> findById(Long id);

    Material save(Material material);

    void delete(Long id);

    void update(Material material);

}
