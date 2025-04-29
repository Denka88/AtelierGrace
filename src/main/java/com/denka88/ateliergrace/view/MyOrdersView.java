package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.*;
import com.denka88.ateliergrace.service.*;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.stream.Collectors;

@Route(value = "my-orders", layout = MainLayout.class)
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

        // Общие стили для страницы
        setPadding(true);
        setSpacing(false);
        setSizeFull();
        addClassName(LumoUtility.Padding.LARGE);

        setupGrid();
        updateGrid();

        // Контейнер для сетки
        Div content = new Div(grid);
        content.setSizeFull();
        add(content);
    }

    private void setupGrid(){
        grid.addClassName("force-focus-outline");
        grid.addThemeVariants(
                com.vaadin.flow.component.grid.GridVariant.LUMO_WRAP_CELL_CONTENT,
                com.vaadin.flow.component.grid.GridVariant.LUMO_COLUMN_BORDERS,
                com.vaadin.flow.component.grid.GridVariant.LUMO_ROW_STRIPES
        );
        grid.setHeightFull();

        // Стилизация колонок
        grid.addColumn(Order::getId)
                .setHeader("Номер заказа")
                .setSortable(true)
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(Order::getOrderName)
                .setHeader("Название")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(Order::getType)
                .setHeader("Тип заказа")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(Order::getOrderDate)
                .setHeader("Дата создания")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(order -> {
                    if (order.getOrderEmployees() == null || order.getOrderEmployees().isEmpty()) {
                        return "Не назначено";
                    }
                    return order.getOrderEmployees().stream()
                            .map(oe -> oe.getDateOfReady() != null ?
                                    oe.getDateOfReady().toString() : "Не указана")
                            .collect(Collectors.joining(", "));
                }).setHeader("Дата готовности")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(Order::getCost)
                .setHeader("Стоимость")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(Order::getStatus)
                .setHeader("Статус")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(order -> {
                    if(order.getOrderEmployees() == null || order.getOrderEmployees().isEmpty()){
                        return "Не назначены";
                    }
                    return order.getOrderEmployees().stream()
                            .map(OrderEmployee::getEmployee)
                            .map(Employee::toString)
                            .collect(Collectors.joining(", "));
                }).setHeader("Сотрудники")
                .setAutoWidth(true);

        grid.addColumn(order -> {
                    if (order.getMaterials() == null || order.getMaterials().isEmpty()) {
                        return "Не назначены";
                    }
                    return order.getMaterials().stream()
                            .map(Material::getName)
                            .collect(Collectors.joining(", "));
                }).setHeader("Материалы")
                .setAutoWidth(true);
    }

    private void updateGrid(){
        currentUserService.getCurrentClient().ifPresent(client -> {
            List<Order> orders = orderService.findByClientId(client.getId());
            grid.setItems(orders);
        });
    }
}
