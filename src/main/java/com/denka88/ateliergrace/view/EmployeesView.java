package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.Employee;
import com.denka88.ateliergrace.model.UserType;
import com.denka88.ateliergrace.service.CurrentUserService;
import com.denka88.ateliergrace.service.EmployeeService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

        add(grid, addEmployee);
    }

    private void setupGrid(){
        grid.setClassName("force-focus-outline");

        grid.addColumn(Employee::getId).setHeader("ID").setSortable(true);
        grid.addColumn(Employee::getSurname).setHeader("Фамилия").setSortable(true);
        grid.addColumn(Employee::getName).setHeader("Имя").setSortable(true);
        grid.addColumn(Employee::getPatronymic).setHeader("Отчество").setSortable(true);
        grid.addColumn(Employee::getPost).setHeader("Должность").setSortable(true);
        if(currentUserService.getCurrentUserType().equals(UserType.ADMIN)){
            grid.addColumn(new ComponentRenderer<>(Button::new, (button, employee) -> {
                button.addThemeVariants(ButtonVariant.LUMO_ICON,
                        ButtonVariant.LUMO_ERROR,
                        ButtonVariant.LUMO_TERTIARY);
                button.addClickListener(e -> {
                    employeeService.delete(employee.getId());
                    updateGrid();
                });
                button.setIcon(new Icon(VaadinIcon.TRASH));
            })).setHeader("Действие");
        }
    }

    private List<Employee> updateGrid(){
        List<Employee> employees = employeeService.findAll();
        grid.setItems(employees);
        return employees;
    }
}
