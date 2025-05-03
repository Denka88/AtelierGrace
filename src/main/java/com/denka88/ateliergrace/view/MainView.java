package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.Employee;
import com.denka88.ateliergrace.model.UserType;
import com.denka88.ateliergrace.service.CurrentUserService;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

@Route(value = "main", layout = MainLayout.class)
@RouteAlias(value = "index", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PageTitle("MAIN")
@PermitAll
public class MainView extends VerticalLayout {
    
    private final CurrentUserService currentUserService;

    public MainView(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;

        H1 title = new H1();

        if (currentUserService.getCurrentUserType() == UserType.EMPLOYEE) {
            currentUserService.getCurrentEmployee().ifPresent(employee -> {
                String surname = employee.getSurname();
                String name = employee.getName();
                String patronymic = employee.getPatronymic();

                String fio = surname + " " + name + " " + patronymic;

                title.setText(String.format("Здравствуйте, %s!", fio));
            });
        }
        
        if (currentUserService.getCurrentUserType() == UserType.CLIENT) {
            currentUserService.getCurrentClient().ifPresent(client -> {
                String surname = client.getSurname();
                String name = client.getName();
                String patronymic = client.getPatronymic();

                String fio = surname + " " + name + " " + patronymic;

                title.setText(String.format("Здравствуйте, %s!", fio));
            });
        }

        title.getStyle().set("margin", "auto");
        add(title);
    }
    
    
    
}
