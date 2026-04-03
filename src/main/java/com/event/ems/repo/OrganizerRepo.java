package com.event.ems.repo;

import com.event.ems.model.OrganizersModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizerRepo extends JpaRepository<OrganizersModel,Long> {
}
