package com.event.ems.repo;

import com.event.ems.model.RegistrationModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepo extends JpaRepository <RegistrationModel,Long> {
}
