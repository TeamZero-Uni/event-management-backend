package com.event.ems.service;

import com.event.ems.dto.ApiResponse;
import com.event.ems.model.EventModel;
import com.event.ems.repo.EventRepo;
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

    public ApiResponse<List<EventModel>> getAllEvents(){
        List<EventModel> events = eventRepo.findAll();
        return new ApiResponse<>(
                true,
                "Events Fatched succesfully",
                events,
                LocalDateTime.now()
        );
    }
}
