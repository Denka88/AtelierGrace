package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.*;
import com.denka88.ateliergrace.service.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "orders-list", layout = MainLayout.class)
@PageTitle("Заказы")
@RolesAllowed({"ADMIN", "EMPLOYEE"})
public class OrdersView extends VerticalLayout {

    private final OrderService orderService;
    private final MaterialService materialService;
    private final Grid<Order> grid;
    private final CurrentUserService currentUserService;

    private FormLayout editForm = new FormLayout();
    private TextField id = new TextField("ID");
    private TextField editOrderName = new TextField("Название заказа");
    private TextField editType = new TextField("Тип заказа");
    private MultiSelectComboBox<Material> editMaterials = new MultiSelectComboBox<>("Материалы");
    private Button editButton = new Button("Сохранить изменения");

    public OrdersView(OrderService orderService, MaterialService materialService,
                      CurrentUserService currentUserService) {
        this.orderService = orderService;
        this.materialService = materialService;
        this.currentUserService = currentUserService;
        this.grid = new Grid<>(Order.class, false);

        setupGrid();
        updateGrid();

        // Настройка формы редактирования
        editForm.setWidth("400px");
        id.setVisible(false);

        // Настройка комбобокса материалов
        editMaterials.setItems(materialService.findAll());
        editMaterials.setItemLabelGenerator(Material::getName);

        editButton.addClickListener(e -> {
            Order updateOrder = orderService.findById(Long.valueOf(id.getValue())).orElse(null);
            if (updateOrder != null) {
                updateOrder.setOrderName(editOrderName.getValue());
                updateOrder.setType(editType.getValue());
                updateOrder.setMaterials(new HashSet<>(editMaterials.getSelectedItems()));
                orderService.update(updateOrder);
                updateGrid();
                editForm.setVisible(false);
            }
        });

        grid.addCellFocusListener(e -> {
            id.setValue(String.valueOf(e.getItem().map(Order::getId).orElse(null)));
            editOrderName.setValue(e.getItem().map(Order::getOrderName).orElse(null));
            editType.setValue(e.getItem().map(Order::getType).orElse(null));
            editMaterials.clear();
        });

        editForm.add(id, editOrderName, editType, editMaterials, editButton);
        editForm.setVisible(false);

        add(grid, editForm);
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

        // Контекстное меню для редактирования
        GridContextMenu<Order> contextMenu = grid.addContextMenu();
        contextMenu.addItem("Изменить", e -> {
            if (editForm.isVisible()) {
                editForm.setVisible(false);
            } else {
                editForm.setVisible(true);
                id.setValue(String.valueOf(e.getItem().map(Order::getId).orElse(null)));
                editOrderName.setValue(e.getItem().map(Order::getOrderName).orElse(null));
                editType.setValue(e.getItem().map(Order::getType).orElse(null));
                editMaterials.clear();
            }
        });
        
        if(currentUserService.getCurrentUserType() == UserType.ADMIN){
            contextMenu.addItem("Удалить", e->{
                orderService.delete(e.getItem().map(Order::getId).orElse(null));
                updateGrid();
            });
        }

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
    }

    private void updateGrid(){
        List<Order> orders = orderService.findAll();
        grid.setItems(orders);
    }
}
