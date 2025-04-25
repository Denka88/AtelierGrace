package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.Material;
import com.denka88.ateliergrace.service.MaterialService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
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

        Button addButton = new Button("Добавить материал");

        Popover addMaterial = new Popover();
        addMaterial.setModal(true);
        addMaterial.setBackdropVisible(true);
        addMaterial.setWidth("210px");

        TextField name = new TextField("Название");
        TextField value = new TextField("На складе");

        Button post = new Button("Добавить");

        post.addClickListener(e -> {
           Material material = new Material();
           material.setName(name.getValue());
           material.setValue(Integer.parseInt(value.getValue()));
           materialService.save(material);
           updateGrid();
        });
        addMaterial.add(name, value, post);

        addMaterial.setTarget(addButton);

        add(grid, addMaterial, addButton);
    }
    
    private void setupGrid(){
        grid.setClassName("force-focus-outline");
        
        grid.addColumn(Material::getId).setHeader("ID").setSortable(true);
        grid.addColumn(Material::getName).setHeader("Название").setSortable(true);
        grid.addColumn(Material::getValue).setHeader("На складе").setSortable(true);
        grid.addColumn(new ComponentRenderer<>(Button::new, (button, material) -> {
            button.addThemeVariants(ButtonVariant.LUMO_ICON,
                    ButtonVariant.LUMO_ERROR,
                    ButtonVariant.LUMO_TERTIARY);
            button.addClickListener(e -> {
               materialService.delete(material.getId());
               updateGrid();
            });
            button.setIcon(new Icon(VaadinIcon.TRASH));
        })).setHeader("Действие");
    }
    
    private List<Material> updateGrid(){
        List<Material> materials = materialService.findAll();
        grid.setItems(materials);
        return materials;
    }
}
