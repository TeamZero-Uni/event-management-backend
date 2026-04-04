package com.event.ems.repo;

import com.event.ems.model.EventModel;
import com.event.ems.model.VenueModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;

public interface EventRepo extends JpaRepository<EventModel,Long> {
    boolean existsByTitleIgnoreCaseAndEventDateAndVenue_VenueId(
            String title,
            LocalDate eventDate,
            Long venueId
    );

    boolean existsByVenueAndEventDateAndStartTimeLessThanAndEndTimeGreaterThan(
            VenueModel venue,
            LocalDate eventDate,
            LocalTime endTime,
            LocalTime startTime
    );
}
