package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.model.Employee;
import com.denka88.ateliergrace.model.Material;
import com.denka88.ateliergrace.model.Order;
import com.denka88.ateliergrace.model.OrderEmployee;
import com.denka88.ateliergrace.service.CurrentUserService;
import com.denka88.ateliergrace.service.OrderService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.stream.Collectors;

@Route("my-orders")
@PageTitle("Мои заказы")
@RolesAllowed("CLIENT")
public class MyOrdersView extends VerticalLayout {

    private final OrderService orderService;
    private final CurrentUserService currentUserService;
    private final Grid<Order> grid;

    public MyOrdersView(OrderService orderService, CurrentUserService currentUserService) {
        this.orderService = orderService;
        this.currentUserService = currentUserService;
        this.grid = new Grid<>(Order.class, false);    
        
        setupGrid();
        updateGrid();
        
        add(grid);
    }
    
    private void setupGrid(){
        grid.setClassName("force-focus-outline");
        
        grid.addColumn(Order::getId).setHeader("Номер заказа").setSortable(true).setAutoWidth(true);
        grid.addColumn(Order::getOrderName).setHeader("Название").setSortable(true).setAutoWidth(true);
        grid.addColumn(Order::getType).setHeader("Тип заказа").setSortable(true).setAutoWidth(true);
        grid.addColumn(Order::getOrderDate).setHeader("Дата создания").setSortable(true).setAutoWidth(true);
        grid.addColumn(order -> {
            if (order.getOrderEmployees() == null || order.getOrderEmployees().isEmpty()) {
                return "Не назначено";
            }
            return order.getOrderEmployees().stream()
                    .map(oe -> oe.getDateOfReady() != null ?
                            oe.getDateOfReady().toString() : "Не указана")
                    .collect(Collectors.joining(", "));
        }).setHeader("Дата готовности").setSortable(true).setAutoWidth(true);
        grid.addColumn(Order::getCost).setHeader("Стоимость").setSortable(true).setAutoWidth(true);
        grid.addColumn(Order::getStatus).setHeader("Статус").setSortable(true).setAutoWidth(true);
        grid.addColumn(order -> {
            if(order.getOrderEmployees() == null || order.getOrderEmployees().isEmpty()){
                return "Не назначены";
            }
            return order.getOrderEmployees().stream()
                    .map(OrderEmployee::getEmployee)
                    .map(Employee::toString)
                    .collect(Collectors.joining(", "));
        }).setHeader("Сотрудники").setAutoWidth(true);
        
        grid.addColumn(order -> {
            if (order.getMaterials() == null || order.getMaterials().isEmpty()) {
                return "Не назначены";
            }
            return order.getMaterials().stream()
                    .map(Material::getName)
                    .collect(Collectors.joining(", "));
        }).setHeader("Материалы").setAutoWidth(true);
    }
    
    private void updateGrid(){
        currentUserService.getCurrentClient().ifPresent(client ->{
            List<Order> orders = orderService.findByClientId(client.getId());
            grid.setItems(orders);
        });
    }
}
