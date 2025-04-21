package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.Material;
import com.denka88.ateliergrace.service.MaterialService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@PageTitle("Материалы")
@Route(value = "materials-list", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "EMPLOYEE"})
public class MaterialsView extends VerticalLayout {
    
    private final MaterialService materialService;
    private final Grid<Material> grid;

    public MaterialsView(MaterialService materialService) {
        this.materialService = materialService;
        this.grid = new Grid<>(Material.class, false);
        
        setupGrid();
        updateGrid();
        
        add(grid);
    }
    
    private void setupGrid(){
        grid.setClassName("force-focus-outline");
        
        grid.addColumn(Material::getId).setHeader("ID").setSortable(true);
        grid.addColumn(Material::getName).setHeader("Название").setSortable(true);
        grid.addColumn(Material::getValue).setHeader("На складе").setSortable(true);
    }
    
    private List<Material> updateGrid(){
        List<Material> materials = materialService.findAll();
        grid.setItems(materials);
        return materials;
    }
}
