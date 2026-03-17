package com.event.ems.controller;

import com.event.ems.dto.ApiResponse;
import com.event.ems.model.EventModel;
import com.event.ems.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ApiResponse<List<EventModel>> getAllEvents(){
        return eventService.getAllEvents();
    }
}

