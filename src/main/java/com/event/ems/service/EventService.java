package com.event.ems.service;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.EventResponse;
import com.event.ems.model.EventModel;
import com.event.ems.repo.EventRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EventService {

    private final EventRepo eventRepo;

    public ApiResponse<List<EventResponse>> getAllEvents(){

        List<EventResponse> events = eventRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new ApiResponse<>(
                true,
                "Events fetched successfully",
                events,
                LocalDateTime.now()
        );
    }

    private EventResponse mapToResponse(EventModel event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .maxParticipants(event.getMaxParticipants())
                .posterUrl(event.getPosterUrl())
                .status(event.getStatus().name())
                .type(event.getType().name())
                .placeName(event.getVenue().getPlaceName())
                .capacity(event.getVenue().getCapacity())
                .fullname(event.getCreatedBy().getFullname())
                .email(event.getCreatedBy().getEmail())
                .build();
    }
}