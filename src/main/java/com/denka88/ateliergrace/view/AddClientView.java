package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.Client;
import com.denka88.ateliergrace.model.UserType;
import com.denka88.ateliergrace.service.AuthService;
import com.denka88.ateliergrace.service.ClientService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "add-client", layout = MainLayout.class)
@PageTitle("Добавить клиента")
@RolesAllowed({"EMPLOYEE", "ADMIN"})
public class AddClientView extends VerticalLayout {

    private final ClientService clientService;
    private final AuthService authService;

    public AddClientView(ClientService clientService, AuthService authService) {
        this.clientService = clientService;
        this.authService = authService;

        setSizeFull();
        setSpacing(false);
        setPadding(true);
        addClassName(LumoUtility.Background.CONTRAST_5);

        Div card = new Div();
        card.addClassNames(
                LumoUtility.Padding.XLARGE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.MEDIUM,
                LumoUtility.Background.BASE,
                "registration-card"
        );
        card.setWidth("500px");

        H2 title = new H2("Добавление клиента");
        title.addClassNames(LumoUtility.Margin.Bottom.LARGE, LumoUtility.TextAlignment.CENTER);

        TextField login = new TextField("Логин");
        login.setWidthFull();
        login.setRequired(true);
        login.setPlaceholder("Введите логин");
        login.setMinLength(5);

        PasswordField password = new PasswordField("Пароль");
        password.setWidthFull();
        password.setRequired(true);
        password.setPlaceholder("Введите пароль");
        password.setMinLength(8);

        TextField name = new TextField("Имя");
        name.setWidthFull();
        name.setRequired(true);
        name.setPlaceholder("Введите ваше имя");

        TextField surname = new TextField("Фамилия");
        surname.setWidthFull();
        surname.setRequired(true);
        surname.setPlaceholder("Введите вашу фамилию");

        TextField patronymic = new TextField("Отчество");
        patronymic.setWidthFull();
        patronymic.setPlaceholder("Введите ваше отчество (если есть)");

        TextField phone = new TextField("Телефон");
        phone.setWidthFull();
        phone.setRequired(true);
        phone.setPlaceholder("Введите номер телефона");

        Button registerButton = new Button("Добавить", VaadinIcon.USER_CHECK.create());
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.setWidthFull();
        registerButton.addClassName(LumoUtility.Margin.Top.MEDIUM);

        registerButton.addClickListener(e -> {
            if (login.isEmpty() || password.isEmpty() || name.isEmpty() || surname.isEmpty() || phone.isEmpty()) {
                showError("Заполните все обязательные поля");
                return;
            }

            if(login.getValue().length() < 5){
                showError("Неподходящая длина логина. Минимум 5 символов");
                return;
            }

            if(password.getValue().length() < 8){
                showError("Неподходящая длина пароля. Минимум 8 символов");
                return;
            }

            try {
                Client client = new Client();
                client.setName(name.getValue());
                client.setSurname(surname.getValue());
                client.setPatronymic(patronymic.getValue());
                client.setPhone(phone.getValue());

                Client savedClient = clientService.save(client);
                authService.register(
                        login.getValue(),
                        password.getValue(),
                        UserType.CLIENT,
                        savedClient.getId()
                );

                showSuccess("Регистрация успешна!");
                UI.getCurrent().navigate("clients-list");
            } catch (Exception ex) {
                showError("Ошибка регистрации: " + ex.getMessage());
            }
        });

        FormLayout form = new FormLayout();
        form.addClassNames(LumoUtility.Padding.NONE);
        form.add(
                title, login, password,
                name, surname, patronymic, phone,
                registerButton
        );
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );
        form.setColspan(title, 2);
        form.setColspan(registerButton, 2);

        card.add(form);

        Div container = new Div(card);
        container.setSizeFull();
        container.addClassNames(
                LumoUtility.Display.FLEX,
                LumoUtility.JustifyContent.CENTER,
                LumoUtility.AlignItems.CENTER
        );

        add(container);
    }

    private void showError(String message) {
        Notification.show(message, 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private void showSuccess(String message) {
        Notification.show(message, 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

}
