package com.event.ems.controller;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.VenueRequest;
import com.event.ems.model.VenueModel;
import com.event.ems.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;

    @GetMapping
    public ApiResponse<List<VenueModel>> getAllVenues() {
        return venueService.getAllVenues();
    }

    @GetMapping("{id}")
    public ApiResponse<VenueModel> getVenueById(@PathVariable Long id) {
        return venueService.getVenueById(id);
    }

    @DeleteMapping("{id}")
    public ApiResponse<Long> deleteVenueById(@PathVariable Long id) {
        return venueService.deleteVenueById(id);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VenueModel>> createVenue(@RequestBody VenueRequest request) {
        ApiResponse<VenueModel> response = venueService.createVenue(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
