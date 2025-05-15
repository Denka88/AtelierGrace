package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.Material;
import com.denka88.ateliergrace.model.UserType;
import com.denka88.ateliergrace.service.CurrentUserService;
import com.denka88.ateliergrace.service.MaterialService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
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
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;

@PageTitle("Материалы")
@Route(value = "materials-list", layout = MainLayout.class)
@RolesAllowed({"ADMIN", "EMPLOYEE"})
public class MaterialsView extends VerticalLayout {

    private final MaterialService materialService;
    private final Grid<Material> grid;
    private final CurrentUserService currentUserService;

    private FormLayout editForm = new FormLayout();
    private TextField id = new TextField("ID");
    private TextField editName = new TextField("Название");
    private IntegerField editValue = new IntegerField("Кол-во");
    private Button editButton = new Button("Сохранить", VaadinIcon.CHECK.create());

    public MaterialsView(MaterialService materialService, CurrentUserService currentUserService) {
        this.materialService = materialService;
        this.currentUserService = currentUserService;
        this.grid = new Grid<>(Material.class, false);

        setPadding(true);
        setSpacing(false);
        setSizeFull();
        addClassName(LumoUtility.Padding.LARGE);

        setupGrid();
        updateGrid();

        Button addButton = new Button("Добавить материал", VaadinIcon.PLUS.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClassName(LumoUtility.Margin.Bottom.LARGE);

        Popover addMaterial = new Popover();
        addMaterial.setModal(true);
        addMaterial.setBackdropVisible(true);
        addMaterial.setWidth("300px");
        addMaterial.addClassName(LumoUtility.Padding.LARGE);

        TextField name = new TextField("Название");
        name.setWidthFull();

        IntegerField value = new IntegerField("На складе");
        value.setWidthFull();
        value.setMin(1);
        value.setMax(999999);
        value.setStepButtonsVisible(true);

        Button post = new Button("Добавить", VaadinIcon.CHECK.create());
        post.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        post.setWidthFull();

        post.addClickListener(e -> {
            if (name.isEmpty() || value.isEmpty()) {
                return;
            }
            Material material = new Material();
            material.setName(name.getValue());
            material.setValue(value.getValue());
            materialService.save(material);
            updateGrid();
            name.clear();
            value.clear();
        });

        FormLayout popoverForm = new FormLayout(name, value, post);
        popoverForm.setWidth("400px");
        addMaterial.add(popoverForm);
        addMaterial.setTarget(addButton);

        editForm.setWidth("400px");
        editForm.addClassNames(
                LumoUtility.Padding.LARGE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.SMALL,
                LumoUtility.Background.BASE
        );

        id.setVisible(false);
        
        editName.setMinLength(3);
        editName.setRequired(true);

        editValue.setStepButtonsVisible(true);
        editValue.setMin(1);
        editValue.setMax(999999);

        editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        editButton.addClassName(LumoUtility.Margin.Top.MEDIUM);
        editButton.getStyle().set("margin-left", "auto");

        editButton.addClickListener(e->{
            if(editName.isEmpty()){
                showError("Заполните все поля");
                return;
            }
            
            if(editName.getValue().length() < 3){
                showError("Неподходящая длина названия. Минимум 3 символа");
                return;
            }
            
            Material updateMaterial = materialService.findById(Long.valueOf(id.getValue())).orElse(null);
            if (updateMaterial != null) {
                updateMaterial.setName(editName.getValue());
                updateMaterial.setValue(editValue.getValue());
                materialService.update(updateMaterial);
                updateGrid();
                editForm.setVisible(false);
                Notification.show("Материал обновлен")
                        .setPosition(Notification.Position.TOP_END);
            }
        });

        grid.addCellFocusListener(e->{
            id.setValue(String.valueOf(e.getItem().map(Material::getId).orElse(null)));
            editName.setValue(e.getItem().map(Material::getName).orElse(null));
            editValue.setValue(e.getItem().map(Material::getValue).orElse(null));
        });

        styleTextField(editName);
        styleIntegerField(editValue);

        Button close = new Button(new Icon("lumo", "cross"), (e) -> editForm.setVisible(false));

        HorizontalLayout formHeader = new HorizontalLayout();
        formHeader.getStyle().set("justify-content", "space-between");

        H3 title = new H3("Изменить материал");

        formHeader.add(title, close);

        editForm.add(formHeader, id, editName, editValue, editButton);
        editForm.setVisible(false);

        HorizontalLayout buttonLayout = new HorizontalLayout(addButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        Div content = new Div(grid, editForm);
        content.addClassName(LumoUtility.Display.FLEX);
        content.addClassName(LumoUtility.FlexDirection.COLUMN);
        content.addClassName(LumoUtility.Gap.LARGE);
        content.setSizeFull();

        add(buttonLayout, content, addMaterial);
    }

    private void setupGrid(){
        grid.addClassName("force-focus-outline");
        grid.addThemeVariants(
                com.vaadin.flow.component.grid.GridVariant.LUMO_WRAP_CELL_CONTENT,
                com.vaadin.flow.component.grid.GridVariant.LUMO_COLUMN_BORDERS,
                com.vaadin.flow.component.grid.GridVariant.LUMO_ROW_STRIPES
        );
        grid.setHeightFull();

        grid.addColumn(Material::getId)
                .setHeader("ID")
                .setSortable(true)
                .setAutoWidth(true)
                .setFlexGrow(0);

        grid.addColumn(Material::getName)
                .setHeader("Название")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(Material::getValue)
                .setHeader("На складе")
                .setSortable(true)
                .setAutoWidth(true);

        GridContextMenu<Material> contextMenu = grid.addContextMenu();

        Button editMenuItem = new Button("Изменить", VaadinIcon.EDIT.create());
        editMenuItem.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        contextMenu.addItem(editMenuItem, e->{
            if(editForm.isVisible()){
                editForm.setVisible(false);
            }else if(!editForm.isVisible()){
                editForm.setVisible(true);
                id.setValue(String.valueOf(e.getItem().map(Material::getId).orElse(null)));
                editName.setValue(e.getItem().map(Material::getName).orElse(null));
                editValue.setValue(e.getItem().map(Material::getValue).orElse(null));
            }
        });

        
        
        if(currentUserService.getCurrentUserType().equals(UserType.ADMIN)) {
            Button deleteMenuItem = new Button("Удалить", VaadinIcon.TRASH.create());
            deleteMenuItem.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            contextMenu.addItem(deleteMenuItem, e -> {
                Dialog delete = new Dialog();
                
                Button confirmDelete = new Button("Удалить");
                confirmDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);
                Button cancelDelete = new Button("Отмена");
                cancelDelete.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

                HorizontalLayout buttonLayout = new HorizontalLayout(cancelDelete, confirmDelete);
                buttonLayout.setWidthFull();
                buttonLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
                delete.add(buttonLayout);
                delete.setHeaderTitle(String.format("Удалить материал \"%s\"", e.getItem().map(Material::getName).orElse(null)));
                delete.open();
                confirmDelete.addClickListener(event -> {
                    materialService.delete(e.getItem().map(Material::getId).orElse(null));
                    updateGrid();
                    Notification.show("Материал удален")
                            .setPosition(Notification.Position.TOP_END);
                    delete.close();
                });
                
                cancelDelete.addClickListener(event -> {
                    delete.close();
                });
                updateGrid();
            });
        }
    }

    private void updateGrid(){
        List<Material> materials = materialService.findAll();
        grid.setItems(materials);
    }

    private void styleTextField(TextField textField) {
        textField.setWidthFull();
        textField.addClassName(LumoUtility.Margin.Bottom.SMALL);
        textField.getElement().getThemeList().add("small");
    }

    private void styleIntegerField(IntegerField integerField) {
        integerField.setWidthFull();
        integerField.addClassName(LumoUtility.Margin.Bottom.SMALL);
        integerField.getElement().getThemeList().add("small");
    }

    private void showError(String message) {
        Notification.show(message, 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}