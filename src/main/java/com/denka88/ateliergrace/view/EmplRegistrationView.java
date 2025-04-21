package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.Employee;
import com.denka88.ateliergrace.model.UserType;
import com.denka88.ateliergrace.service.AuthService;
import com.denka88.ateliergrace.service.EmployeeService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;

@PageTitle("Регистрация сотрудника")
@Route(value = "employee-registration", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class EmplRegistrationView extends VerticalLayout {

    private final AuthService authService;
    private final EmployeeService employeeService;

    public EmplRegistrationView(AuthService authService, EmployeeService employeeService) {
        this.authService = authService;
        this.employeeService = employeeService;

        TextField login = new TextField("Логин");
        PasswordField password = new PasswordField("Пароль");
        PasswordField confirmPassword = new PasswordField("Подтверждение пароля");

        TextField name = new TextField("Имя");
        TextField surname = new TextField("Фамилия");
        TextField patronymic = new TextField("Отчество");
        TextField post = new TextField("Должность");

        Button registerButton = new Button("Зарегестрировать");

        registerButton.addClickListener(e -> {
            if(!password.getValue().equals(confirmPassword.getValue())) {
                Notification.show("Пароли не совпадают", 3000, Notification.Position.TOP_CENTER);
                return;
            }
            try {
                Employee employee = new Employee();
                employee.setName(name.getValue());
                employee.setSurname(surname.getValue());
                employee.setPatronymic(patronymic.getValue());
                employee.setPost(post.getValue());

                Employee savedEmployee = employeeService.save(employee);

                authService.register(login.getValue(), password.getValue(), UserType.EMPLOYEE, savedEmployee.getId());

                Notification.show("Регистрация успешна!", 3000, Notification.Position.TOP_CENTER);
                getUI().ifPresent(ui -> ui.navigate("/employees-list"));
            }
            catch (Exception ex){
                Notification.show("Ошибка: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER);
            }
        });

        FormLayout form = new FormLayout();

        form.add(
                login, password, confirmPassword, name, surname, patronymic, post, registerButton
        );
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
        );
        form.setWidth("30%");
        form.getStyle().set("margin", "auto");
        add(form);
        setAlignItems(Alignment.CENTER);
    }
}
