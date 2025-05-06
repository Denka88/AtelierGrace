package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.Auth;
import com.denka88.ateliergrace.model.Client;
import com.denka88.ateliergrace.service.AuthService;
import com.denka88.ateliergrace.service.ClientService;
import com.denka88.ateliergrace.service.CurrentUserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;

import java.util.Optional;

@Route(value = "client-profile", layout = MainLayout.class)
@PageTitle("Мой профиль")
@RolesAllowed("CLIENT")
public class ClientProfile extends VerticalLayout {

    private final CurrentUserService currentUserService;
    private final ClientService clientService;
    private final TextField id = new TextField("ID");
    private final TextField surnameField = new TextField("Фамилия");
    private final TextField nameField = new TextField("Имя");
    private final TextField patronymicField = new TextField("Отчество");
    private final TextField phoneField = new TextField("Номер телефона");

    Button confirm = new Button("Применить изменения", VaadinIcon.CHECK.create());
    Button reset = new Button("Отменить изменения", VaadinIcon.CLOSE.create());
    Button editProfile = new Button("Редактировать данные", VaadinIcon.EDIT.create());
    
    

    public ClientProfile(CurrentUserService currentUserService, ClientService clientService) {
        this.currentUserService = currentUserService;
        this.clientService = clientService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        
        id.setVisible(false);

        
        confirm.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        confirm.addClassName(LumoUtility.Margin.Bottom.LARGE);
        confirm.setVisible(false);
        confirm.addClickListener(e->{
            surnameField.setReadOnly(true);
            nameField.setReadOnly(true);
            patronymicField.setReadOnly(true);
            phoneField.setReadOnly(true);
            
            editProfile.setVisible(true);
            confirm.setVisible(false);
            reset.setVisible(false);

            Client updateClient = clientService.findById(Long.valueOf(id.getValue())).orElse(null);
            if (updateClient != null) {
                updateClient.setSurname(surnameField.getValue());
                updateClient.setName(nameField.getValue());
                updateClient.setPatronymic(patronymicField.getValue());
                updateClient.setPhone(phoneField.getValue());
                clientService.update(updateClient);
                loadClientData();
                Notification.show("Профиль обновлен")
                        .setPosition(Notification.Position.TOP_CENTER);
            }
        });
        
        reset.addThemeVariants(ButtonVariant.LUMO_ERROR);
        reset.addClassName(LumoUtility.Margin.Bottom.LARGE);
        reset.setVisible(false);
        reset.addClickListener(e->{
            loadClientData();
            
            surnameField.setReadOnly(true);
            nameField.setReadOnly(true);
            patronymicField.setReadOnly(true);
            phoneField.setReadOnly(true);
            
            editProfile.setVisible(true);
            confirm.setVisible(false);
            reset.setVisible(false);
            
        });

        editProfile.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        editProfile.addClassName(LumoUtility.Margin.Bottom.LARGE);
        editProfile.addClickListener(e->{
            surnameField.setReadOnly(false);
            nameField.setReadOnly(false);
            patronymicField.setReadOnly(false);
            phoneField.setReadOnly(false);
            
            editProfile.setVisible(false);
            confirm.setVisible(true);
            reset.setVisible(true);
        });

        HorizontalLayout buttons = new HorizontalLayout(editProfile, confirm, reset);
        buttons.setWidthFull();
        buttons.setJustifyContentMode(JustifyContentMode.CENTER);
        
        
        add(
                new H2("Мой профиль"),
                createFormLayout(),
                buttons
        );

        loadClientData();
    }

    private Component createFormLayout() {
        FormLayout formLayout = new FormLayout();
        formLayout.add(
                surnameField,
                nameField,
                patronymicField,
                phoneField
        );

        surnameField.setWidth("300px");
        nameField.setWidth("300px");
        patronymicField.setWidth("300px");
        phoneField.setWidth("300px");

        surnameField.setReadOnly(true);
        nameField.setReadOnly(true);
        patronymicField.setReadOnly(true);
        phoneField.setReadOnly(true);

        return formLayout;
    }

    private void loadClientData() {
        Optional<Client> optionalClient = currentUserService.getCurrentClient();
        optionalClient.ifPresent(client -> {
            id.setValue(client.getId() != null ? String.valueOf(client.getId()) : "");
            surnameField.setValue(client.getSurname() != null ? client.getSurname() : "");
            nameField.setValue(client.getName() != null ? client.getName() : "");
            patronymicField.setValue(client.getPatronymic() != null ? client.getPatronymic() : "");
            phoneField.setValue(client.getPhone() != null ? client.getPhone() : "");
        });
    }
    
    
}
