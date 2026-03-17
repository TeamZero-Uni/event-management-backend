package com.event.ems.repo;

import com.event.ems.model.VenueModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepo extends JpaRepository<VenueModel,Long> {
}
