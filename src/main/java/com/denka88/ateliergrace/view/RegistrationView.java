package com.denka88.ateliergrace.view;

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
import com.vaadin.flow.component.html.Paragraph;
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
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

@PageTitle("Регистрация | Грация")
@Route(value = "register", autoLayout = false)
@AnonymousAllowed
public class RegistrationView extends VerticalLayout {

    private final AuthService authService;
    private final ClientService clientService;

    public RegistrationView(AuthService authService, ClientService clientService) {
        this.authService = authService;
        this.clientService = clientService;

        setPadding(false);
        setSpacing(false);
        setSizeFull();
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

        H2 title = new H2("Регистрация");
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

        PasswordField confirmPassword = new PasswordField("Подтвердите пароль");
        confirmPassword.setWidthFull();
        confirmPassword.setRequired(true);
        confirmPassword.setPlaceholder("Повторите пароль");

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

        Button registerButton = new Button("Зарегистрироваться", VaadinIcon.USER_CHECK.create());
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.setWidthFull();
        registerButton.addClassName(LumoUtility.Margin.Top.MEDIUM);

        Span loginText = new Span("Уже есть аккаунт? ");
        RouterLink loginLink = new RouterLink("Войти", LoginView.class);
        loginLink.addClassName(LumoUtility.Margin.Left.SMALL);
        HorizontalLayout loginLayout = new HorizontalLayout(loginText, loginLink);
        loginLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        loginLayout.addClassName(LumoUtility.Margin.Top.MEDIUM);

        registerButton.addClickListener(e -> {
            if (login.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                    name.isEmpty() || surname.isEmpty() || phone.isEmpty()) {
                showError("Заполните все обязательные поля");
                return;
            }

            if (!password.getValue().equals(confirmPassword.getValue())) {
                showError("Пароли не совпадают");
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

                showSuccess("Регистрация успешна! Вы будете перенаправлены на страницу входа");
                UI.getCurrent().navigate("login");
            } catch (Exception ex) {
                showError("Ошибка регистрации: " + ex.getMessage());
            }
        });

        FormLayout form = new FormLayout();
        form.addClassNames(LumoUtility.Padding.NONE);
        form.add(
                title, login, password, confirmPassword,
                name, surname, patronymic, phone,
                registerButton, loginLayout
        );
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );
        form.setColspan(title, 2);
        form.setColspan(registerButton, 2);
        form.setColspan(loginLayout, 2);

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