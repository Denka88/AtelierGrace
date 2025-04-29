package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.Client;
import com.denka88.ateliergrace.model.Material;
import com.denka88.ateliergrace.model.UserType;
import com.denka88.ateliergrace.service.ClientService;
import com.denka88.ateliergrace.service.CurrentUserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@PageTitle("Клиенты")
@Route(value = "clients-list", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "EMPLOYEE"})
public class ClientsView extends VerticalLayout {

    private final ClientService clientService;
    private final Grid<Client> grid;
    private final CurrentUserService currentUserService;
    
    private FormLayout editForm = new FormLayout();
    
    private TextField id = new TextField("ID");
    private TextField editSurname = new TextField("Фамилия");
    private TextField editName = new TextField("Имя");
    private TextField editPatronymic = new TextField("Отчество");
    private Button editButton = new Button("Сохранить изменения");

    public ClientsView(ClientService clientService, CurrentUserService currentUserService) {
        this.clientService = clientService;
        this.currentUserService = currentUserService;
        this.grid = new Grid<>(Client.class, false);

        setupGrid();
        updateGrid();
        
        editForm.setWidth("400px");
        
        id.setVisible(false);
        
        editButton.addClickListener(e->{
            Client updateClient = clientService.findById(Long.valueOf(id.getValue())).orElse(null);
            updateClient.setSurname(editSurname.getValue());
            updateClient.setName(editName.getValue());
            updateClient.setPatronymic(editPatronymic.getValue());
            clientService.update(updateClient);
            updateGrid();
            editForm.setVisible(false);
        });
        
        grid.addCellFocusListener(e->{
            id.setValue(String.valueOf(e.getItem().map(Client::getId).orElse(null)));
            editSurname.setValue(e.getItem().map(Client::getSurname).orElse("Не доступно"));
            editName.setValue(e.getItem().map(Client::getName).orElse("Не доступно"));
            editPatronymic.setValue(e.getItem().map(Client::getPatronymic).orElse("Не доступно"));
        });

        editForm.add(id, editSurname, editName, editPatronymic, editButton);
        editForm.setVisible(false);

        add(grid, editForm);
    }

    private void setupGrid(){
        grid.setClassName("force-focus-outline");

        grid.addColumn(Client::getId).setHeader("ID").setSortable(true).setAutoWidth(true);
        grid.addColumn(Client::getSurname).setHeader("Фамилия").setSortable(true).setAutoWidth(true);
        grid.addColumn(Client::getName).setHeader("Имя").setSortable(true).setAutoWidth(true);
        grid.addColumn(Client::getPatronymic).setHeader("Отчество").setSortable(true).setAutoWidth(true);
        grid.addColumn(Client::getPhone).setHeader("Номер телефона").setSortable(true).setAutoWidth(true);

        GridContextMenu<Client> contextMenu = grid.addContextMenu();
        contextMenu.addItem("Изменить", e->{
            if (editForm.isVisible()) {
                editForm.setVisible(false);
            } else if (!editForm.isVisible()) {
                editForm.setVisible(true);
                id.setValue(String.valueOf(e.getItem().map(Client::getId).orElse(null)));
                editSurname.setValue(e.getItem().map(Client::getSurname).orElse("Не доступно"));
                editName.setValue(e.getItem().map(Client::getName).orElse("Не доступно"));
                editPatronymic.setValue(e.getItem().map(Client::getPatronymic).orElse("Не доступно"));
            }
        });
        if(currentUserService.getCurrentUserType().equals(UserType.ADMIN)) {
            contextMenu.addItem("Удалить", e -> {
                clientService.delete(e.getItem().map(Client::getId).orElse(null));
                updateGrid();
            });
        }
    }

    private List<Client> updateGrid(){
        List<Client> clients = clientService.findAll();
        grid.setItems(clients);

        return clients;
    }

}
