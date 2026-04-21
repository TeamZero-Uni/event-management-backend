package com.event.ems.service;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.OrganizerResponse;
import com.event.ems.model.OrganizersModel;
import com.event.ems.repo.OrganizerRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrganizerService {
    private final OrganizerRepo organizerRepo;

    public ApiResponse<List<OrganizerResponse>> getAllOrganizers() {
        List<OrganizersModel> organizers = organizerRepo.findAll();

        List<OrganizerResponse> organizerResponses = organizers.stream()
                .map(organizer -> new OrganizerResponse(
                        organizer.getId(),
                        organizer.getUser().getUserId(),
                        organizer.getPosition(),
                        organizer.getClubName()
                ))
                .toList();

        return new ApiResponse<>(
                true,
                "Organizers fetched successfully",
                organizerResponses,
                LocalDateTime.now()
        );
    }
}
