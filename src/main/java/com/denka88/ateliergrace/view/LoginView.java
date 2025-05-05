package com.denka88.ateliergrace.view;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route(value = "login", autoLayout = false)
@PageTitle("Вход | Грация")
@AnonymousAllowed
public class LoginView extends Main implements BeforeEnterObserver {

    private final LoginForm login;

    public LoginView() {
        addClassNames(
                LumoUtility.Display.FLEX,
                LumoUtility.JustifyContent.CENTER,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.FlexDirection.COLUMN,
                "login-view"
        );
        setSizeFull();
        getStyle().set("background", "var(--lumo-contrast-5pct)");

        Div card = new Div();
        card.addClassNames(
                LumoUtility.Padding.XLARGE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.MEDIUM,
                LumoUtility.Background.BASE,
                "login-card"
        );
        card.setWidth("400px");

        login = new LoginForm();
        login.setAction("login");

        LoginI18n loginForm = LoginI18n.createDefault();
        loginForm.getForm().setTitle("Войти");
        loginForm.getForm().setUsername("Логин");
        loginForm.getForm().setPassword("Пароль");
        loginForm.getForm().setSubmit("Войти");
        loginForm.getForm().setForgotPassword("Забыли пароль?");
        loginForm.setErrorMessage(new LoginI18n.ErrorMessage());
        loginForm.getErrorMessage().setTitle("Неверные данные");
        loginForm.getErrorMessage().setMessage("Проверьте логин и пароль и попробуйте снова");
        login.setI18n(loginForm);

        RouterLink registerLink = new RouterLink("Зарегистрироваться", RegistrationView.class);
        registerLink.addClassNames(LumoUtility.Margin.Top.MEDIUM);

        VerticalLayout layout = new VerticalLayout(login, registerLink);
        layout.setPadding(false);
        layout.setSpacing(false);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        card.add(layout);
        add(card);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }
}