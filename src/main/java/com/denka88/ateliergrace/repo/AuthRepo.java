package com.denka88.ateliergrace.repo;


import com.denka88.ateliergrace.model.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthRepo extends JpaRepository<Auth, Long> {
    Optional<Auth> findByLogin(String login);

    boolean existsByLogin(String login);

    @Modifying
    @Query("DELETE FROM Auth a WHERE a.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
