package com.event.ems.repo;

import com.event.ems.model.StudentModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepo extends JpaRepository<StudentModel, Long> {
}