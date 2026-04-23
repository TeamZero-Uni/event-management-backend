package com.event.ems.service;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.EventRequest;
import com.event.ems.exception.EventNotFoundException;
import com.event.ems.exception.UserNotFoundException;
import com.event.ems.exception.VenueNotFoundException;
import com.event.ems.model.*;
import com.event.ems.repo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepo eventRepo;
    private final RegistrationRepo registrationRepo;
    private final VenueRepo venueRepo;
    private final UserRepo userRepo;
    private final NotificationRepo notificationRepo;

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

        if (request.getBudget() == null) {
            throw new IllegalArgumentException("Budget is required");
        }

        if (request.getBudget().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Budget cannot be negative");
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
            eventStatus = parseEventStatus(request.getStatus());
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
        event.setBudget(request.getBudget());
        event.setDocPath(
                (request.getDocPath() == null || request.getDocPath().isEmpty())
                        ? null
                        : request.getDocPath()
        );

        EventModel savedEvent = eventRepo.save(event);
        return new ApiResponse<>(true, "Event created successfully", savedEvent, LocalDateTime.now());
    }

    public ApiResponse<Long> deleteEventById(Long id) {
        EventModel event = eventRepo.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found: " + id));

        registrationRepo.deleteByEvent_Id(id);

        List<NotificationModel> relatedNotifications = notificationRepo.findByEvent_Id(id);
        for (NotificationModel notification : relatedNotifications) {
            notification.setEventReferenceId(id);
            notification.setEvent(null);
        }
        notificationRepo.saveAll(relatedNotifications);

        NotificationModel deleteNotification = new NotificationModel();
        deleteNotification.setUser(event.getCreatedBy());
        deleteNotification.setEventReferenceId(id);
        deleteNotification.setMessage("The organizer has decided to cancel the event '" + event.getTitle() + "'. We apologize for any inconvenience this may cause.");
        deleteNotification.setIsRead(false);
        notificationRepo.save(deleteNotification);

        eventRepo.delete(event);
        return new ApiResponse<>(true, "Event deleted successfully", id, LocalDateTime.now());
    }

    public ApiResponse<EventModel> getEventByIdResponse(Long id) {
        EventModel event = eventRepo.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found: " + id));
        return new ApiResponse<>(true, "Event fetched successfully", event, LocalDateTime.now());
    }

    public ApiResponse<EventModel> updateEvent(Long eventId, EventRequest eventRequest) {
        // 1. Fetch the existing event
        EventModel event = eventRepo.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with ID: " + eventId));

        // Keep old values so we can generate a clear change summary for notifications.
        String oldTitle = event.getTitle();
        String oldDescription = event.getDescription();
        var oldDate = event.getEventDate();
        var oldStartTime = event.getStartTime();
        var oldEndTime = event.getEndTime();
        Integer oldMaxParticipants = event.getMaxParticipants();
        String oldPosterUrl = event.getPosterUrl();
        BigDecimal oldBudget = event.getBudget();
        EventStatus oldStatus = event.getStatus();
        String oldVenueName = event.getVenue() != null ? event.getVenue().getPlaceName() : null;

        // 2. Handle Status Update (Check for status change logic here later)
        if (eventRequest.getStatus() != null && !eventRequest.getStatus().trim().isEmpty()) {
            EventStatus newStatus = parseEventStatus(eventRequest.getStatus());
            event.setStatus(newStatus);
        }

        // 3. Update standard fields safely
        if (eventRequest.getEventTitle() != null && !eventRequest.getEventTitle().trim().isEmpty()) {
            event.setTitle(eventRequest.getEventTitle().trim());
        }
        if (eventRequest.getDescription() != null) event.setDescription(eventRequest.getDescription());
        if (eventRequest.getEventDate() != null) event.setEventDate(eventRequest.getEventDate());
        if (eventRequest.getStartTime() != null) event.setStartTime(eventRequest.getStartTime());
        if (eventRequest.getEndTime() != null) event.setEndTime(eventRequest.getEndTime());
        if (eventRequest.getMaxParticipants() != null) event.setMaxParticipants(eventRequest.getMaxParticipants());
        if (eventRequest.getPosterUrl() != null) event.setPosterUrl(eventRequest.getPosterUrl());

        if (eventRequest.getBudget() != null) {
            if (eventRequest.getBudget().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Budget cannot be negative");
            }
            event.setBudget(eventRequest.getBudget());
        }

        // 4. Update Venue
        VenueModel targetVenue = event.getVenue();
        if (eventRequest.getVenueName() != null && !eventRequest.getVenueName().trim().isEmpty()) {
            targetVenue = venueRepo.findByPlaceNameIgnoreCase(eventRequest.getVenueName().trim())
                    .orElseThrow(() -> new VenueNotFoundException("Venue not found: " + eventRequest.getVenueName()));
            event.setVenue(targetVenue);
        }

        // 5. Validation Logic
        if (event.getMaxParticipants() > targetVenue.getCapacity()) {
            throw new RuntimeException("Max participants exceeds venue capacity of " + targetVenue.getCapacity());
        }

        // 6. Conflict Checks (Title and Time - Ignoring THIS event ID)
        boolean duplicateTitle = eventRepo.existsByTitleIgnoreCaseAndEventDateAndVenue_VenueIdAndIdNot(
                event.getTitle(), event.getEventDate(), targetVenue.getVenueId(), eventId
        );
        if (duplicateTitle) {
            throw new IllegalArgumentException("Another event with this title already exists here on this date");
        }

        boolean hasTimeConflict = eventRepo.existsByVenueAndEventDateAndStartTimeLessThanAndEndTimeGreaterThanAndIdNot(
                targetVenue, event.getEventDate(), event.getEndTime(), event.getStartTime(), eventId
        );
        if (hasTimeConflict) {
            throw new RuntimeException("Another event is already scheduled at this venue during this time");
        }

        // 7. Save the event
        EventModel savedEvent = eventRepo.save(event);

        String changeSummary = buildChangeSummary(
                oldTitle,
                oldDescription,
                oldDate,
                oldStartTime,
                oldEndTime,
                oldMaxParticipants,
                oldPosterUrl,
                oldBudget,
                oldStatus,
                oldVenueName,
                savedEvent
        );

        if (savedEvent.getStatus() != EventStatus.PENDING && !changeSummary.isBlank()) {
            NotificationModel notification = new NotificationModel();
            notification.setUser(savedEvent.getCreatedBy());
            notification.setEvent(savedEvent);
            notification.setEventReferenceId(savedEvent.getId());
            notification.setMessage("Event '" + savedEvent.getTitle() + "' updated: " + changeSummary);
            notification.setIsRead(false);
            notificationRepo.save(notification);
        }

        /* * TODO: NOTIFICATION LOGIC
         * This section is for notifying registered students when:
         * 1. The event status changes from PENDING to ACCEPTED.
         * 2. The event is already ACCEPTED but critical fields (Date, Venue, Time) were changed.
         *
         * Example:
         * if (statusChangedToAccepted) {
         * sendNotifications(savedEvent, "Your event is now approved!");
         * }
         */

        return new ApiResponse<>(true, "Event updated successfully", savedEvent, LocalDateTime.now());
    }

    private EventStatus parseEventStatus(String statusValue) {
        String normalized = statusValue == null ? "" : statusValue.trim().toUpperCase();
        if ("APPROVED".equals(normalized)) {
            normalized = "ACCEPTED";
        }
        try {
            return EventStatus.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid event status: " + statusValue);
        }
    }

    private String buildChangeSummary(
            String oldTitle,
            String oldDescription,
            Object oldDate,
            Object oldStartTime,
            Object oldEndTime,
            Integer oldMaxParticipants,
            String oldPosterUrl,
            BigDecimal oldBudget,
            EventStatus oldStatus,
            String oldVenueName,
            EventModel updatedEvent
    ) {
        List<String> changes = new ArrayList<>();

        addChange(changes, "title", oldTitle, updatedEvent.getTitle());
        addChange(changes, "description", oldDescription, updatedEvent.getDescription());
        addChange(changes, "date", oldDate, updatedEvent.getEventDate());
        addChange(changes, "start time", oldStartTime, updatedEvent.getStartTime());
        addChange(changes, "end time", oldEndTime, updatedEvent.getEndTime());
        addChange(changes, "max participants", oldMaxParticipants, updatedEvent.getMaxParticipants());
        addChange(changes, "poster", oldPosterUrl, updatedEvent.getPosterUrl());
        addChange(changes, "budget", oldBudget, updatedEvent.getBudget());
        addChange(changes, "status", oldStatus, updatedEvent.getStatus());

        String newVenueName = updatedEvent.getVenue() != null ? updatedEvent.getVenue().getPlaceName() : null;
        addChange(changes, "venue", oldVenueName, newVenueName);

        return String.join(", ", changes);
    }

    private void addChange(List<String> changes, String field, Object oldValue, Object newValue) {
        if (Objects.equals(oldValue, newValue)) {
            return;
        }
        changes.add(field + ": " + formatValue(oldValue) + " -> " + formatValue(newValue));
    }

    private String formatValue(Object value) {
        if (value == null) {
            return "empty";
        }

        if (value instanceof String textValue) {
            String trimmed = textValue.trim();
            return trimmed.isEmpty() ? "empty" : "'" + trimmed + "'";
        }

        return String.valueOf(value);
    }
}
