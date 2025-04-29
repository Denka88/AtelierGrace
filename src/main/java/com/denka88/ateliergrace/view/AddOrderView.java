package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.model.*;
import com.denka88.ateliergrace.service.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@PageTitle("Создать заказ")
@Route("add-order")
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

        TextField orderName = new TextField("Название заказа");
        TextField type = new TextField("Тип заказа");
        MultiSelectComboBox<Material> materials = new MultiSelectComboBox<>("Материалы");

        materials.setItems(materialService.findAll());
        materials.setItemLabelGenerator(Material::getName);
        materials.setWidth("100%");

        Button create = new Button("Создать");

        create.addClickListener(e -> {

            if (orderName.isEmpty() || type.isEmpty()) {
                Notification.show("Заполните все обязательные поля", 3000,
                        Notification.Position.MIDDLE);
                return;
            }
            
            Order order = new Order();
            order.setOrderName(orderName.getValue());
            order.setType(type.getValue());
            
            Set<Material> materialSet = new HashSet<>();
            for (Material material : materials.getValue()) {
                materialSet.add(materialService.findById(material.getId())
                        .orElseThrow(() -> new RuntimeException("Материал не найден")));
            }
            order.setMaterials(materialSet);

            try {
                Order savedOrder = orderService.save(order);
                Notification.show(String.format(
                                "Заказ #%d '%s' успешно создан!",
                                savedOrder.getId(), savedOrder.getOrderName()),
                        5000, Notification.Position.MIDDLE);
                getUI().ifPresent(ui -> ui.navigate("my-orders"));
            } catch (Exception ex) {
                Notification.show("Ошибка при создании заказа: " + ex.getMessage(),
                        5000, Notification.Position.MIDDLE);
            }
            getUI().ifPresent(ui -> ui.navigate("main"));
        });


        FormLayout form = new FormLayout();

        form.add(orderName, type, materials, create);
        form.setWidth("30%");
        form.getStyle().set("margin", "auto");
        add(form);
        setAlignItems(Alignment.CENTER);
    }
}
