package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.Client;
import com.denka88.ateliergrace.model.UserType;
import com.denka88.ateliergrace.service.ClientService;
import com.denka88.ateliergrace.service.CurrentUserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

    public ClientsView(ClientService clientService, CurrentUserService currentUserService) {
        this.clientService = clientService;
        this.currentUserService = currentUserService;
        this.grid = new Grid<>(Client.class, false);

        setupGrid();
        updateGrid();

        add(grid);
    }

    private void setupGrid(){
        grid.setClassName("force-focus-outline");

        grid.addColumn(Client::getId).setHeader("ID").setSortable(true).setAutoWidth(true);
        grid.addColumn(Client::getSurname).setHeader("Фамилия").setSortable(true).setAutoWidth(true);
        grid.addColumn(Client::getName).setHeader("Имя").setSortable(true).setAutoWidth(true);
        grid.addColumn(Client::getPatronymic).setHeader("Отчество").setSortable(true).setAutoWidth(true);
        grid.addColumn(Client::getPhone).setHeader("Номер телефона").setSortable(true).setAutoWidth(true);
        if(currentUserService.getCurrentUserType().equals(UserType.ADMIN)){
            grid.addColumn(new ComponentRenderer<>(Button::new, (button, client) -> {
                button.addThemeVariants(ButtonVariant.LUMO_ICON,
                        ButtonVariant.LUMO_ERROR,
                        ButtonVariant.LUMO_TERTIARY);
                button.addClickListener(e -> {
                    clientService.delete(client.getId());
                    updateGrid();
                });
                button.setIcon(new Icon(VaadinIcon.TRASH));
            })).setHeader("Действие").setAutoWidth(true);
        }
    }

    private List<Client> updateGrid(){
        List<Client> clients = clientService.findAll();
        grid.setItems(clients);

        return clients;
    }

}
