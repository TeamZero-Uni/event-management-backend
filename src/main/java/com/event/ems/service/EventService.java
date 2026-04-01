package com.event.ems.service;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.EventRequest;
import com.event.ems.exception.UserNotFoundException;
import com.event.ems.exception.VenueNotFoundException;
import com.event.ems.model.*;
import com.event.ems.repo.EventRepo;
import com.event.ems.repo.UserRepo;
import com.event.ems.repo.VenueRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepo eventRepo;
    private final VenueRepo venueRepo;
    private final UserRepo userRepo;

    public ApiResponse<List<EventModel>> getAllEvents(){
        List<EventModel> events = eventRepo.findAll();
        return new ApiResponse<>(
                true,
                "Events Fatched succesfully",
                events,
                LocalDateTime.now()
        );
    }

    public ApiResponse<EventModel> createEvent(EventRequest request, String username) {
        UserModel user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        String venueName = request.getVenueName() == null ? "" : request.getVenueName().trim();
        VenueModel venue = venueRepo.findByPlaceNameIgnoreCase(venueName)
                .orElseThrow(() -> new VenueNotFoundException("Venue not found: " + venueName));

        if (request.getMaxParticipants() == null || request.getMaxParticipants() <= 0) {
            throw new RuntimeException("Max participants must be greater than zero");
        }

        if (request.getMaxParticipants() > venue.getCapacity()) {
            throw new RuntimeException("Max participants exceeds venue capacity of " + venue.getCapacity());
        }

        if (request.getStartTime() != null && request.getEndTime() != null
                && !request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        boolean hasTimeConflict = eventRepo.existsByVenueAndEventDateAndStartTimeLessThanAndEndTimeGreaterThan(
                venue,
                request.getEventDate(),
                request.getEndTime(),
                request.getStartTime()
        );

        if (hasTimeConflict) {
            throw new RuntimeException("Another event is already scheduled at this venue during this time");
        }

        String eventTitle = request.getEventTitle() == null ? "" : request.getEventTitle().trim();

        boolean alreadyExists = eventRepo.existsByTitleIgnoreCaseAndEventDateAndVenue_VenueId(
                eventTitle,
                request.getEventDate(),
                venue.getVenueId()
        );

        if (alreadyExists) {
            throw new IllegalArgumentException("This event already exists for the selected venue and date");
        }

        EventType eventType;
        try {
            eventType = EventType.valueOf(request.getEventType().trim().toUpperCase());
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid event type: " + request.getEventType());
        }

        EventStatus eventStatus = EventStatus.ACCEPTED;
        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            try {
                eventStatus = EventStatus.valueOf(request.getStatus().trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid event status: " + request.getStatus());
            }
        }

        EventModel event = new EventModel();
        event.setTitle(eventTitle);
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setMaxParticipants(request.getMaxParticipants());
        event.setPosterUrl(request.getPosterUrl());
        event.setType(eventType);
        event.setStatus(eventStatus);
        event.setVenue(venue);
        event.setCreatedBy(user);

        EventModel savedEvent = eventRepo.save(event);
        return new ApiResponse<>(true, "Event created successfully", savedEvent, LocalDateTime.now());
    }
}
