package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.denka88.ateliergrace.model.*;
import com.denka88.ateliergrace.service.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.popover.Popover;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;

import java.util.List;
import java.util.Objects;

@Route(value = "supplies", layout = MainLayout.class)
@PageTitle("Поставки")
@RolesAllowed({"ADMIN", "EMPLOYEE"})
public class OrganizationMaterialView extends VerticalLayout {

    private final OrganizationMaterialService organizationMaterialService;
    private final OrganizationService organizationService;
    private final MaterialService materialService;
    private final Grid<OrganizationMaterial> grid;

    public OrganizationMaterialView(OrganizationMaterialService organizationMaterialService,
                                    OrganizationService organizationService,
                                    MaterialService materialService) {
        this.organizationMaterialService = organizationMaterialService;
        this.organizationService = organizationService;
        this.materialService = materialService;
        this.grid = new Grid<>(OrganizationMaterial.class, false);

        // Общие стили для страницы
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

        ComboBox<Organization> organizationFilter = new ComboBox<>("Поставщик");
        organizationFilter.setItems(organizationService.findAll());
        organizationFilter.setItemLabelGenerator(organization -> String.format("%s", organization.getName()));
        organizationFilter.setClearButtonVisible(true);
        
        ComboBox<Material> materialFilter = new ComboBox<>("Material");
        materialFilter.setItems(materialService.findAll());
        materialFilter.setItemLabelGenerator(organization -> String.format("%s", organization.getName()));
        materialFilter.setClearButtonVisible(true);
        
        Button applyFilters = new Button("Применить", VaadinIcon.CHECK.create());
        applyFilters.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button resetFilters = new Button("Сбросить", VaadinIcon.TRASH.create());
        resetFilters.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);

        HorizontalLayout buttonsLayout = new HorizontalLayout(applyFilters, resetFilters);
        buttonsLayout.setWidthFull();
        buttonsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        
        filterForm.add(organizationFilter, materialFilter, buttonsLayout);
        filterPopover.add(filterForm);
        filterPopover.setTarget(filterButton);
        
        applyFilters.addClickListener(e->{
            List<OrganizationMaterial> allSupplies = organizationMaterialService.findAll();
            ListDataProvider<OrganizationMaterial> dataProvider = new ListDataProvider<>(allSupplies);
            
            dataProvider.addFilter(supplied -> {
                boolean organizationMatch = organizationFilter.getValue() == null;
                if (!organizationMatch) {
                    organizationMatch = Objects.equals(
                            supplied.getOrganization().getId(),
                            organizationFilter.getValue().getId()
                    );
                }

                boolean materialMatch = materialFilter.getValue() == null;
                if(!materialMatch) {
                    materialMatch = Objects.equals(
                            supplied.getMaterial().getId(),
                            materialFilter.getValue().getId()
                    );
                }
                
                return organizationMatch && materialMatch;
            });
            
            grid.setDataProvider(dataProvider);
            filterPopover.close();
        });
        
        resetFilters.addClickListener(e->{
            organizationFilter.clear();
            materialFilter.clear();
            grid.setItems(organizationMaterialService.findAll());
            filterPopover.close();
        });

        HorizontalLayout toolbar = new HorizontalLayout(filterButton);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        setupGrid();
        updateGrid();

        Button addButton = new Button("Добавить поставку", VaadinIcon.PLUS.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClassName(LumoUtility.Margin.Bottom.LARGE);

        // Стилизация popover для добавления
        Popover addSupplied = new Popover();
        addSupplied.setModal(true);
        addSupplied.setBackdropVisible(true);
        addSupplied.setWidth("300px");
        addSupplied.addClassName(LumoUtility.Padding.LARGE);

        // Форма добавления поставки
        FormLayout formLayout = new FormLayout();
        formLayout.setWidth("400px");

        Select<Material> materialSelect = new Select<>();
        materialSelect.setLabel("Материал");
        materialSelect.setItems(materialService.findAll());
        materialSelect.setItemLabelGenerator(Material::getName);
        materialSelect.setWidthFull();

        Select<Organization> organizationSelect = new Select<>();
        organizationSelect.setLabel("Поставщик");
        organizationSelect.setItems(organizationService.findAll());
        organizationSelect.setItemLabelGenerator(Organization::getName);
        organizationSelect.setWidthFull();

        NumberField cost = new NumberField("Цена");
        cost.setWidthFull();
        cost.setMin(0);
        cost.setRequired(true);

        IntegerField value = new IntegerField("Количество");
        value.setWidthFull();
        value.setMin(1);
        value.setStepButtonsVisible(true);
        value.setRequired(true);

        Button post = new Button("Добавить", VaadinIcon.CHECK.create());
        post.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        post.setWidth("400px");

        post.addClickListener(e -> {
            if (materialSelect.isEmpty() || organizationSelect.isEmpty() ||
                    cost.isEmpty() || value.isEmpty()) {
                Notification.show("Заполните все поля")
                        .setPosition(Notification.Position.TOP_END);
                return;
            }

            try {
                Material selectedMaterial = materialSelect.getValue();
                Organization selectedOrganization = organizationSelect.getValue();

                OrganizationMaterialKey key = new OrganizationMaterialKey(
                        selectedMaterial.getId(),
                        selectedOrganization.getId()
                );

                OrganizationMaterial supplied = new OrganizationMaterial();
                supplied.setId(key);
                supplied.setMaterial(selectedMaterial);
                supplied.setOrganization(selectedOrganization);
                supplied.setValue(value.getValue());
                supplied.setCost(cost.getValue().floatValue());

                organizationMaterialService.save(supplied);
                updateGrid();

                materialSelect.clear();
                organizationSelect.clear();
                cost.clear();
                value.clear();

                Notification.show("Поставка добавлена")
                        .setPosition(Notification.Position.TOP_END);
            } catch (Exception ex) {
                Notification.show("Ошибка при добавлении поставки: " + ex.getMessage())
                        .setPosition(Notification.Position.TOP_END);
            }
        });

        formLayout.add(materialSelect, organizationSelect, cost, value, post);
        addSupplied.add(formLayout);
        addSupplied.setTarget(addButton);

        // Контейнер для кнопки добавления
        HorizontalLayout buttonLayout = new HorizontalLayout(addButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        // Контейнер для сетки
        Div content = new Div(grid);
        content.setSizeFull();

        add(toolbar, buttonLayout, content, addSupplied);
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
        grid.addColumn(om -> om.getOrganization().getName())
                .setHeader("Поставщик")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(om -> om.getMaterial().getName())
                .setHeader("Материал")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(OrganizationMaterial::getValue)
                .setHeader("Количество")
                .setSortable(true)
                .setAutoWidth(true);

        grid.addColumn(om -> String.format("%.2f", om.getCost()))
                .setHeader("Цена")
                .setSortable(true)
                .setAutoWidth(true);
    }

    private List<OrganizationMaterial> updateGrid(){
        List<OrganizationMaterial> organizationMaterials = organizationMaterialService.findAll();
        grid.setItems(organizationMaterials);
        return organizationMaterials;
    }
}