package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.Auth;
import com.denka88.ateliergrace.model.Client;
import com.denka88.ateliergrace.model.Employee;
import com.denka88.ateliergrace.model.UserType;
import com.denka88.ateliergrace.repo.AuthRepo;
import com.denka88.ateliergrace.service.ClientService;
import com.denka88.ateliergrace.service.CurrentUserService;
import com.denka88.ateliergrace.service.EmployeeService;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CurrentUserServiceImpl implements CurrentUserService {

    private final AuthRepo authRepo;
    private final ClientService clientService;
    private final AuthenticationContext authenticationContext;
    private final EmployeeService employeeService;

    public CurrentUserServiceImpl(AuthRepo authRepo, ClientService clientService, AuthenticationContext authenticationContext, EmployeeService employeeService) {
        this.authRepo = authRepo;
        this.clientService = clientService;
        this.authenticationContext = authenticationContext;
        this.employeeService = employeeService;
    }

    @Override
    public Optional<Auth> getCurrentAuth() {
        return authenticationContext.getAuthenticatedUser(UserDetails.class)
                .flatMap(user -> authRepo.findByLogin(user.getUsername()));
    }
    
    @Override
    public Optional<Employee> getCurrentEmployee() {
        return getCurrentAuth()
                .filter(auth -> auth.getUserType() == UserType.EMPLOYEE)
                .flatMap(auth -> employeeService.findById(auth.getUserId()));
    }

    @Override
    public Optional<Client> getCurrentClient() {
        return getCurrentAuth()
                .filter(auth -> auth.getUserType() == UserType.CLIENT)
                .flatMap(auth -> clientService.findById(auth.getUserId()));
    }

    @Override
    public Long getCurrentUserId() {
        return getCurrentAuth()
                .map(Auth::getUserId)
                .orElse(null);
    }

    @Override
    public UserType getCurrentUserType() {
        return getCurrentAuth()
                .map(Auth::getUserType)
                .orElse(null);
    }
}
