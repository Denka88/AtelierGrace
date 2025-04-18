package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.model.Client;
import com.denka88.ateliergrace.model.UserType;
import com.denka88.ateliergrace.service.AuthService;
import com.denka88.ateliergrace.service.ClientService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Регистрация")
@Route(value = "register", autoLayout = false)
@AnonymousAllowed
public class RegistrationView extends VerticalLayout {
    
    private final AuthService authService;
    private final ClientService clientService;

    public RegistrationView(AuthService authService, ClientService clientService) {
        this.authService = authService;
        this.clientService = clientService;

        TextField login = new TextField("Логин");
        PasswordField password = new PasswordField("Пароль");
        PasswordField confirmPassword = new PasswordField("Подтвердите пароль");
        
        TextField name = new TextField("Имя");
        TextField surname = new TextField("Фамилия");
        TextField patronymic = new TextField("Отчество");
        TextField phone = new TextField("Телефон");

        Button registerButton = new Button("Зарегистрироваться");
        
        registerButton.addClickListener(e -> {
            if (!password.getValue().equals(confirmPassword.getValue())) {
                Notification.show("Пароли не совпадают", 3000, Notification.Position.TOP_CENTER);
                return;
            }
            try{
                Client client = new Client();
                client.setName(name.getValue());
                client.setSurname(surname.getValue());
                client.setPatronymic(patronymic.getValue());
                client.setPhone(phone.getValue());

                Client savedClient = clientService.save(client);

                authService.register(login.getValue(), password.getValue(), UserType.CLIENT, savedClient.getId());

                Notification.show("Регистрация успешна!", 3000, Notification.Position.TOP_CENTER);
                getUI().ifPresent(ui -> ui.navigate("login"));
            }
            catch (Exception ex){
                Notification.show("Ошибка: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER);
            }


        });
        
        FormLayout form = new FormLayout();

        form.add(
                login, password, confirmPassword,
                name, surname, patronymic, phone,
                registerButton
        );
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );
        add(form);
        setAlignItems(Alignment.CENTER);
    }
}
