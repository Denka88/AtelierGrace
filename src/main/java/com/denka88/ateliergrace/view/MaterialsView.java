package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.Material;
import com.denka88.ateliergrace.model.UserType;
import com.denka88.ateliergrace.service.CurrentUserService;
import com.denka88.ateliergrace.service.MaterialService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
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
    private final CurrentUserService currentUserService;

    private FormLayout editForm = new FormLayout();

    private TextField id = new TextField("ID");
    private TextField editName = new TextField("Название");
    private IntegerField editValue = new IntegerField("Кол-во");
    private Button editButton = new Button("Сохранить изменения");
    

    public MaterialsView(MaterialService materialService, CurrentUserService currentUserService) {
        this.materialService = materialService;
        this.currentUserService = currentUserService;
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

        editForm.setWidth("400px");

        id.setVisible(false);
        
        editValue.setStepButtonsVisible(true);
        editValue.setMin(1);
        editValue.setMax(999999);
        
        editButton.addClickListener(e->{
            Material updateMaterial = materialService.findById(Long.valueOf(id.getValue())).orElse(null);
            updateMaterial.setName(editName.getValue());
            updateMaterial.setValue(editValue.getValue());
            materialService.update(updateMaterial);
            updateGrid();
            editForm.setVisible(false);
        });
        
        grid.addCellFocusListener(e->{
            id.setValue(String.valueOf(e.getItem().map(Material::getId).orElse(null)));
            editName.setValue(e.getItem().map(Material::getName).orElse(null));
            editValue.setValue(e.getItem().map(Material::getValue).orElse(null));
        });
        
        editForm.add(id, editName, editValue, editButton);
        editForm.setVisible(false);
        
        add(grid, addMaterial, addButton, editForm);
    }
    
    private void setupGrid(){
        grid.setClassName("force-focus-outline");
        
        grid.addColumn(Material::getId).setHeader("ID").setSortable(true).setAutoWidth(true);
        grid.addColumn(Material::getName).setHeader("Название").setSortable(true).setAutoWidth(true);
        grid.addColumn(Material::getValue).setHeader("На складе").setSortable(true).setAutoWidth(true);

        GridContextMenu<Material> contextMenu = grid.addContextMenu();
        contextMenu.addItem("Изменить", e->{
            if(editForm.isVisible()){
                editForm.setVisible(false);
            }else if(!editForm.isVisible()){
                editForm.setVisible(true);
                id.setValue(String.valueOf(e.getItem().map(Material::getId).orElse(null)));
                editName.setValue(e.getItem().map(Material::getName).orElse(null));
                editValue.setValue(e.getItem().map(Material::getValue).orElse(null));
            }
        });
        if(currentUserService.getCurrentUserType().equals(UserType.ADMIN)) {
            contextMenu.addItem("Удалить", e -> {
                materialService.delete(e.getItem().map(Material::getId).orElse(null));
                updateGrid();
            });
        }
        
    }
    
    private void updateGrid(){
        List<Material> materials = materialService.findAll();
        grid.setItems(materials);
    }
}
