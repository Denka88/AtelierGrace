package com.denka88.ateliergrace;

import com.denka88.ateliergrace.view.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

@Layout
@AnonymousAllowed
public class MainLayout extends AppLayout {

    public MainLayout(AuthenticationContext authenticationContext) {
        // Стилизованный заголовок
        H1 title = new H1("Грация");
        title.getStyle()
                .set("font-size", "var(--lumo-font-size-xxl)")
                .set("margin", "0")
                .set("color", "var(--lumo-primary-text-color)")
                .set("font-weight", "600")
                .set("cursor", "pointer")
                .set("transition", "color 0.2s");
        title.addClassName(LumoUtility.Padding.Horizontal.LARGE);
        title.addClickListener(e -> UI.getCurrent().navigate("/main"));
        title.addClickListener(e -> title.getStyle().set("color", "var(--lumo-primary-color-50pct)"));
        title.addClickListener(e -> title.getStyle().set("color", "var(--lumo-primary-text-color)"));

        // Навигационная панель
        HorizontalLayout navigation = getNavigation(authenticationContext);

        // Кнопка выхода с иконкой
        Button logout = new Button("Выйти", VaadinIcon.SIGN_OUT.create(),
                e -> authenticationContext.logout());
        logout.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        logout.getStyle()
                .set("margin-right", "var(--lumo-space-m)")
                .set("color", "var(--lumo-error-text-color)");
        logout.addClassName(LumoUtility.Padding.Horizontal.MEDIUM);

        // Контейнер для элементов навбара
        HorizontalLayout navbarLayout = new HorizontalLayout(title, navigation, logout);
        navbarLayout.setWidthFull();
        navbarLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        navbarLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        navbarLayout.setPadding(true);
        navbarLayout.setSpacing(false);
        navbarLayout.addClassName(LumoUtility.BoxShadow.SMALL);
        navbarLayout.getStyle()
                .set("background", "var(--lumo-base-color)")
                .set("border-bottom", "1px solid var(--lumo-contrast-10pct)");

        addToNavbar(navbarLayout);
    }

    private HorizontalLayout getNavigation(AuthenticationContext authenticationContext) {
        HorizontalLayout navigation = new HorizontalLayout();
        navigation.addClassNames(
                LumoUtility.Gap.MEDIUM,
                LumoUtility.Height.MEDIUM,
                LumoUtility.AlignItems.CENTER
        );

        if (authenticationContext.hasRole("ADMIN")){
            navigation.add(
                    styledLink("Заказы"), styledLink("Клиенты"),
                    styledLink("Поставщики"), styledLink("Материалы"),
                    styledLink("Сотрудники"), styledLink("Поставки")
            );
        } else if (authenticationContext.hasRole("CLIENT")) {
            navigation.add(
                    styledLink("Мои заказы"), styledLink("Создать заказ")
            );
        }
        else {
            navigation.add(
                    styledLink("Заказы"), styledLink("Поставщики"),
                    styledLink("Поставки"), styledLink("Материалы")
            );
        }

        return navigation;
    }

    private RouterLink styledLink(String viewName) {
        RouterLink link = createLink(viewName);
        link.addClassNames(
                LumoUtility.Padding.Vertical.SMALL,
                LumoUtility.Padding.Horizontal.MEDIUM,
                LumoUtility.BorderRadius.MEDIUM
        );
        link.getStyle()
                .set("transition", "background-color 0.2s, color 0.2s")
                .set("font-weight", "500");

        link.getElement().addEventListener("mouseenter", e -> {
            link.getStyle().set("color", "var(--lumo-primary-color)");
        });

        link.getElement().addEventListener("mouseleave", e -> {
            link.getStyle().set("color", "var(--lumo-body-text-color)");
        });

        return link;
    }

    private RouterLink createLink(String viewName) {
        RouterLink link = new RouterLink();
        link.add(viewName);
        switch (viewName){
            case "Заказы":
                link.setRoute(OrdersView.class);
                break;
            case "Клиенты":
                link.setRoute(ClientsView.class);
                break;
            case "Поставщики":
                link.setRoute(OrganizationsView.class);
                break;
            case "Материалы":
                link.setRoute(MaterialsView.class);
                break;
            case "Сотрудники":
                link.setRoute(EmployeesView.class);
                break;
            case "Поставки":
                link.setRoute(OrganizationMaterialView.class);
                break;
            case "Мои заказы":
                link.setRoute(MyOrdersView.class);
                break;
            case "Создать заказ":
                link.setRoute(AddOrderView.class);
                break;
            default:
                link.setRoute(MainView.class);
        }
        link.addClassNames(LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.TextColor.BODY,
                LumoUtility.FontWeight.MEDIUM);
        link.getStyle().set("text-decoration", "none");

        return link;
    }
}