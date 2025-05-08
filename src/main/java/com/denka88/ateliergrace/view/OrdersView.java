package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.*;
import com.denka88.ateliergrace.service.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.data.provider.ListDataProvider;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Route(value = "orders-list", layout = MainLayout.class)
@PageTitle("Заказы")
@RolesAllowed({"ADMIN", "EMPLOYEE"})
public class OrdersView extends VerticalLayout {

    private final OrderService orderService;
    private final MaterialService materialService;
    private final CurrentUserService currentUserService;
    private final EmployeeService employeeService;
    private final Grid<Order> grid;

    private FormLayout editForm = new FormLayout();
    private TextField id = new TextField("ID");
    private TextField editOrderName = new TextField("Название заказа");
    private TextField editType = new TextField("Тип заказа");
    private MultiSelectComboBox<Material> editMaterials = new MultiSelectComboBox<>("Материалы");
    private Button editButton = new Button("Сохранить", VaadinIcon.CHECK.create());

    private final TextArea detailsText = new TextArea();
    private final TextArea clientDetails = new TextArea();

    public OrdersView(OrderService orderService, MaterialService materialService,
                      CurrentUserService currentUserService, EmployeeService employeeService) {
        this.orderService = orderService;
        this.materialService = materialService;
        this.currentUserService = currentUserService;
        this.employeeService = employeeService;
        this.grid = new Grid<>(Order.class, false);

        setPadding(true);
        setSpacing(false);
        setSizeFull();
        addClassName(LumoUtility.Padding.LARGE);

        Button filterButton = new Button("Фильтры", VaadinIcon.FILTER.create());
        filterButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Popover filterPopover = new Popover();
        filterPopover.setWidth("300px");

        FormLayout filterForm = new FormLayout();
        filterForm.setWidthFull();

        ComboBox<Status> statusFilter = new ComboBox<>("Статус заказа");
        statusFilter.setItems(Status.values());
        statusFilter.setItemLabelGenerator(status -> {
            switch (status) {
                case PROGRESS: return "Выполняется";
                case COMPLETED: return "Выполнен";
                default: return status.toString();
            }
        });
        statusFilter.setClearButtonVisible(true);

        MultiSelectComboBox<Material> materialsFilter = new MultiSelectComboBox<>("Материалы в заказе");
        materialsFilter.setItems(materialService.findAll());
        materialsFilter.setItemLabelGenerator(Material::getName);
        materialsFilter.setClearButtonVisible(true);

        ComboBox<Employee> employeeFilter = new ComboBox<>("Сотрудник");
        employeeFilter.setItems(employeeService.findAll());
        employeeFilter.setItemLabelGenerator(employee ->
                String.format("%s %s", employee.getSurname(), employee.getName()));
        employeeFilter.setClearButtonVisible(true);

        Button applyFilters = new Button("Применить", VaadinIcon.CHECK.create());
        applyFilters.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button resetFilters = new Button("Сбросить", VaadinIcon.TRASH.create());
        resetFilters.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout buttonsLayout = new HorizontalLayout(applyFilters, resetFilters);
        buttonsLayout.setWidthFull();
        buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        filterForm.add(statusFilter, materialsFilter, employeeFilter, buttonsLayout);
        filterPopover.add(filterForm);
        filterPopover.setTarget(filterButton);

        applyFilters.addClickListener(e -> {
            List<Order> allOrders = orderService.findAll();
            ListDataProvider<Order> dataProvider = new ListDataProvider<>(allOrders);

            dataProvider.addFilter(order -> {
                boolean statusMatch = statusFilter.getValue() == null ||
                        order.getStatus() == statusFilter.getValue();

                boolean materialsMatch = materialsFilter.getSelectedItems().isEmpty();

                if (!materialsMatch) {
                    Set<Long> selectedMaterialIds = materialsFilter.getSelectedItems()
                            .stream()
                            .map(Material::getId)
                            .collect(Collectors.toSet());

                    materialsMatch = order.getMaterials().stream()
                            .map(Material::getId)
                            .anyMatch(selectedMaterialIds::contains);
                }
                boolean employeeMatch = employeeFilter.getValue() == null;
                if (!employeeMatch) {
                    Long selectedEmployeeId = employeeFilter.getValue().getId();
                    employeeMatch = order.getOrderEmployees().stream()
                            .anyMatch(oe -> oe.getEmployee().getId().equals(selectedEmployeeId));
                }

                return statusMatch && materialsMatch && employeeMatch;
            });

            grid.setDataProvider(dataProvider);
            filterPopover.close();
        });

        resetFilters.addClickListener(e -> {
            statusFilter.clear();
            materialsFilter.clear();
            employeeFilter.clear();
            grid.setItems(orderService.findAll());
            filterPopover.close();
        });

        Button addOrder = new Button("Создать заказ", VaadinIcon.PLUS.create());
        addOrder.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addOrder.addClassName(LumoUtility.Margin.Bottom.LARGE);
        addOrder.addClickListener(e->{
            UI.getCurrent().navigate("/add-order");
        });
        
        HorizontalLayout toolbar = new HorizontalLayout(filterButton, addOrder);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        detailsText.setLabel("Данные о заказе");
        detailsText.setWidthFull();
        detailsText.setReadOnly(true);        
        detailsText.setVisible(false);
        
        clientDetails.setLabel("Данные о заказчике");
        clientDetails.setWidthFull();
        clientDetails.setReadOnly(true);
        clientDetails.setVisible(false);


        ContextMenu closeDetailsOrder = new ContextMenu(detailsText);
        MenuItem closeOrder = closeDetailsOrder.addItem("Закрыть");
        closeOrder.setDisableOnClick(true);
        closeOrder.addClickListener(e->{
           detailsText.setVisible(false);
           closeOrder.setEnabled(true);
        });
        ContextMenu closeDetailsClient = new ContextMenu(clientDetails);
        MenuItem closeClient = closeDetailsClient.addItem("Закрыть");
        closeClient.setDisableOnClick(true);
        closeClient.addClickListener(e->{
            clientDetails.setVisible(false);
            closeClient.setEnabled(true);
        });

        setupGrid();
        updateGrid();

        editForm.setWidth("600px");
        editForm.addClassNames(
                LumoUtility.Padding.LARGE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.SMALL,
                LumoUtility.Background.BASE
        );
        editForm.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
        );

        id.setVisible(false);
        
        editOrderName.setMinLength(3);
        editOrderName.setRequired(true);
        
        editType.setMinLength(3);
        editType.setRequired(true);

        editMaterials.setItems(materialService.findAll());
        editMaterials.setItemLabelGenerator(Material::getName);
        editMaterials.setWidthFull();

        editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        editButton.addClassName(LumoUtility.Margin.Top.MEDIUM);
        editButton.getStyle().set("margin-left", "auto");

        editButton.addClickListener(e -> {
            if (editOrderName.isEmpty() || editMaterials.isEmpty() || editType.isEmpty()) {
                showError("Заполните все поля");
                return;
            }
            
            if (editOrderName.getValue().length() < 3 || editType.getValue().length() < 3){
                showError("Неподходящая длина. Минимум 3 символа");
                return;
            }
            
            
            Order updateOrder = orderService.findById(Long.valueOf(id.getValue())).orElse(null);
            if (updateOrder != null) {
                updateOrder.setOrderName(editOrderName.getValue());
                updateOrder.setDescription(editType.getValue());
                updateOrder.setMaterials(new HashSet<>(editMaterials.getSelectedItems()));
                orderService.update(updateOrder);
                updateGrid();
                editForm.setVisible(false);
                Notification.show("Заказ обновлен")
                        .setPosition(Notification.Position.TOP_END);
            }
        });

        grid.addCellFocusListener(e -> {
            id.setValue(String.valueOf(e.getItem().map(Order::getId).orElse(null)));
            editOrderName.setValue(e.getItem().map(Order::getOrderName).orElse("Не доступно"));
            editType.setValue(e.getItem().map(Order::getDescription).orElse("Не доступно"));
            editMaterials.clear();
            if (e.getItem().isPresent()) {
                editMaterials.select(e.getItem().get().getMaterials());
            }

            String detailsId = String.valueOf(e.getItem().map(Order::getId).orElse(null));
            String detailsClient = String.valueOf(e.getItem().map(Order::getClient).orElse(null));
            String detailsOrderName = String.valueOf(e.getItem().map(Order::getOrderName).orElse(null));
            String detailsDescription = String.valueOf(e.getItem().map(Order::getDescription).orElse(null));
            String detailsOrderDate = String.valueOf(e.getItem().map(Order::getOrderDate).orElse(null));
            String detailsCost = String.valueOf(e.getItem().map(Order::getCost).orElse(null));
            String detailsStatus = String.valueOf(e.getItem().map(Order::getStatus).orElse(null));
            String detailsEmployees = String.valueOf(e.getItem().map(Order::getOrderEmployees).orElse(null));
            String detailsMaterials = String.valueOf(e.getItem().map(Order::getMaterials).orElse(null));

            String id = String.valueOf(e.getItem().map(Order::getClient).map(Client::getId).orElse(null));
            String surname = e.getItem().map(Order::getClient).map(Client::getSurname).orElse(null);
            String name = e.getItem().map(Order::getClient).map(Client::getName).orElse(null);
            String patronymic = e.getItem().map(Order::getClient).map(Client::getPatronymic).orElse(null);
            String phone = e.getItem().map(Order::getClient).map(Client::getPhone).orElse(null);
            
            String clientDetailsContainer = String.format("ID: %s%nФамилия: %s%nИмя: %s%nОтчество: %s%nНомер телефона: %s%n", 
                    id, surname, name, patronymic, phone);
            String areaContainer = String.format("ID: %s%nЗаказчик: %s%nНазвание заказа: %s%nТип заказа: %s%nДата создания: %s%n" +
                    "Цена: %s%nСтатус заказа: %s%nСотрудники: %s%nМатериалы: %s%n", detailsId, detailsClient, detailsOrderName, detailsDescription, detailsOrderDate, detailsCost, detailsStatus, detailsEmployees, detailsMaterials);
            
            clientDetails.setValue(clientDetailsContainer);
            detailsText.setValue(areaContainer);
        });

        styleTextField(editOrderName);
        styleTextField(editType);

        Button close = new Button(new Icon("lumo", "cross"), (e) -> editForm.setVisible(false));

        HorizontalLayout formHeader = new HorizontalLayout();
        formHeader.getStyle().set("justify-content", "space-between");

        H3 title = new H3("Изменить заказ");

        formHeader.add(title, close);

        editForm.add(formHeader, id, editOrderName, editType, editMaterials, editButton);
        editForm.setVisible(false);
        
        
        HorizontalLayout formAndDetails = new HorizontalLayout(editForm, detailsText, clientDetails);
        formAndDetails.setWidthFull();
        formAndDetails.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        Div content = new Div(grid, formAndDetails);
        content.addClassName(LumoUtility.Display.FLEX);
        content.addClassName(LumoUtility.FlexDirection.COLUMN);
        content.addClassName(LumoUtility.Gap.LARGE);
        content.setSizeFull();

        add(toolbar, content);
    }

    private void setupGrid(){
        grid.addClassName("force-focus-outline");
        grid.addThemeVariants(
                com.vaadin.flow.component.grid.GridVariant.LUMO_WRAP_CELL_CONTENT,
                com.vaadin.flow.component.grid.GridVariant.LUMO_COLUMN_BORDERS,
                com.vaadin.flow.component.grid.GridVariant.LUMO_ROW_STRIPES
        );
        grid.setHeightFull();

        grid.addColumn(Order::getId)
                .setHeader("ID")
                .setSortable(true)
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(Order::getClient)
                .setHeader("Клиент")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(Order::getOrderName)
                .setHeader("Название")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(Order::getDescription)
                .setHeader("Описание заказа")
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
                    .filter(oe -> oe.getDateOfReady() != null)
                    .findFirst()
                    .map(oe -> oe.getDateOfReady().toString())
                    .orElse("Не указана");
        }).setHeader("Дата готовности").setSortable(true).setAutoWidth(true);

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
        }).setHeader("Сотрудники");

        grid.addColumn(order -> {
            if (order.getMaterials() == null || order.getMaterials().isEmpty()) {
                return "Не назначены";
            }
            return order.getMaterials().stream()
                    .map(Material::getName)
                    .collect(Collectors.joining(", "));
        }).setHeader("Материалы");
        
        GridContextMenu<Order> contextMenu = grid.addContextMenu();

        Button openDetails = new Button("Сведения о заказе", VaadinIcon.CHECK.create());
        openDetails.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        contextMenu.addItem(openDetails, e->{
            detailsText.setVisible(!detailsText.isVisible());
        });

        Button openClientDetails = new Button("Сведения о заказчике", VaadinIcon.CHECK.create());
        openClientDetails.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        
        contextMenu.addItem(openClientDetails, e->{
           clientDetails.setVisible(!clientDetails.isVisible()); 
        });

        Button editMenuItem = new Button("Изменить", VaadinIcon.EDIT.create());
        editMenuItem.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        
        contextMenu.addItem(editMenuItem, e -> {
            if (editForm.isVisible()) {
                editForm.setVisible(false);
            } else {
                editForm.setVisible(true);
                id.setValue(String.valueOf(e.getItem().map(Order::getId).orElse(null)));
                editOrderName.setValue(e.getItem().map(Order::getOrderName).orElse(null));
                editType.setValue(e.getItem().map(Order::getDescription).orElse(null));
                editMaterials.clear();
                if (e.getItem().isPresent()) {
                    editMaterials.select(e.getItem().get().getMaterials());
                }
            }
        });

        if(currentUserService.getCurrentUserType() == UserType.ADMIN){
            Button deleteMenuItem = new Button("Удалить", VaadinIcon.TRASH.create());
            deleteMenuItem.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            contextMenu.addItem(deleteMenuItem, e->{
                orderService.delete(e.getItem().map(Order::getId).orElse(null));
                updateGrid();
                Notification.show("Заказ удален")
                        .setPosition(Notification.Position.TOP_END);
            });
        }

        if(currentUserService.getCurrentUserType().equals(UserType.EMPLOYEE)) {
            grid.addColumn(new ComponentRenderer<>(order -> {
                VerticalLayout actions = new VerticalLayout();
                actions.setSpacing(false);
                actions.setPadding(false);
                actions.setWidthFull();

                Popover setCost = new Popover();
                setCost.setWidth("300px");
                setCost.addClassName(LumoUtility.Padding.LARGE);
                setCost.setModal(true);
                setCost.setBackdropVisible(true);

                NumberField cost = new NumberField("Стоимость заказа");
                cost.setWidthFull();
                cost.setMin(0);
                
                Button completeBtn = new Button("Завершить", VaadinIcon.CHECK_CIRCLE.create());
                completeBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
                completeBtn.setWidthFull();
                completeBtn.setEnabled(order.getStatus() != Status.COMPLETED);
                completeBtn.addClickListener(e -> {
                    orderService.completeOrder(order.getId());
                    updateGrid();
                    Notification.show("Заказ #" + order.getId() + " завершен")
                            .setPosition(Notification.Position.TOP_END);
                });

                actions.add(completeBtn);
                return actions;
            })).setHeader("Действия").setAutoWidth(true).setFlexGrow(0);
        }
    }

    private void updateGrid(){
        List<Order> orders = orderService.findAll();
        grid.setItems(orders);
    }

    private void styleTextField(TextField textField) {
        textField.setWidthFull();
        textField.addClassName(LumoUtility.Margin.Bottom.SMALL);
        textField.getElement().getThemeList().add("small");
    }

    private void showError(String message) {
        Notification.show(message, 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
    
}