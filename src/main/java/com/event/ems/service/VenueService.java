package com.event.ems.service;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.VenueRequest;
import com.event.ems.exception.VenueNotFoundException;
import com.event.ems.model.VenueModel;
import com.event.ems.repo.VenueRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepo venueRepo;

    public ApiResponse<List<VenueModel>> getAllVenues() {
        List<VenueModel> venues = venueRepo.findAll();
        return new ApiResponse<>(
                true,
                "Venues fetched successfully",
                venues,
                LocalDateTime.now()
        );
    }

    public ApiResponse<VenueModel> getVenueById(Long id) {
        VenueModel venue = venueRepo.findById(id)
                .orElseThrow(() -> new VenueNotFoundException("Venue not found with id: " + id));

        return new ApiResponse<>(
                true,
                "Venue fetched successfully",
                venue,
                LocalDateTime.now()
        );
    }

    public ApiResponse<Long> deleteVenueById(Long id) {
        if (!venueRepo.existsById(id)) {
            throw new VenueNotFoundException("Venue not found with id: " + id);
        }

        venueRepo.deleteById(id);

        return new ApiResponse<>(
                true,
                "Venue deleted successfully",
                id,
                LocalDateTime.now()
        );
    }

    public ApiResponse<VenueModel> createVenue(VenueRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }

        String placeName = request.getPlaceName() == null ? "" : request.getPlaceName().trim();
        if (placeName.isEmpty()) {
            throw new IllegalArgumentException("Place name is required");
        }

        if (request.getCapacity() == null || request.getCapacity() <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }

        if (venueRepo.findByPlaceNameIgnoreCase(placeName).isPresent()) {
            throw new IllegalArgumentException("Venue already exists with the same place name");
        }

        VenueModel venue = new VenueModel();
        venue.setPlaceName(placeName);
        venue.setCapacity(request.getCapacity());
        venue.setIsAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true);

        VenueModel savedVenue = venueRepo.save(venue);

        return new ApiResponse<>(
                true,
                "Venue created successfully",
                savedVenue,
                LocalDateTime.now()
        );
    }
}
