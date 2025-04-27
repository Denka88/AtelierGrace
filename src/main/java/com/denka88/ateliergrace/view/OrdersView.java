package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.*;
import com.denka88.ateliergrace.service.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
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
    private final CurrentUserService currentUserService;

    public OrdersView(OrderService orderService, CurrentUserService currentUserService) {
        this.orderService = orderService;
        this.currentUserService = currentUserService;
        this.grid = new Grid<>(Order.class, false);
        
        setupGrid();
        updateGrid();
        
        add(grid);
    }
    
    private void setupGrid(){
        grid.setClassName("force-focus-outline");
        
        grid.addColumn(Order::getId).setHeader("ID").setSortable(true).setAutoWidth(true);
        grid.addColumn(Order::getClient).setHeader("Клиент").setSortable(true).setAutoWidth(true);
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
        if(currentUserService.getCurrentUserType().equals(UserType.EMPLOYEE)) {
            grid.addColumn(new ComponentRenderer<>(order -> {
                Button takeOrderBtn = new Button("Взять заказ");
                takeOrderBtn.addClickListener(e -> {
                    orderService.takeOrder(order);
                    updateGrid();
                    Notification.show("Вы взяли заказ #" + order.getId())
                            .setPosition(Notification.Position.TOP_END);
                });
                boolean alreadyTaken = order.getOrderEmployees().stream()
                        .anyMatch(oe -> oe.getEmployee().getId().equals(currentUserService.getCurrentUserId()));
                takeOrderBtn.setEnabled(!alreadyTaken);
                takeOrderBtn.setText(alreadyTaken ? "Вы уже взяли" : "Взять заказ");
                return takeOrderBtn;
            })).setHeader("Действия");
        }
        if(currentUserService.getCurrentUserType().equals(UserType.ADMIN)){
            grid.addColumn(new ComponentRenderer<>(Button::new, (button, order) -> {
                button.addThemeVariants(ButtonVariant.LUMO_ICON,
                        ButtonVariant.LUMO_ERROR,
                        ButtonVariant.LUMO_TERTIARY);
                button.addClickListener(e -> {
                    orderService.delete(order.getId());
                    updateGrid();
                });
                button.setIcon(new Icon(VaadinIcon.TRASH));
            })).setHeader("Действие");
        }
    }
    
    private List<Order> updateGrid(){
        List<Order> orders = orderService.findAll();
        grid.setItems(orders);
        return orders;
    }
    
    
    
}
