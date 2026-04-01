package com.event.ems.repo;

import com.event.ems.model.VenueModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VenueRepo extends JpaRepository<VenueModel,Long> {
    Optional<VenueModel> findByPlaceNameIgnoreCase(String placeName);
    Optional<VenueModel> findByPlaceName(String placeName);



}

