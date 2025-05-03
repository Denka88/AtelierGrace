package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.Employee;
import com.denka88.ateliergrace.model.UserType;
import com.denka88.ateliergrace.service.CurrentUserService;
import com.denka88.ateliergrace.service.EmployeeService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@PageTitle("Сотрудники")
@Route(value = "employees-list", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class EmployeesView extends VerticalLayout {

    private final EmployeeService employeeService;
    private final Grid<Employee> grid;
    private final CurrentUserService currentUserService;

    private FormLayout editForm = new FormLayout();
    private TextField id = new TextField("ID");
    private TextField editSurname = new TextField("Фамилия");
    private TextField editName = new TextField("Имя");
    private TextField editPatronymic = new TextField("Отчество");
    private TextField editPost = new TextField("Должность");
    private Button editButton = new Button("Сохранить", VaadinIcon.CHECK.create());

    public EmployeesView(EmployeeService employeeService, CurrentUserService currentUserService) {
        this.employeeService = employeeService;
        this.currentUserService = currentUserService;
        this.grid = new Grid<>(Employee.class, false);

        // Общие стили для страницы
        setPadding(true);
        setSpacing(false);
        setSizeFull();
        addClassName(LumoUtility.Padding.LARGE);

        setupGrid();
        updateGrid();

        // Стилизация кнопки добавления
        Button addEmployee = new Button("Добавить сотрудника", VaadinIcon.PLUS.create());
        addEmployee.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addEmployee.addClassName(LumoUtility.Margin.Bottom.LARGE);
        addEmployee.addClickListener(e -> {
            UI.getCurrent().navigate("/employee-registration");
        });

        // Стилизация формы редактирования
        editForm.setWidth("400px");
        editForm.addClassNames(
                LumoUtility.Padding.LARGE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.SMALL,
                LumoUtility.Background.BASE
        );

        id.setVisible(false);
        editSurname.setRequired(true);
        editName.setRequired(true);
        editPatronymic.setRequired(true);
        editPost.setRequired(true);

        editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        editButton.addClassName(LumoUtility.Margin.Top.MEDIUM);
        editButton.getStyle().set("margin-left", "auto");

        editButton.addClickListener(e->{
            if (editSurname.isEmpty() || editName.isEmpty() || editPatronymic.isEmpty() || editPost.isEmpty()){
                showError("Заполните все поля");
                return;
            }
            
            Employee updateEmployee = employeeService.findById(Long.valueOf(id.getValue())).orElse(null);
            if (updateEmployee != null) {
                updateEmployee.setSurname(editSurname.getValue());
                updateEmployee.setName(editName.getValue());
                updateEmployee.setPatronymic(editPatronymic.getValue());
                updateEmployee.setPost(editPost.getValue());
                employeeService.update(updateEmployee);
                updateGrid();
                editForm.setVisible(false);
            }
        });

        grid.addCellFocusListener(e->{
            id.setValue(String.valueOf(e.getItem().map(Employee::getId).orElse(null)));
            editSurname.setValue(e.getItem().map(Employee::getSurname).orElse("Не доступно"));
            editName.setValue(e.getItem().map(Employee::getName).orElse("Не доступно"));
            editPatronymic.setValue(e.getItem().map(Employee::getPatronymic).orElse("Не доступно"));
            editPost.setValue(e.getItem().map(Employee::getPost).orElse("Не доступно"));
        });

        styleTextField(editSurname);
        styleTextField(editName);
        styleTextField(editPatronymic);
        styleTextField(editPost);

        editForm.add(id, editSurname, editName, editPatronymic, editPost, editButton);
        editForm.setVisible(false);

        HorizontalLayout buttonLayout = new HorizontalLayout(addEmployee);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

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

        grid.addColumn(Employee::getId)
                .setHeader("ID")
                .setSortable(true)
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(Employee::getSurname)
                .setHeader("Фамилия")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(Employee::getName)
                .setHeader("Имя")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(Employee::getPatronymic)
                .setHeader("Отчество")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(Employee::getPost)
                .setHeader("Должность")
                .setSortable(true)
                .setAutoWidth(true);

        GridContextMenu<Employee> contextMenu = grid.addContextMenu();

        Button editMenuItem = new Button("Изменить", VaadinIcon.EDIT.create());
        editMenuItem.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        contextMenu.addItem(editMenuItem, e->{
            if(editForm.isVisible()){
                editForm.setVisible(false);
            }else if(!editForm.isVisible()){
                editForm.setVisible(true);
                id.setValue(String.valueOf(e.getItem().map(Employee::getId).orElse(null)));
                editSurname.setValue(e.getItem().map(Employee::getSurname).orElse("Не доступно"));
                editName.setValue(e.getItem().map(Employee::getName).orElse("Не доступно"));
                editPatronymic.setValue(e.getItem().map(Employee::getPatronymic).orElse("Не доступно"));
                editPost.setValue(e.getItem().map(Employee::getPost).orElse("Не доступно"));
            }
        });

        if(currentUserService.getCurrentUserType().equals(UserType.ADMIN)) {
            Button deleteMenuItem = new Button("Удалить", VaadinIcon.TRASH.create());
            deleteMenuItem.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            contextMenu.addItem(deleteMenuItem, e -> {
                employeeService.delete(e.getItem().map(Employee::getId).orElse(null));
                updateGrid();
            });
        }
    }

    private void updateGrid(){
        List<Employee> employees = employeeService.findAll();
        grid.setItems(employees);
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