package com.event.ems.repo;

import com.event.ems.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserModel,String> {
    Optional<UserModel> findByUsername(String username);
    Optional<UserModel> findByUserId(Long id);

    @Query("SELECT u.username FROM UserModel u WHERE u.username LIKE ?1% ORDER BY u.username DESC LIMIT 1")
    String findLastUsernameByPrefix(String prefix);
}
