package com.denka88.ateliergrace.impl;

import com.denka88.ateliergrace.model.Auth;
import com.denka88.ateliergrace.model.UserType;
import com.denka88.ateliergrace.repo.AuthRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthRepo authRepo;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register() {
        String login = "testUser";
        String password = "password";
        Long userId = 1L;
        UserType userType = UserType.CLIENT;

        when(authRepo.existsByLogin(login)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("hashedPassword");

        authService.register(login, password, userType, userId);

        ArgumentCaptor<Auth> captor = ArgumentCaptor.forClass(Auth.class);
        verify(authRepo).save(captor.capture());

        Auth savedAuth = captor.getValue();
        assertEquals(login, savedAuth.getLogin());
        assertEquals("hashedPassword", savedAuth.getPasswordHash());
        assertEquals(userType, savedAuth.getUserType());
        assertEquals(userId, savedAuth.getUserId());
    }

    @Test
    void delete() {
        Long userId = 123L;
        authService.delete(userId);
        verify(authRepo, times(1)).deleteByUserId(userId);
    }

    @Test
    void findByLogin() {
        String login = "user123";
        Auth mockAuth = new Auth();
        mockAuth.setLogin(login);
        when(authRepo.findByLogin(login)).thenReturn(Optional.of(mockAuth));

        Optional<Auth> result = authService.findByLogin(login);
        assertTrue(result.isPresent());
        assertEquals(login, result.get().getLogin());
    }
}