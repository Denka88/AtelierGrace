package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.model.Auth;
import com.denka88.ateliergrace.model.Material;
import com.denka88.ateliergrace.model.Order;
import com.denka88.ateliergrace.model.Status;
import com.denka88.ateliergrace.service.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

@PageTitle("Создать заказ")
@Route("add-order")
@RolesAllowed("CLIENT")
public class AddOrderView extends VerticalLayout {

    private final OrderService orderService;
    private final MaterialService materialService;
    private final OrderEmployeeService orderEmployeeService;
    private final ClientService clientService;
    private final AuthService authService;

    public AddOrderView(OrderService orderService, MaterialService materialService,
                        OrderEmployeeService orderEmployeeService,
                        ClientService clientService, AuthService authService, AuthenticationContext authenticationContext) {
        this.orderService = orderService;
        this.materialService = materialService;
        this.orderEmployeeService = orderEmployeeService;
        this.clientService = clientService;
        this.authService = authService;

        TextField orderName = new TextField("Название заказа");
        TextField type = new TextField("Тип заказа");
        MultiSelectComboBox<Material> materials = new MultiSelectComboBox<>("Материалы");

        materials.setItems(materialService.findAll());
        materials.setItemLabelGenerator(Material::getName);

        materials.setWidth("100%");

        Button create = new Button("Создать");

        create.addClickListener(e -> {
            Order order = new Order();
            order.setClient(clientService.findById(1L));
            order.setOrderName(orderName.getValue());
            order.setType(type.getValue());
//            order.setMaterials(new HashSet<>(materials.getValue()));
            order.setOrderDate(LocalDate.now());
            order.setStatus(Status.PROGRESS);
            order.setOrderEmployees(Collections.singleton(orderEmployeeService.findFree()));
            orderService.save(order);
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
