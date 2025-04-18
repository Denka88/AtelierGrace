package com.denka88.ateliergrace.service;

import com.denka88.ateliergrace.model.Auth;
import com.denka88.ateliergrace.model.UserType;

import java.util.Optional;

public interface AuthService {
    
    void register(String login, String password, UserType userType, Long userId);
    
    void delete(Long userId);
    
//    void authenticate(String login, String password);
    
    Optional<Auth> findByLogin(String login);
    
}
