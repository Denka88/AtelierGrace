package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.Auth;
import com.denka88.ateliergrace.repo.AuthRepo;
import com.denka88.ateliergrace.service.AuthService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AuthRepo authRepo;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserDetailsServiceImpl(AuthRepo authRepo, BCryptPasswordEncoder passwordEncoder) {
        this.authRepo = authRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Auth auth = authRepo.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));

        return User.builder()
                .username(auth.getLogin())
                .password(auth.getPasswordHash())
                .authorities("ROLE_" + auth.getUserType())
                .build();
    }
}
