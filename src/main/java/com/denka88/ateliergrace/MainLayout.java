package com.denka88.ateliergrace;

import com.denka88.ateliergrace.service.CurrentUserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Layout
@AnonymousAllowed
public class MainLayout extends AppLayout {

    private final CurrentUserService currentUserService;

    public MainLayout(AuthenticationContext authenticationContext, CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;

        DrawerToggle toggle = new DrawerToggle();

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

        HorizontalLayout navbarRight = new HorizontalLayout();
        navbarRight.setAlignItems(FlexComponent.Alignment.CENTER);
        navbarRight.getStyle().set("margin-left", "auto").set("margin-right", "5px");

        HorizontalLayout userControls = new HorizontalLayout();
        userControls.setSpacing(false);
        userControls.setAlignItems(FlexComponent.Alignment.CENTER);
        userControls.addClassName(LumoUtility.Gap.SMALL);
        
        Span username = new Span();
        username.getStyle()
                .set("font-weight", "500")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("margin-right", "var(--lumo-space-s)");
        currentUserService.getCurrentAuth().ifPresent(auth -> {
            username.setText(auth.getLogin());
        });

        Icon userIcon = VaadinIcon.USER_CHECK.create();
        userIcon.getStyle()
                .set("width", "var(--lumo-icon-size-s)")
                .set("height", "var(--lumo-icon-size-s)")
                .set("color", "var(--lumo-contrast-60pct)");

        Button logout = new Button("Выйти", VaadinIcon.EXIT_O.create(),
                e -> authenticationContext.logout());
        logout.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        logout.getStyle()
                .set("margin-left", "var(--lumo-space-s)")
                .set("color", "var(--lumo-error-text-color)")
                .set("padding", "0");

        userControls.add(userIcon, username, logout);
        navbarRight.add(userControls);
        SideNav nav = getSideNav(authenticationContext);
        Scroller scroller = new Scroller(nav);
        scroller.setClassName(LumoUtility.Padding.SMALL);

        addToDrawer(scroller);

        addToNavbar(toggle, title, navbarRight);
    }

    private SideNav getSideNav(AuthenticationContext authenticationContext) {
        SideNav sideNav = new SideNav();
        if (authenticationContext.hasRole("ADMIN")) {
            sideNav.addItem(
                    new SideNavItem("Заказы", "/orders-list",VaadinIcon.PAPERCLIP.create()),
                    new SideNavItem("Клиенты", "/clients-list", VaadinIcon.USER.create()),
                    new SideNavItem("Поставщики", "/organizations-list", VaadinIcon.OFFICE.create()),
                    new SideNavItem("Материалы", "/materials-list", VaadinIcon.CUBE.create()),
                    new SideNavItem("Сотрудники", "/employees-list", VaadinIcon.USER_STAR.create()),
                    new SideNavItem("Поставки", "/supplies", VaadinIcon.TRUCK.create())
            );
        }
        else if (authenticationContext.hasRole("CLIENT")) {
            sideNav.addItem(
                    new SideNavItem("Мои заказы", "/my-orders", VaadinIcon.PAPERCLIP.create()),
                    new SideNavItem("Мой профиль", "/client-profile", VaadinIcon.USER_CARD.create())
            );
        }
        else{
            sideNav.addItem(
                    new SideNavItem("Заказы", "/orders-list",VaadinIcon.PAPERCLIP.create()),
                    new SideNavItem("Поставщики", "/organizations-list", VaadinIcon.OFFICE.create()),
                    new SideNavItem("Материалы", "/materials-list", VaadinIcon.CUBE.create()),
                    new SideNavItem("Поставки", "/supplies", VaadinIcon.TRUCK.create()),
                    new SideNavItem("Клиенты", "/clients-list", VaadinIcon.USER.create())
            );
        }
        return sideNav;
    }
}