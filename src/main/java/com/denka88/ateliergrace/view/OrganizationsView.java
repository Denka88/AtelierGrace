package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.Organization;
import com.denka88.ateliergrace.model.UserType;
import com.denka88.ateliergrace.service.CurrentUserService;
import com.denka88.ateliergrace.service.OrganizationService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@PageTitle("Поставщики")
@Route(value = "organizations-list", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "EMPLOYEE"})
public class OrganizationsView extends VerticalLayout {

    private final OrganizationService organizationService;
    private final Grid<Organization> grid;
    private final CurrentUserService currentUserService;

    private FormLayout editForm = new FormLayout();

    private TextField id = new TextField("ID");
    private TextField editName = new TextField("Название");
    private TextField editAddress = new TextField("Адрес");
    private Button editButton = new Button("Сохранить", VaadinIcon.CHECK.create());

    public OrganizationsView(OrganizationService organizationService, CurrentUserService currentUserService) {
        this.organizationService = organizationService;
        this.currentUserService = currentUserService;
        this.grid = new Grid<>(Organization.class, false);

        setPadding(true);
        setSpacing(false);
        setSizeFull();
        addClassName(LumoUtility.Padding.LARGE);

        setupGrid();
        updateGrid();

        Button addButton = new Button("Добавить поставщика", VaadinIcon.PLUS.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClassName(LumoUtility.Margin.Bottom.LARGE);

        Popover addOrganization = new Popover();
        addOrganization.setModal(true);
        addOrganization.setBackdropVisible(true);
        addOrganization.setWidth("300px");
        addOrganization.addClassName(LumoUtility.Padding.LARGE);

        TextField name = new TextField("Название");
        name.setWidthFull();

        TextField address = new TextField("Адрес");
        address.setWidthFull();

        Button post = new Button("Добавить", VaadinIcon.CHECK.create());
        post.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        post.setWidthFull();

        post.addClickListener(e -> {
            if (name.isEmpty() || address.isEmpty()) {
                Notification.show("Заполните все поля")
                        .setPosition(Notification.Position.TOP_END);
                return;
            }
            Organization organization = new Organization();
            organization.setName(name.getValue());
            organization.setAddress(address.getValue());
            organizationService.save(organization);
            updateGrid();
            name.clear();
            address.clear();
            Notification.show("Поставщик добавлен")
                    .setPosition(Notification.Position.TOP_END);
        });

        FormLayout popoverForm = new FormLayout(name, address, post);
        popoverForm.setWidth("400px");
        addOrganization.add(popoverForm);
        addOrganization.setTarget(addButton);

        id.setVisible(false);
        
        editName.setMinLength(3);
        editName.setRequired(true);
        
        editAddress.setMinLength(5);
        editAddress.setRequired(true);

        editForm.setWidth("400px");
        editForm.addClassNames(
                LumoUtility.Padding.LARGE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.SMALL,
                LumoUtility.Background.BASE
        );

        editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        editButton.addClassName(LumoUtility.Margin.Top.MEDIUM);
        editButton.getStyle().set("margin-left", "auto");

        editButton.addClickListener(e->{
            if(editName.isEmpty() || editAddress.isEmpty()){
                showError("Заполните все поля");
                return;
            }
            
            if(editName.getValue().length() < 3){
                showError("Неподходящая длина названия. Минимум 3 символа");
                return;
            }
            
            if(editAddress.getValue().length() < 5){
                showError("Неподходящая длина адреса. Минимум 5 символов");
                return;
            }
            
            Organization updateOrganization = organizationService.findById(Long.valueOf(id.getValue())).orElse(null);
            if (updateOrganization != null) {
                updateOrganization.setName(editName.getValue());
                updateOrganization.setAddress(editAddress.getValue());
                organizationService.update(updateOrganization);
                updateGrid();
                editForm.setVisible(false);
                Notification.show("Изменения сохранены")
                        .setPosition(Notification.Position.TOP_END);
            }
        });

        grid.addCellFocusListener(e->{
            id.setValue(String.valueOf(e.getItem().map(Organization::getId).orElse(null)));
            editName.setValue(e.getItem().map(Organization::getName).orElse("Не доступно"));
            editAddress.setValue(e.getItem().map(Organization::getAddress).orElse("Не доступно"));
        });

        styleTextField(editName);
        styleTextField(editAddress);

        Button close = new Button(new Icon("lumo", "cross"), (e) -> editForm.setVisible(false));

        HorizontalLayout formHeader = new HorizontalLayout();
        formHeader.getStyle().set("justify-content", "space-between");

        H3 title = new H3("Изменить поставщика");

        formHeader.add(title, close);

        editForm.add(formHeader, id, editName, editAddress, editButton);
        editForm.setVisible(false);

        HorizontalLayout buttonLayout = new HorizontalLayout(addButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        Div content = new Div(grid, editForm);
        content.addClassName(LumoUtility.Display.FLEX);
        content.addClassName(LumoUtility.FlexDirection.COLUMN);
        content.addClassName(LumoUtility.Gap.LARGE);
        content.setSizeFull();

        add(buttonLayout, content, addOrganization);
    }

    private void setupGrid(){
        grid.addClassName("force-focus-outline");
        grid.addThemeVariants(
                com.vaadin.flow.component.grid.GridVariant.LUMO_WRAP_CELL_CONTENT,
                com.vaadin.flow.component.grid.GridVariant.LUMO_COLUMN_BORDERS,
                com.vaadin.flow.component.grid.GridVariant.LUMO_ROW_STRIPES
        );
        grid.setHeightFull();

        grid.addColumn(Organization::getId)
                .setHeader("ID")
                .setSortable(true)
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(Organization::getName)
                .setHeader("Название")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(Organization::getAddress)
                .setHeader("Адрес")
                .setSortable(true)
                .setAutoWidth(true);

        GridContextMenu<Organization> contextMenu = grid.addContextMenu();

        Button editMenuItem = new Button("Изменить", VaadinIcon.EDIT.create());
        editMenuItem.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        contextMenu.addItem(editMenuItem, e->{
            if(editForm.isVisible()){
                editForm.setVisible(false);
            } else if (!editForm.isVisible()) {
                editForm.setVisible(true);
                id.setValue(String.valueOf(e.getItem().map(Organization::getId).orElse(null)));
                editName.setValue(e.getItem().map(Organization::getName).orElse(null));
                editAddress.setValue(e.getItem().map(Organization::getAddress).orElse(null));
            }
        });

        if(currentUserService.getCurrentUserType().equals(UserType.ADMIN)) {
            Button deleteMenuItem = new Button("Удалить", VaadinIcon.TRASH.create());
            deleteMenuItem.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            contextMenu.addItem(deleteMenuItem, e -> {
                organizationService.delete(e.getItem().map(Organization::getId).orElse(null));
                updateGrid();
                Notification.show("Поставщик удален")
                        .setPosition(Notification.Position.TOP_END);
            });
        }
    }

    private void updateGrid(){
        List<Organization> organizations = organizationService.findAll();
        grid.setItems(organizations);
    }

    private void styleTextField(TextField textField) {
        textField.setWidthFull();
        textField.addClassName(LumoUtility.Margin.Bottom.SMALL);
        textField.getElement().getThemeList().add("small");
    }

    private void showError(String message) {
        Notification.show(message, 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}