package com.event.ems.controller;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.EventRequest;
import com.event.ems.model.EventModel;
import com.event.ems.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ApiResponse<List<EventModel>> getAllEvents() {
        return eventService.getAllEvents();
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

}