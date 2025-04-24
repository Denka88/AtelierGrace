package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.Employee;
import com.denka88.ateliergrace.model.Material;
import com.denka88.ateliergrace.model.Order;
import com.denka88.ateliergrace.model.OrderEmployee;
import com.denka88.ateliergrace.service.ClientService;
import com.denka88.ateliergrace.service.EmployeeService;
import com.denka88.ateliergrace.service.OrderEmployeeService;
import com.denka88.ateliergrace.service.OrderService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.stream.Collectors;

@Route(value = "orders-list", layout = MainLayout.class)
@PageTitle("Заказы")
@RolesAllowed({"ADMIN", "EMPLOYEE"})
public class OrdersView extends VerticalLayout {
    
    private final OrderService orderService;
    private final Grid<Order> grid;

    public OrdersView(OrderService orderService) {
        this.orderService = orderService;
        this.grid = new Grid<>(Order.class, false);
        
        setupGrid();
        updateGrid();
        
        add(grid);
    }
    
    private void setupGrid(){
        grid.setClassName("force-focus-outline");
        
        grid.addColumn(Order::getId).setHeader("ID").setSortable(true);
        grid.addColumn(Order::getClient).setHeader("Client").setSortable(true);
        grid.addColumn(Order::getOrderName).setHeader("Название").setSortable(true);
        grid.addColumn(Order::getType).setHeader("Тип заказа").setSortable(true);
        grid.addColumn(Order::getOrderDate).setHeader("Дата создания").setSortable(true);
        grid.addColumn(Order::getCost).setHeader("Стоимость").setSortable(true);
        grid.addColumn(Order::getStatus).setHeader("Статус").setSortable(true);
        
        grid.addColumn(order ->{
            if(order.getOrderEmployees() == null || order.getOrderEmployees().isEmpty()){
                return "Не назначены";
            }
            return order.getOrderEmployees().stream()
                    .map(OrderEmployee::getEmployee)
                    .map(Employee::toString)
                    .collect(Collectors.joining(", "));
        }).setHeader("Сотрудники");
        
        grid.addColumn(order -> {
            if (order.getMaterials() == null || order.getMaterials().isEmpty()) {
                return "Не назначены";
            }
            return order.getMaterials().stream()
                    .map(Material::getName)
                    .collect(Collectors.joining(", "));
        }).setHeader("Материалы");
    }
    
    private List<Order> updateGrid(){
        List<Order> orders = orderService.findAll();
        grid.setItems(orders);
        return orders;
    }
    
    
    
}
