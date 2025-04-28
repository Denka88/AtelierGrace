package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.model.Material;
import com.denka88.ateliergrace.model.Organization;
import com.denka88.ateliergrace.model.OrganizationMaterial;
import com.denka88.ateliergrace.model.OrganizationMaterialKey;
import com.denka88.ateliergrace.service.MaterialService;
import com.denka88.ateliergrace.service.OrganizationMaterialService;
import com.denka88.ateliergrace.service.OrganizationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@Route("supplies")
@PageTitle("Поставки")
@RolesAllowed({"ADMIN", "EMPLOYEE"})
public class OrganizationMaterialView extends VerticalLayout {

    private final OrganizationMaterialService organizationMaterialService;
    private final OrganizationService organizationService;
    private final MaterialService materialService;
    private final Grid<OrganizationMaterial> grid;

    public OrganizationMaterialView(OrganizationMaterialService organizationMaterialService, OrganizationService organizationService, MaterialService materialService) {
        this.organizationMaterialService = organizationMaterialService;
        this.organizationService = organizationService;
        this.materialService = materialService;
        this.grid = new Grid<>(OrganizationMaterial.class, false);

        setupGrid();
        updateGrid();

        Button addButton = new Button("Добавить поставку");

        Popover addSupplied = new Popover();
        addSupplied.setModal(true);
        addSupplied.setBackdropVisible(true);
        addSupplied.setWidth("210px");

        NumberField cost = new NumberField("Цена");
        IntegerField value = new IntegerField("Количество");

        Select<Material> materialSelect = new Select<>();
        materialSelect.setLabel("Материал");
        materialSelect.setItems(materialService.findAll());
        materialSelect.setItemLabelGenerator(Material::getName);

        Select<Organization> organizationSelect = new Select<>();
        organizationSelect.setLabel("Поставщик");
        organizationSelect.setItems(organizationService.findAll());
        organizationSelect.setItemLabelGenerator(Organization::getName);

        Button post = new Button("Добавить");
        post.addClickListener(e -> {
            Material selectedMaterial = materialSelect.getValue();
            Organization selectedOrganization = organizationSelect.getValue();

            OrganizationMaterialKey key = new OrganizationMaterialKey(selectedMaterial.getId(), selectedOrganization.getId());

            OrganizationMaterial supplied = new OrganizationMaterial();
            supplied.setId(key);
            supplied.setMaterial(materialSelect.getValue());
            supplied.setOrganization(organizationSelect.getValue());
            supplied.setValue(Integer.parseInt(String.valueOf(value.getValue())));
            supplied.setCost(Float.parseFloat(String.valueOf(cost.getValue())));
            organizationMaterialService.save(supplied);
            updateGrid();
        });

        addSupplied.add(materialSelect, organizationSelect, cost, value, post);

        addSupplied.setTarget(addButton);

        add(grid, addSupplied, addButton);
    }

    private void setupGrid(){
        grid.setClassName("force-focus-outline");

        grid.addColumn(OrganizationMaterial::getOrganization).setHeader("Поставщик").setSortable(true).setAutoWidth(true);
        grid.addColumn(OrganizationMaterial::getMaterial).setHeader("Материал").setSortable(true).setAutoWidth(true);
        grid.addColumn(OrganizationMaterial::getValue).setHeader("Колличество").setSortable(true).setAutoWidth(true);
        grid.addColumn(OrganizationMaterial::getCost).setHeader("Цена").setSortable(true).setAutoWidth(true);
    }

    private List<OrganizationMaterial> updateGrid(){
        List<OrganizationMaterial> organizationMaterials = organizationMaterialService.findAll();
        grid.setItems(organizationMaterials);
        return organizationMaterials;
    }

}
