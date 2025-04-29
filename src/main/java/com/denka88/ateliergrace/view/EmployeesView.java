package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.Employee;
import com.denka88.ateliergrace.model.Material;
import com.denka88.ateliergrace.model.UserType;
import com.denka88.ateliergrace.service.CurrentUserService;
import com.denka88.ateliergrace.service.EmployeeService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.ArrayList;
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
    private Button editButton = new Button("Сохранить изменения");

    public EmployeesView(EmployeeService employeeService, CurrentUserService currentUserService) {
        this.employeeService = employeeService;
        this.currentUserService = currentUserService;
        this.grid = new Grid<>(Employee.class, false);

        setupGrid();
        updateGrid();

        Button addEmployee = new Button("Добавить сотрудника");
        addEmployee.addClickListener(e -> {
            UI.getCurrent().navigate("/employee-registration");
        });

        editForm.setWidth("400px");
        
        id.setVisible(false);
        
        editButton.addClickListener(e->{
            Employee updateEmployee = employeeService.findById(Long.valueOf(id.getValue())).orElse(null);
            updateEmployee.setSurname(editSurname.getValue());
            updateEmployee.setName(editName.getValue());
            updateEmployee.setPatronymic(editPatronymic.getValue());
            updateEmployee.setPost(editPost.getValue());
            employeeService.update(updateEmployee);
            updateGrid();
            editForm.setVisible(false);
        });
        
        grid.addCellFocusListener(e->{
            id.setValue(String.valueOf(e.getItem().map(Employee::getId).orElse(null)));
            editSurname.setValue(e.getItem().map(Employee::getSurname).orElse("Не доступно"));
            editName.setValue(e.getItem().map(Employee::getName).orElse("Не доступно"));
            editPatronymic.setValue(e.getItem().map(Employee::getPatronymic).orElse("Не доступно"));
            editPost.setValue(e.getItem().map(Employee::getPost).orElse("Не доступно"));
        });
        
        editForm.add(id, editSurname, editName, editPatronymic, editPost, editButton);
        editForm.setVisible(false);
        
        add(grid, addEmployee, editForm);
    }

    private void setupGrid(){
        grid.setClassName("force-focus-outline");

        grid.addColumn(Employee::getId).setHeader("ID").setSortable(true).setAutoWidth(true);
        grid.addColumn(Employee::getSurname).setHeader("Фамилия").setSortable(true).setAutoWidth(true);
        grid.addColumn(Employee::getName).setHeader("Имя").setSortable(true).setAutoWidth(true);
        grid.addColumn(Employee::getPatronymic).setHeader("Отчество").setSortable(true).setAutoWidth(true);
        grid.addColumn(Employee::getPost).setHeader("Должность").setSortable(true).setAutoWidth(true);

        GridContextMenu<Employee> contextMenu = grid.addContextMenu();
        contextMenu.addItem("Изменить", e->{
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
            contextMenu.addItem("Удалить", e -> {
                employeeService.delete(e.getItem().map(Employee::getId).orElse(null));
                updateGrid();
            });
        }
    }

    private List<Employee> updateGrid(){
        List<Employee> employees = employeeService.findAll();
        grid.setItems(employees);
        return employees;
    }
}
