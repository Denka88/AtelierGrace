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
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.textfield.NumberField;
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
                VerticalLayout actions = new VerticalLayout();

                Button takeOrderBtn = new Button("Взять заказ");

                Button confirm = new Button("Подтвердить");

                Popover setCost = new Popover();
                setCost.setWidth("210px");
                setCost.setModal(true);
                setCost.setBackdropVisible(true);

                NumberField cost = new NumberField("Стоимость заказа");

                setCost.add(cost);
                setCost.add(confirm);

                confirm.addClickListener(e -> {
                    order.setCost(Float.parseFloat(cost.getValue().toString()));
                    orderService.takeOrder(order);
                    updateGrid();
                    Notification.show("Вы взяли заказ #" + order.getId())
                            .setPosition(Notification.Position.TOP_END);
                });

                setCost.setTarget(takeOrderBtn);
                boolean alreadyTaken = order.getOrderEmployees().stream()
                        .anyMatch(oe -> oe.getEmployee().getId().equals(currentUserService.getCurrentUserId()));
                takeOrderBtn.setText("Взять заказ");
                takeOrderBtn.setVisible(!alreadyTaken);

                // Кнопка "Завершить"
                Button completeBtn = new Button("Завершить");
                completeBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                completeBtn.setVisible(alreadyTaken);
                completeBtn.setEnabled(alreadyTaken && order.getStatus() != Status.COMPLETED);
                completeBtn.addClickListener(e -> {
                    orderService.completeOrder(order.getId());
                    updateGrid();
                    Notification.show("Заказ #" + order.getId() + " завершен")
                            .setPosition(Notification.Position.TOP_END);
                });

                actions.add(takeOrderBtn, completeBtn);
                actions.setSpacing(false);
                actions.setPadding(false);

                return actions;
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
