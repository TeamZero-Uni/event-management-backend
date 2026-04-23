package com.event.ems.controller;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.OrganizerCreateRequest;
import com.event.ems.dto.OrganizerResponse;
import com.event.ems.service.OrganizerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/organizers")
@RequiredArgsConstructor
public class OrganizerController {

    private final OrganizerService organizerService;

    @GetMapping({"/all"})
    public ResponseEntity<ApiResponse<List<OrganizerResponse>>> getAllOrganizers() {
        return ResponseEntity.ok(organizerService.getAllOrganizers());
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<OrganizerResponse>> createOrganizer(@RequestBody OrganizerCreateRequest request) {
        return ResponseEntity.ok(organizerService.createOrganizer(request));
    }
}
