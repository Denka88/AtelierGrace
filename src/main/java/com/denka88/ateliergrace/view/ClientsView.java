package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.Client;
import com.denka88.ateliergrace.model.UserType;
import com.denka88.ateliergrace.service.ClientService;
import com.denka88.ateliergrace.service.CurrentUserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@PageTitle("Клиенты")
@Route(value = "clients-list", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "EMPLOYEE"})
public class ClientsView extends VerticalLayout {

    private final ClientService clientService;
    private final Grid<Client> grid;
    private final CurrentUserService currentUserService;

    private FormLayout editForm = new FormLayout();

    private TextField id = new TextField("ID");
    private TextField editSurname = new TextField("Фамилия");
    private TextField editName = new TextField("Имя");
    private TextField editPatronymic = new TextField("Отчество");
    private Button editButton = new Button("Сохранить", VaadinIcon.CHECK.create());

    public ClientsView(ClientService clientService, CurrentUserService currentUserService) {
        this.clientService = clientService;
        this.currentUserService = currentUserService;
        this.grid = new Grid<>(Client.class, false);

        setPadding(true);
        setSpacing(false);
        setSizeFull();
        addClassName(LumoUtility.Padding.LARGE);

        setupGrid();
        updateGrid();
        
        Button addClient = new Button("Добавить клиента", VaadinIcon.PLUS.create());
        addClient.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addClient.addClassName(LumoUtility.Margin.Bottom.LARGE);
        addClient.addClickListener(e->{
            UI.getCurrent().navigate("/add-client");
        });

        editForm.setWidth("400px");
        editForm.addClassNames(
                LumoUtility.Padding.LARGE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.SMALL,
                LumoUtility.Background.BASE
        );

        id.setVisible(false);
        editName.setRequired(true);
        editSurname.setRequired(true);
        editPatronymic.setRequired(true);

        editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        editButton.addClassName(LumoUtility.Margin.Top.MEDIUM);
        editButton.getStyle().set("margin-left", "auto");

        editButton.addClickListener(e->{
            if (editSurname.isEmpty() || editName.isEmpty() || editPatronymic.isEmpty()) {
                showError("Заполните все поля");
                return;
            }
            
            Client updateClient = clientService.findById(Long.valueOf(id.getValue())).orElse(null);
            if (updateClient != null) {
                updateClient.setSurname(editSurname.getValue());
                updateClient.setName(editName.getValue());
                updateClient.setPatronymic(editPatronymic.getValue());
                clientService.update(updateClient);
                updateGrid();
                editForm.setVisible(false);
            }
        });

        grid.addCellFocusListener(e->{
            id.setValue(String.valueOf(e.getItem().map(Client::getId).orElse(null)));
            editSurname.setValue(e.getItem().map(Client::getSurname).orElse("Не доступно"));
            editName.setValue(e.getItem().map(Client::getName).orElse("Не доступно"));
            editPatronymic.setValue(e.getItem().map(Client::getPatronymic).orElse("Не доступно"));
        });

        styleTextField(editSurname);
        styleTextField(editName);
        styleTextField(editPatronymic);

        Button close = new Button(new Icon("lumo", "cross"), (e) -> editForm.setVisible(false));

        HorizontalLayout formHeader = new HorizontalLayout();
        formHeader.getStyle().set("justify-content", "space-between");

        H3 title = new H3("Изменить клиента");

        formHeader.add(title, close);

        editForm.add(formHeader, id, editSurname, editName, editPatronymic, editButton);
        editForm.setVisible(false);

        HorizontalLayout buttonLayout = new HorizontalLayout(addClient);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(JustifyContentMode.END);
        
        Div content = new Div(grid, editForm);
        content.addClassName(LumoUtility.Display.FLEX);
        content.addClassName(LumoUtility.FlexDirection.COLUMN);
        content.addClassName(LumoUtility.Gap.LARGE);
        content.setSizeFull();

        add(buttonLayout, content);
    }

    private void setupGrid(){
        grid.addClassName("force-focus-outline");
        grid.addThemeVariants(
                com.vaadin.flow.component.grid.GridVariant.LUMO_WRAP_CELL_CONTENT,
                com.vaadin.flow.component.grid.GridVariant.LUMO_COLUMN_BORDERS,
                com.vaadin.flow.component.grid.GridVariant.LUMO_ROW_STRIPES
        );
        grid.setHeightFull();

        grid.addColumn(Client::getId)
                .setHeader("ID")
                .setSortable(true)
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(Client::getSurname)
                .setHeader("Фамилия")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(Client::getName)
                .setHeader("Имя")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(Client::getPatronymic)
                .setHeader("Отчество")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(Client::getPhone)
                .setHeader("Номер телефона")
                .setSortable(true)
                .setAutoWidth(true);

        GridContextMenu<Client> contextMenu = grid.addContextMenu();

        Button editMenuItem = new Button("Изменить", VaadinIcon.EDIT.create());
        editMenuItem.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        contextMenu.addItem(editMenuItem, e->{
            if (editForm.isVisible()) {
                editForm.setVisible(false);
            } else if (!editForm.isVisible()) {
                editForm.setVisible(true);
                id.setValue(String.valueOf(e.getItem().map(Client::getId).orElse(null)));
                editSurname.setValue(e.getItem().map(Client::getSurname).orElse("Не доступно"));
                editName.setValue(e.getItem().map(Client::getName).orElse("Не доступно"));
                editPatronymic.setValue(e.getItem().map(Client::getPatronymic).orElse("Не доступно"));
            }
        });

        if(currentUserService.getCurrentUserType().equals(UserType.ADMIN)) {
            Button deleteMenuItem = new Button("Удалить", VaadinIcon.TRASH.create());
            deleteMenuItem.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            contextMenu.addItem(deleteMenuItem, e -> {
                Dialog delete = new Dialog();

                Button confirmDelete = new Button("Удалить");
                confirmDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);
                Button cancelDelete = new Button("Отмена");
                cancelDelete.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

                HorizontalLayout buttonLayout = new HorizontalLayout(cancelDelete, confirmDelete);
                buttonLayout.setWidthFull();
                buttonLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
                delete.add(buttonLayout);
                delete.setHeaderTitle(String.format("Удалить клиента \"%s\"", e.getItem().map(Client::getName).orElse(null)));
                delete.open();
                
                confirmDelete.addClickListener(event -> {
                    clientService.delete(e.getItem().map(Client::getId).orElse(null));
                    updateGrid();
                    Notification.show("Клиент удален")
                            .setPosition(Notification.Position.TOP_END);
                    delete.close();
                });
                
                cancelDelete.addClickListener(event -> {
                    delete.close();
                });
            });
        }
    }

    private void updateGrid(){
        List<Client> clients = clientService.findAll();
        grid.setItems(clients);
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