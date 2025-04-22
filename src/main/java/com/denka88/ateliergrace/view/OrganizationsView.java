package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.Organization;
import com.denka88.ateliergrace.service.OrganizationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@PageTitle("Поставщики")
@Route(value = "organizations-list", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "EMPLOYEE"})
public class OrganizationsView extends VerticalLayout {
    
    private final OrganizationService organizationService;
    private final Grid<Organization> grid;

    public OrganizationsView(OrganizationService organizationService) {
        this.organizationService = organizationService;
        this.grid = new Grid<>(Organization.class, false);
        
        setupGrid();
        updateGrid();

        Button addButton = new Button("Добавить поставщика");

        Popover addOrganization = new Popover();
        addOrganization.setModal(true);
        addOrganization.setBackdropVisible(true);
        addOrganization.setWidth("210px");

        TextField name = new TextField("Название");
        TextField address = new TextField("Адрес");

        Button post = new Button("Добавить");

        post.addClickListener(e -> {
           Organization organization = new Organization();
           organization.setName(name.getValue());
           organization.setAddress(address.getValue());
           organizationService.save(organization);
           updateGrid();
        });

        addOrganization.add(name, address, post);

        addOrganization.setTarget(addButton);
        
        add(grid, addOrganization, addButton);
    }
    
    private void setupGrid(){
        grid.setClassName("force-focus-outline");
        
        grid.addColumn(Organization::getId).setHeader("ID").setSortable(true);
        grid.addColumn(Organization::getName).setHeader("Название").setSortable(true);
        grid.addColumn(Organization::getAddress).setHeader("Адрес").setSortable(true);
    }
    
    private List<Organization> updateGrid(){
        List<Organization> organizations = organizationService.findAll();
        grid.setItems(organizations);
        return organizations;
    }
    
    
}
