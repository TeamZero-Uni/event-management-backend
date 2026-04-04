package com.event.ems.controller;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.EventResponse;
import com.event.ems.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ApiResponse<List<EventResponse>> getAllEvents(){
        return eventService.getAllEvents();
    }
}