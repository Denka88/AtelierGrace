package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

@Route(value = "about", layout = MainLayout.class)
@PageTitle("О программе")
@PermitAll
public class AboutView extends VerticalLayout {

    public AboutView() {
        setPadding(true);
        setSpacing(false);
        setSizeFull();
        addClassName(LumoUtility.Padding.LARGE);

        H1 title = new H1("Программа \"Ателье Грация\"");
        title.addClassName(LumoUtility.Margin.Bottom.LARGE);
        title.addClassName(LumoUtility.TextAlignment.CENTER);

        Paragraph description = new Paragraph(
                "Программа \"Ателье Грация\" представляет собой веб-приложение для управления ателье по пошиву одежды. " +
                        "Это система учета заказов, клиентов, материалов и сотрудников с разграничением прав доступа."
        );
        description.addClassName(LumoUtility.Margin.Bottom.LARGE);

        Details functionalitySection = createSection(
                "Основной функционал",
                VaadinIcon.COG,
                createFunctionalContent()
        );

        Details technicalSection = createSection(
                "Технические особенности",
                VaadinIcon.WRENCH,
                createTechnicalContent()
        );

        Details modulesSection = createSection(
                "Основные модули",
                VaadinIcon.CUBES,
                createModulesContent()
        );

        add(
                title,
                description,
                functionalitySection,
                technicalSection,
                modulesSection
        );
    }

    private Component createFunctionalContent() {
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setPadding(false);

        Div adminDiv = new Div(new Text("Для администратора:"));
        UnorderedList adminList = new UnorderedList(
                new ListItem("Управление сотрудниками (добавление, редактирование, удаление)"),
                new ListItem("Управление клиентами"),
                new ListItem("Управление заказами"),
                new ListItem("Управление материалами и поставщиками"),
                new ListItem("Просмотр и управление поставками материалов")
        );
        adminDiv.add(adminList);

        Div employeeDiv = new Div(new Text("Для сотрудников:"));
        UnorderedList employeeList = new UnorderedList(
                new ListItem("Создание и редактирование заказов"),
                new ListItem("Просмотр списка клиентов"),
                new ListItem("Управление материалами и поставщиками"),
                new ListItem("Отслеживание поставок"),
                new ListItem("Завершение заказов")
        );
        employeeDiv.add(employeeList);

        Div clientDiv = new Div(new Text("Для клиентов:"));
        UnorderedList clientList = new UnorderedList(
                new ListItem("Просмотр своих заказов"),
                new ListItem("Редактирование профиля")
        );
        clientDiv.add(clientList);

        content.add(adminDiv, employeeDiv, clientDiv);
        return content;
    }

    private Component createTechnicalContent() {
        UnorderedList list = new UnorderedList(
                new ListItem("Реализована система аутентификации и авторизации"),
                new ListItem("Разграничение прав доступа по ролям (ADMIN, EMPLOYEE, CLIENT)"),
                new ListItem("Валидация вводимых данных"),
                new ListItem("Удобный интерфейс с фильтрацией и сортировкой"),
                new ListItem("Уведомления об успешных операциях и ошибках")
        );
        return list;
    }

    private Component createModulesContent() {
        OrderedList list = new OrderedList(
                new ListItem("Управление заказами - создание, редактирование, просмотр и фильтрация заказов"),
                new ListItem("Управление клиентами - регистрация, просмотр и редактирование данных клиентов"),
                new ListItem("Управление сотрудниками - регистрация и управление учетными записями сотрудников"),
                new ListItem("Управление материалами - учет материалов на складе"),
                new ListItem("Управление поставщиками - ведение базы поставщиков материалов"),
                new ListItem("Учет поставок - фиксация поступлений материалов от поставщиков"),
                new ListItem("Личный кабинет клиента - просмотр своих заказов и профиля")
        );
        return list;
    }

    private Details createSection(String summary, VaadinIcon icon, Component content) {
        Details section = new Details();
        section.setSummaryText(summary);
        section.setOpened(true);

        Span summarySpan = new Span(icon.create(), new Text(" " + summary));
        section.setSummary(summarySpan);

        section.add(content);
        section.addClassName(LumoUtility.Margin.Bottom.MEDIUM);
        return section;
    }


}
