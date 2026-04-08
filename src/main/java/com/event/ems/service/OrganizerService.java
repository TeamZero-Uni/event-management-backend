package com.event.ems.service;

import com.event.ems.dto.ApiResponse;
import com.event.ems.model.OrganizersModel;
import com.event.ems.repo.OrganizerRepo;
import com.event.ems.repo.StudentRepo;
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

    public  ApiResponse<List<OrganizersModel>> getOrganizerCount() {
        List<OrganizersModel> organizers = organizerRepo.findAll();
        return new ApiResponse<>(
                true,
                "Organizers fetched successfully",
                organizers,
                LocalDateTime.now()
        );
    }
}
