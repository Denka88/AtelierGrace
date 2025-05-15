package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.*;
import com.denka88.ateliergrace.service.*;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

import java.util.List;
import java.util.stream.Collectors;

@Route(value = "search", layout = MainLayout.class)
@PageTitle("Результаты поиска")
@PermitAll
public class SearchResultsView extends VerticalLayout implements HasUrlParameter<String> {

    private final OrderService orderService;
    private final ClientService clientService;
    private final EmployeeService employeeService;
    private final MaterialService materialService;
    private final OrganizationService organizationService;

    public SearchResultsView(OrderService orderService,
                             ClientService clientService,
                             EmployeeService employeeService,
                             MaterialService materialService,
                             OrganizationService organizationService) {
        this.orderService = orderService;
        this.clientService = clientService;
        this.employeeService = employeeService;
        this.materialService = materialService;
        this.organizationService = organizationService;

        setPadding(true);
        setSpacing(false);
        setSizeFull();
        addClassName(LumoUtility.Padding.LARGE);
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        removeAll();

        if (parameter == null || parameter.trim().isEmpty()) {
            add(new H2("Введите поисковый запрос"));
            return;
        }

        String searchQuery = parameter.toLowerCase();
        add(new H2("Результаты поиска по запросу: \"" + searchQuery + "\""));

        // Поиск по заказам
        List<Order> orders = orderService.findAll().stream()
                .filter(order -> order.getOrderName().toLowerCase().contains(searchQuery) ||
                        order.getDescription().toLowerCase().contains(searchQuery))
                .toList();

        if (!orders.isEmpty()) {
            add(new H2("Заказы"));
            Grid<Order> orderGrid = new Grid<>(Order.class, false);
            orderGrid.addThemeVariants(
                    com.vaadin.flow.component.grid.GridVariant.LUMO_WRAP_CELL_CONTENT,
                    com.vaadin.flow.component.grid.GridVariant.LUMO_COLUMN_BORDERS,
                    com.vaadin.flow.component.grid.GridVariant.LUMO_ROW_STRIPES
            );
            orderGrid.setHeightFull();

            orderGrid.addColumn(Order::getId)
                    .setHeader("ID")
                    .setSortable(true)
                    .setAutoWidth(true)
                    .setFlexGrow(0);

            orderGrid.addColumn(Order::getClient)
                    .setHeader("Клиент")
                    .setSortable(true)
                    .setAutoWidth(true);

            orderGrid.addColumn(Order::getOrderName)
                    .setHeader("Название")
                    .setSortable(true)
                    .setAutoWidth(true);

            orderGrid.addColumn(Order::getDescription)
                    .setHeader("Описание заказа")
                    .setSortable(true)
                    .setAutoWidth(true);

            orderGrid.addColumn(Order::getOrderDate)
                    .setHeader("Дата создания")
                    .setSortable(true)
                    .setAutoWidth(true);

            orderGrid.addColumn(order -> {
                if (order.getOrderEmployees() == null || order.getOrderEmployees().isEmpty()) {
                    return "Не назначено";
                }
                return order.getOrderEmployees().stream()
                        .filter(oe -> oe.getDateOfReady() != null)
                        .findFirst()
                        .map(oe -> oe.getDateOfReady().toString())
                        .orElse("Не указана");
            }).setHeader("Дата готовности").setSortable(true).setAutoWidth(true);

            orderGrid.addColumn(Order::getCost)
                    .setHeader("Стоимость")
                    .setSortable(true)
                    .setAutoWidth(true);

            orderGrid.addColumn(Order::getStatus)
                    .setHeader("Статус")
                    .setSortable(true)
                    .setAutoWidth(true);

            orderGrid.addColumn(order -> {
                if(order.getOrderEmployees() == null || order.getOrderEmployees().isEmpty()){
                    return "Не назначены";
                }
                return order.getOrderEmployees().stream()
                        .map(OrderEmployee::getEmployee)
                        .map(Employee::toString)
                        .collect(Collectors.joining(", "));
            }).setHeader("Сотрудники");

            orderGrid.addColumn(order -> {
                if (order.getMaterials() == null || order.getMaterials().isEmpty()) {
                    return "Не назначены";
                }
                return order.getMaterials().stream()
                        .map(Material::getName)
                        .collect(Collectors.joining(", "));
            }).setHeader("Материалы");
            orderGrid.setItems(orders);
            add(orderGrid);
            // Здесь можно добавить Grid для отображения найденных заказов
        }

        // Поиск по клиентам
        List<Client> clients = clientService.findAll().stream()
                .filter(client -> client.getName().toLowerCase().contains(searchQuery) ||
                        client.getSurname().toLowerCase().contains(searchQuery) ||
                        client.getPatronymic().toLowerCase().contains(searchQuery) ||
                        client.getPhone().toLowerCase().contains(searchQuery))
                .toList();

        if (!clients.isEmpty()) {
            add(new H2("Клиенты"));
            Grid<Client> clientsGrid = new Grid<>(Client.class, false);
            clientsGrid.addThemeVariants(
                    com.vaadin.flow.component.grid.GridVariant.LUMO_WRAP_CELL_CONTENT,
                    com.vaadin.flow.component.grid.GridVariant.LUMO_COLUMN_BORDERS,
                    com.vaadin.flow.component.grid.GridVariant.LUMO_ROW_STRIPES
            );
            clientsGrid.setHeightFull();

            clientsGrid.addColumn(Client::getId)
                    .setHeader("ID")
                    .setSortable(true)
                    .setAutoWidth(true)
                    .setFlexGrow(0);

            clientsGrid.addColumn(Client::getSurname)
                    .setHeader("Фамилия")
                    .setSortable(true)
                    .setAutoWidth(true);

            clientsGrid.addColumn(Client::getName)
                    .setHeader("Имя")
                    .setSortable(true)
                    .setAutoWidth(true);

            clientsGrid.addColumn(Client::getPatronymic)
                    .setHeader("Отчество")
                    .setSortable(true)
                    .setAutoWidth(true);

            clientsGrid.addColumn(Client::getPhone)
                    .setHeader("Номер телефона")
                    .setSortable(true)
                    .setAutoWidth(true);
            clientsGrid.setItems(clients);
            add(clientsGrid);
            // Здесь можно добавить Grid для отображения найденных клиентов
        }
        
        List<Employee> employees = employeeService.findAll().stream()
                .filter(employee -> employee.getName().toLowerCase().contains(searchQuery) ||
                        employee.getPatronymic().toLowerCase().contains(searchQuery) ||
                        employee.getSurname().toLowerCase().contains(searchQuery) ||
                        employee.getPost().toLowerCase().contains(searchQuery))
                .toList();
        
        if (!employees.isEmpty()) {
            add(new H2("Сотрудники"));
            Grid<Employee> employeesGrid = new Grid<>(Employee.class, false);
            employeesGrid.addThemeVariants(
                    com.vaadin.flow.component.grid.GridVariant.LUMO_WRAP_CELL_CONTENT,
                    com.vaadin.flow.component.grid.GridVariant.LUMO_COLUMN_BORDERS,
                    com.vaadin.flow.component.grid.GridVariant.LUMO_ROW_STRIPES
            );
            employeesGrid.setHeightFull();

            employeesGrid.addColumn(Employee::getId)
                    .setHeader("ID")
                    .setSortable(true)
                    .setAutoWidth(true)
                    .setFlexGrow(0);

            employeesGrid.addColumn(Employee::getSurname)
                    .setHeader("Фамилия")
                    .setSortable(true)
                    .setAutoWidth(true);

            employeesGrid.addColumn(Employee::getName)
                    .setHeader("Имя")
                    .setSortable(true)
                    .setAutoWidth(true);

            employeesGrid.addColumn(Employee::getPatronymic)
                    .setHeader("Отчество")
                    .setSortable(true)
                    .setAutoWidth(true);

            employeesGrid.addColumn(Employee::getPost)
                    .setHeader("Должность")
                    .setSortable(true)
                    .setAutoWidth(true);
            employeesGrid.setItems(employees);
            add(employeesGrid);
        }
        
        List<Material> materials = materialService.findAll().stream()
                .filter(material -> material.getName().toLowerCase().contains(searchQuery)).toList();
        
        if (!materials.isEmpty()) {
            add(new H2("Материалы"));
            Grid<Material> materialsGrid = new Grid<>(Material.class, false);
            materialsGrid.addThemeVariants(
                    com.vaadin.flow.component.grid.GridVariant.LUMO_WRAP_CELL_CONTENT,
                    com.vaadin.flow.component.grid.GridVariant.LUMO_COLUMN_BORDERS,
                    com.vaadin.flow.component.grid.GridVariant.LUMO_ROW_STRIPES
            );
            materialsGrid.setHeightFull();

            materialsGrid.addColumn(Material::getId)
                    .setHeader("ID")
                    .setSortable(true)
                    .setAutoWidth(true)
                    .setFlexGrow(0);

            materialsGrid.addColumn(Material::getName)
                    .setHeader("Название")
                    .setSortable(true)
                    .setAutoWidth(true);

            materialsGrid.addColumn(Material::getValue)
                    .setHeader("На складе")
                    .setSortable(true)
                    .setAutoWidth(true);
            materialsGrid.setItems(materials);
            add(materialsGrid);
        }
        
        List<Organization> organizations = organizationService.findAll().stream()
                .filter(organization -> organization.getName().toLowerCase().contains(searchQuery) ||
                        organization.getAddress().toLowerCase().contains(searchQuery))
                .toList();

        if (!organizations.isEmpty()) {
            add(new H2("Поставщики"));
            Grid<Organization> organizationsGrid = new Grid<>(Organization.class, false);
            organizationsGrid.addThemeVariants(
                    com.vaadin.flow.component.grid.GridVariant.LUMO_WRAP_CELL_CONTENT,
                    com.vaadin.flow.component.grid.GridVariant.LUMO_COLUMN_BORDERS,
                    com.vaadin.flow.component.grid.GridVariant.LUMO_ROW_STRIPES
            );
            organizationsGrid.setHeightFull();

            organizationsGrid.addColumn(Organization::getId)
                    .setHeader("ID")
                    .setSortable(true)
                    .setAutoWidth(true)
                    .setFlexGrow(0);

            organizationsGrid.addColumn(Organization::getName)
                    .setHeader("Название")
                    .setSortable(true)
                    .setAutoWidth(true);

            organizationsGrid.addColumn(Organization::getAddress)
                    .setHeader("Адрес")
                    .setSortable(true)
                    .setAutoWidth(true);
            organizationsGrid.setItems(organizations);
            add(organizationsGrid);
        }
    }
}