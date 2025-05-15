package com.denka88.ateliergrace.view;

import com.denka88.ateliergrace.MainLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "about", layout = MainLayout.class)
@PageTitle("О программе")
public class AboutView extends VerticalLayout {

    public AboutView(){

        H1 title = new H1("Описание программы \"Ателье Грация\"");

    }

}
