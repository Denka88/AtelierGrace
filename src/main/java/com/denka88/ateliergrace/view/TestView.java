package com.denka88.ateliergrace.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route("test")
@PermitAll
public class TestView extends VerticalLayout {
    
    public TestView() {
        
        H1 title = new H1("Test");
        
        add(title);
        
    }
    
}
