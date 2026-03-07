package com.event.ems.repo;

import com.event.ems.model.UserModel;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserModel,String> {
    Optional<UserModel> findByUsername(String username);
    Optional<UserModel> findByUserId(Long id);
}
