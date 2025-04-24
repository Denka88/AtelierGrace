package com.denka88.ateliergrace.service;

import com.denka88.ateliergrace.model.Auth;
import com.denka88.ateliergrace.model.Client;
import com.denka88.ateliergrace.model.UserType;

import java.util.Optional;

public interface CurrentUserService {
    
    Optional<Auth> getCurrentAuth();
    
    Optional<Client> getCurrentClient();
    
    Long getCurrentUserId();
    
    UserType getCurrentUserType();
    
}
