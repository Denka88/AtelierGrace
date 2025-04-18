/*
package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.service.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;


@Route(value = "loginDep", autoLayout = false)
@AnonymousAllowed
public class LoginViewDep extends VerticalLayout {
    
    private final AuthService authService;

    public LoginViewDep(AuthService authService) {
        this.authService = authService;
        
        TextField login = new TextField("Логин");
        PasswordField password = new PasswordField("Пароль");
        Button submit = new Button("Войти");

        submit.addClickListener(e -> {
            try {
                authService.authenticate(login.getValue(), password.getValue());
                UI.getCurrent().navigate("");  // Перенаправляем на главную
            } catch (Exception ex) {
                Notification.show("Ошибка: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER);
            }
        });

        add(login, password, submit);
    }
}*/
