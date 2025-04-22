package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.Order;
import com.denka88.ateliergrace.service.ClientService;
import com.denka88.ateliergrace.service.EmployeeService;
import com.denka88.ateliergrace.service.OrderService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "orders-list", layout = MainLayout.class)
@PageTitle("Заказы")
public class OrdersView extends VerticalLayout {
    
    private final OrderService orderService;
    private final ClientService clientService;
    private final EmployeeService employeeService;
    private final Grid<Order> grid;

    public OrdersView(OrderService orderService, ClientService clientService, EmployeeService employeeService) {
        this.orderService = orderService;
        this.clientService = clientService;
        this.employeeService = employeeService;
        this.grid = new Grid<>(Order.class, false);
        
        
    }
    
    private void setupGrid(){
        grid.setClassName("force-focus-outline");
        
        grid.addColumn(Order::getId).setHeader("ID").setSortable(true);
        grid.addColumn(Order::getClient).setHeader("Client").setSortable(true);
        
    }
    
    
}
