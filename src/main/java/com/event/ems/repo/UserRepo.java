package com.event.ems.repo;

import com.event.ems.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserModel,Long> {
    Optional<UserModel> findByUsername(String username);
    Optional<UserModel> findByEmail(String email);
    Optional<UserModel> findByUserId(Long id);
    Optional<UserModel> findTopByUsernameStartingWithOrderByUsernameDesc(String prefix);
}
