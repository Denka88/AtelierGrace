package com.denka88.ateliergrace.repo;


import com.denka88.ateliergrace.model.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepo extends JpaRepository<Auth, Long> {
    Optional<Auth> findByLogin(String login);

    boolean existsByLogin(String login);
}
