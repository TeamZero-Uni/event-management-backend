package com.event.ems.controller;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.EventRequest;
import com.event.ems.model.EventModel;
import com.event.ems.model.UserModel;
import com.event.ems.repo.EventRepo;
import com.event.ems.repo.UserRepo;
import com.event.ems.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // Injected repositories to fetch user and event data directly
    private final UserRepo userRepo;
    private final EventRepo eventRepo;

    @GetMapping("/all")
    public ApiResponse<List<EventModel>> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public ApiResponse<EventModel> getEventById(@PathVariable Long id) {
        return eventService.getEventByIdResponse(id);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EventModel>> createEvent(
            @RequestBody EventRequest request,
            Principal principal) {

        if (principal == null) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Unauthorized: No token provided", null, null),
                    HttpStatus.UNAUTHORIZED
            );
        }

        String loggedInUsername = principal.getName();

        ApiResponse<EventModel> response = eventService.createEvent(request, loggedInUsername);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("{id}")
    public ApiResponse<Long> deleteEventById(@PathVariable Long id) {
        return eventService.deleteEventById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EventModel>> updateEvent(
            @PathVariable Long id,
            @RequestBody EventRequest eventRequest) {
        ApiResponse<EventModel> response = eventService.updateEvent(id, eventRequest);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<EventModel>> updateEventStatus(
            @PathVariable Long id,
            @RequestBody EventRequest eventRequest) {
        ApiResponse<EventModel> response = eventService.updateEvent(id, eventRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-requests")
    public ResponseEntity<ApiResponse<List<EventModel>>> getMyRequestedEvents(Principal principal) {

        // 1. Check if the user is authenticated
        if (principal == null) {
            return new ResponseEntity<>(
                    new ApiResponse<>(false, "Unauthorized: No token provided", null, null),
                    HttpStatus.UNAUTHORIZED
            );
        }

        // 2. Get logged-in user's username (usually email)
        String username = principal.getName();

        // 3. Find the user object from the database
        UserModel student = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 4. Fetch all events created by this specific student
        List<EventModel> myRequests = eventRepo.findByCreatedBy(student);

        // 5. Return the list wrapped in ApiResponse
        return new ResponseEntity<>(new ApiResponse<>(
                true, "Fetched requested events successfully", myRequests, LocalDateTime.now()
        ), HttpStatus.OK);
    }

}