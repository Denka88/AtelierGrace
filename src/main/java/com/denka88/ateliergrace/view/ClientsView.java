package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.Client;
import com.denka88.ateliergrace.service.ClientService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@PageTitle("Клиенты")
@Route(value = "clients-list", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "EMPLOYEE"})
public class ClientsView extends VerticalLayout {

    private final ClientService clientService;
    private final Grid<Client> grid;

    public ClientsView(ClientService clientService) {
        this.clientService = clientService;
        this.grid = new Grid<>(Client.class, false);

        setupGrid();
        updateGrid();

        add(grid);
    }

    private void setupGrid(){
        grid.setClassName("force-focus-outline");

        grid.addColumn(Client::getId).setHeader("ID").setSortable(true);
        grid.addColumn(Client::getSurname).setHeader("Фамилия").setSortable(true);
        grid.addColumn(Client::getName).setHeader("Имя").setSortable(true);
        grid.addColumn(Client::getPatronymic).setHeader("Отчество").setSortable(true);
        grid.addColumn(Client::getPhone).setHeader("Номер телефона").setSortable(true);
    }

    private List<Client> updateGrid(){
        List<Client> clients = clientService.findAll();
        grid.setItems(clients);

        return clients;
    }

}
