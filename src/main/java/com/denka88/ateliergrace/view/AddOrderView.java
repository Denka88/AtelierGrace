package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.*;
import com.denka88.ateliergrace.service.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;

import java.util.HashSet;
import java.util.Set;

@PageTitle("Создать заказ")
@Route(value = "add-order", layout = MainLayout.class)
@RolesAllowed("CLIENT")
public class AddOrderView extends VerticalLayout {

    private final OrderService orderService;
    private final MaterialService materialService;
    private final CurrentUserService currentUserService;

    public AddOrderView(OrderService orderService, MaterialService materialService,
                        CurrentUserService currentUserService) {
        this.orderService = orderService;
        this.materialService = materialService;
        this.currentUserService = currentUserService;

        // Общие стили для страницы
        setPadding(true);
        setSpacing(false);
        setSizeFull();
        addClassName(LumoUtility.Padding.LARGE);

        // Создание формы
        FormLayout form = new FormLayout();
        form.addClassName(LumoUtility.Padding.LARGE);
        form.addClassName(LumoUtility.BorderRadius.LARGE);
        form.addClassName(LumoUtility.BoxShadow.SMALL);
        form.addClassName(LumoUtility.Background.BASE);
        form.setWidth("500px");
        form.getStyle().set("margin", "auto");

        // Поля формы
        TextField orderName = new TextField("Название заказа");
        orderName.setWidthFull();
        orderName.setRequired(true);
        orderName.setPlaceholder("Введите название заказа");

        TextField type = new TextField("Тип заказа");
        type.setWidthFull();
        type.setRequired(true);
        type.setPlaceholder("Укажите тип заказа");

        MultiSelectComboBox<Material> materials = new MultiSelectComboBox<>("Материалы");
        materials.setItems(materialService.findAll());
        materials.setItemLabelGenerator(Material::getName);
        materials.setWidthFull();
        materials.setPlaceholder("Выберите материалы");

        // Стилизация кнопки
        Button create = new Button("Создать заказ", VaadinIcon.CHECK.create());
        create.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        create.setWidthFull();

        create.addClickListener(e -> {
            if (orderName.isEmpty() || type.isEmpty()) {
                Notification.show("Заполните все обязательные поля", 3000,
                                Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            Order order = new Order();
            order.setOrderName(orderName.getValue());
            order.setType(type.getValue());
            order.setClient(currentUserService.getCurrentClient());

            Set<Material> materialSet = new HashSet<>(materials.getValue());
            order.setMaterials(materialSet);

            try {
                Order savedOrder = orderService.save(order);
                Notification.show(String.format(
                                        "Заказ #%d '%s' успешно создан!",
                                        savedOrder.getId(), savedOrder.getOrderName()),
                                5000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                UI.getCurrent().navigate("my-orders");
            } catch (Exception ex) {
                Notification.show("Ошибка при создании заказа: " + ex.getMessage(),
                                5000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        // Добавление элементов в форму
        form.add(orderName, type, materials, create);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
        );

        // Центрирование формы
        Div container = new Div(form);
        container.setWidthFull();
        container.addClassName(LumoUtility.Display.FLEX);
        container.addClassName(LumoUtility.JustifyContent.CENTER);

        add(container);
        setAlignItems(Alignment.CENTER);
    }
}