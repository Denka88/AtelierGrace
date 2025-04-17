package com.denka88.ateliergrace.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;

@Route("")
@RouteAlias("main")
@PageTitle("MAIN")
public class MainView extends VerticalLayout {
    
    public MainView() {
        
        H1 title = new H1("MAIN");
        
        add(title);
        
    }
    
    
    
}
