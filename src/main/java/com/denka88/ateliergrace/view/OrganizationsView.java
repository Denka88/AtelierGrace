package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.Organization;
import com.denka88.ateliergrace.model.UserType;
import com.denka88.ateliergrace.service.CurrentUserService;
import com.denka88.ateliergrace.service.OrganizationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
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

@PageTitle("Поставщики")
@Route(value = "organizations-list", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "EMPLOYEE"})
public class OrganizationsView extends VerticalLayout {
    
    private final OrganizationService organizationService;
    private final Grid<Organization> grid;
    private final CurrentUserService currentUserService;

    private FormLayout editForm = new FormLayout();
    
    private TextField id = new TextField("ID");
    private TextField editName = new TextField("Название");
    private TextField editAddress = new TextField("Адресс");
    private Button editButton = new Button("Сохранить изменения");
    
    public OrganizationsView(OrganizationService organizationService, CurrentUserService currentUserService) {
        this.organizationService = organizationService;
        this.currentUserService = currentUserService;
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
        
        id.setVisible(false);

        editForm.setWidth("400px");
        editForm.add(id, editName, editAddress, editButton);
        editForm.setVisible(false);
        
        editButton.addClickListener(e->{
            Organization updateOrganization = organizationService.findById(Long.valueOf(id.getValue())).orElse(null);
            updateOrganization.setName(editName.getValue());
            updateOrganization.setAddress(editAddress.getValue());
            organizationService.update(updateOrganization);
            updateGrid();
            editForm.setVisible(false);
        });
        
        grid.addCellFocusListener(e->{
            id.setValue(String.valueOf(e.getItem().map(Organization::getId).orElse(null)));
            editName.setValue(e.getItem().map(Organization::getName).orElse("Не доступно"));
            editAddress.setValue(e.getItem().map(Organization::getAddress).orElse("Не доступно"));
        });
        
        add(grid, addOrganization, addButton, editForm);
        
    }
    
    private void setupGrid(){
        grid.setClassName("force-focus-outline");
        
        grid.addColumn(Organization::getId).setHeader("ID").setSortable(true).setAutoWidth(true);
        grid.addColumn(Organization::getName).setHeader("Название").setSortable(true).setAutoWidth(true);
        grid.addColumn(Organization::getAddress).setHeader("Адрес").setSortable(true).setAutoWidth(true);

        GridContextMenu<Organization> contextMenu = grid.addContextMenu();
        
        contextMenu.addItem("Изменить", e->{
            if(editForm.isVisible()){
                editForm.setVisible(false);
            } else if (!editForm.isVisible()) {
                editForm.setVisible(true);
                id.setValue(String.valueOf(e.getItem().map(Organization::getId).orElse(null)));
                editName.setValue(e.getItem().map(Organization::getName).orElse(null));
                editAddress.setValue(e.getItem().map(Organization::getAddress).orElse(null));
            }
        });
        if(currentUserService.getCurrentUserType().equals(UserType.ADMIN)) {
            contextMenu.addItem("Удалить", e -> {
                organizationService.delete(e.getItem().map(Organization::getId).orElse(null));
                updateGrid();
            });
        }
    }
    
    private void updateGrid(){
        List<Organization> organizations = organizationService.findAll();
        grid.setItems(organizations);
    }
    
    
}
