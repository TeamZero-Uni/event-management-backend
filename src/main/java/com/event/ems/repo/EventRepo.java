package com.event.ems.repo;

import com.event.ems.model.EventModel;
import com.event.ems.model.UserModel;
import com.event.ems.model.VenueModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface EventRepo extends JpaRepository<EventModel, Long> {

    // 1. Used in createEvent: Prevents duplicate event titles at the same venue on the same day
    boolean existsByTitleIgnoreCaseAndEventDateAndVenue_VenueId(
            String title,
            LocalDate eventDate,
            Long venueId
    );

    // 2. Used in createEvent: Checks for time overlaps for ANY event at this venue
    boolean existsByVenueAndEventDateAndStartTimeLessThanAndEndTimeGreaterThan(
            VenueModel venue,
            LocalDate eventDate,
            LocalTime endTime,
            LocalTime startTime
    );

    // 3. NEW! Used in updateEvent: Prevents duplicate event titles, but IGNORES the event we are currently updating
    boolean existsByTitleIgnoreCaseAndEventDateAndVenue_VenueIdAndIdNot(
            String title,
            LocalDate eventDate,
            Long venueId,
            Long eventId
    );

    // 4. NEW! Used in updateEvent: Checks for time overlaps, but IGNORES the event we are currently updating
    boolean existsByVenueAndEventDateAndStartTimeLessThanAndEndTimeGreaterThanAndIdNot(
            VenueModel venue,
            LocalDate eventDate,
            LocalTime endTime,
            LocalTime startTime,
            Long eventId
    );

    @Modifying
    @Query("DELETE FROM EventModel e WHERE e.createdBy.userId = :userId")
    void deleteAllByCreatedByUserId(@Param("userId") Long userId);


    List<EventModel> findByCreatedBy(UserModel user);

}