package com.event.ems.repo;

import com.event.ems.model.StudentModel;
import com.event.ems.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepo extends JpaRepository<StudentModel, Long> {
    Optional<StudentModel> findByUser(UserModel user);
}
