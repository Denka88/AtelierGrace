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
        H1 title = new H1("Грация");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("left", "var(--lumo-space-l)").set("margin", "0")
                .set("position", "absolute").set("cursor", "pointer");
        title.addClickListener(e -> UI.getCurrent().navigate("/main"));

        HorizontalLayout navigation = getNavigation();
        navigation.getElement();
        
        Button logout = new Button("Logout", e -> authenticationContext.logout());
        logout.getStyle().set("margin-right", "5px");
        
        addToNavbar(title, navigation, logout);
    }

    private HorizontalLayout getNavigation() {
        HorizontalLayout navigation = new HorizontalLayout();
        navigation.addClassNames(LumoUtility.JustifyContent.CENTER,
                LumoUtility.Gap.SMALL, LumoUtility.Height.MEDIUM,
                LumoUtility.Width.FULL);
        navigation.add(createLink("Заказы"), createLink("Клиенты"),
                createLink("Поставщики"), createLink("Материалы"), createLink("Сотрудники"));
        return navigation;
    }

    private RouterLink createLink(String viewName) {
        RouterLink link = new RouterLink();
        link.add(viewName);
            switch (viewName){
                case "Заказы":
                    link.setRoute(TestView.class);
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
                default:
                    link.setRoute(MainView.class);
            }
        link.addClassNames(LumoUtility.Display.FLEX,
                LumoUtility.AlignItems.CENTER,
                LumoUtility.Padding.Horizontal.MEDIUM,
                LumoUtility.TextColor.SECONDARY, LumoUtility.FontWeight.MEDIUM);
        link.getStyle().set("text-decoration", "none");

        return link;
    }

}