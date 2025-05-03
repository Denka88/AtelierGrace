package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.Employee;
import com.denka88.ateliergrace.model.UserType;
import com.denka88.ateliergrace.service.AuthService;
import com.denka88.ateliergrace.service.EmployeeService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Регистрация сотрудника | Грация")
@Route(value = "employee-registration", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class EmplRegistrationView extends VerticalLayout {

    private final AuthService authService;
    private final EmployeeService employeeService;

    public EmplRegistrationView(AuthService authService, EmployeeService employeeService) {
        this.authService = authService;
        this.employeeService = employeeService;

        // Общие стили для страницы
        setPadding(false);
        setSpacing(false);
        setSizeFull();
        addClassName(LumoUtility.Padding.LARGE);

        // Создание карточки формы
        Div card = new Div();
        card.addClassNames(
                LumoUtility.Padding.XLARGE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.MEDIUM,
                LumoUtility.Background.BASE,
                "employee-registration-card"
        );
        card.setWidth("600px");

        // Заголовок формы
        H2 title = new H2("Регистрация сотрудника");
        title.addClassNames(LumoUtility.Margin.Bottom.LARGE, LumoUtility.TextAlignment.CENTER);

        // Поля формы
        TextField login = new TextField("Логин");
        login.setWidthFull();
        login.setRequired(true);
        login.setPlaceholder("Введите логин сотрудника");
        login.setMinLength(5);

        PasswordField password = new PasswordField("Пароль");
        password.setWidthFull();
        password.setRequired(true);
        password.setPlaceholder("Введите пароль");
        password.setMinLength(8);

        PasswordField confirmPassword = new PasswordField("Подтверждение пароля");
        confirmPassword.setWidthFull();
        confirmPassword.setRequired(true);
        confirmPassword.setPlaceholder("Повторите пароль");

        TextField surname = new TextField("Фамилия");
        surname.setWidthFull();
        surname.setRequired(true);
        surname.setPlaceholder("Введите фамилию");

        TextField name = new TextField("Имя");
        name.setWidthFull();
        name.setRequired(true);
        name.setPlaceholder("Введите имя");

        TextField patronymic = new TextField("Отчество");
        patronymic.setWidthFull();
        patronymic.setPlaceholder("Введите отчество (если есть)");

        TextField post = new TextField("Должность");
        post.setWidthFull();
        post.setRequired(true);
        post.setPlaceholder("Введите должность");

        // Кнопка регистрации
        Button registerButton = new Button("Зарегистрировать", VaadinIcon.USER_CHECK.create());
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.setWidthFull();
        registerButton.addClassName(LumoUtility.Margin.Top.MEDIUM);

        registerButton.addClickListener(e -> {
            if (login.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                    name.isEmpty() || surname.isEmpty() || post.isEmpty()) {
                showError("Заполните все обязательные поля");
                return;
            }

            if (!password.getValue().equals(confirmPassword.getValue())) {
                showError("Пароли не совпадают");
                return;
            }
            
            if (login.getValue().length() < 5){
                showError("Неподходящая длина логина. Минимум 5 символов");
            }

            if(password.getValue().length() < 8){
                showError("Неподходящая длина пароля. Минимум 8 символов");
                return;
            }

            try {
                Employee employee = new Employee();
                employee.setSurname(surname.getValue());
                employee.setName(name.getValue());
                employee.setPatronymic(patronymic.getValue());
                employee.setPost(post.getValue());

                Employee savedEmployee = employeeService.save(employee);
                authService.register(
                        login.getValue(),
                        password.getValue(),
                        UserType.EMPLOYEE,
                        savedEmployee.getId()
                );

                showSuccess("Сотрудник успешно зарегистрирован!");
                UI.getCurrent().navigate("employees-list");
            } catch (Exception ex) {
                showError("Ошибка регистрации: " + ex.getMessage());
            }
        });

        // Форма с адаптивным дизайном
        FormLayout form = new FormLayout();
        form.addClassNames(LumoUtility.Padding.NONE);
        form.add(
                title,
                login, password, confirmPassword,
                surname, name, patronymic,
                post,
                registerButton
        );
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );
        form.setColspan(title, 2);
        form.setColspan(registerButton, 2);

        card.add(form);

        // Центрирование формы
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