package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.Auth;
import com.denka88.ateliergrace.model.UserType;
import com.denka88.ateliergrace.repo.AuthRepo;
import com.denka88.ateliergrace.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthRepo authRepo;
    private final BCryptPasswordEncoder passwordEncoder;
//    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(AuthRepo authRepo, BCryptPasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.authRepo = authRepo;
        this.passwordEncoder = passwordEncoder;
//        this.authenticationManager = authenticationManager;
    }

    @Override
    public void register(String login, String password, UserType userType, Long userId) {
        if (authRepo.existsByLogin(login)) {
            throw new IllegalArgumentException("Login already exists");
        }

        Auth auth = new Auth();
        auth.setLogin(login);
        auth.setPasswordHash(passwordEncoder.encode(password)); // Кодируем пароль
        auth.setUserType(userType);
        auth.setUserId(userId);

        authRepo.save(auth);
    }

    @Override
    public void delete(Long userId) {
        authRepo.deleteById(userId);
    }

    /*@Override
    public void authenticate(String login, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }*/

    @Override
    public Optional<Auth> findByLogin(String login) {
        return authRepo.findByLogin(login);
    }
}
