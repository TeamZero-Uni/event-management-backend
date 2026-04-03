package com.event.ems.controller;

import com.event.ems.dto.ApiResponse;
import com.event.ems.model.OrganizersModel;
import com.event.ems.model.StudentModel;
import com.event.ems.service.OrganizerService;
import com.event.ems.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/organizers")
@RequiredArgsConstructor
public class OrganizerController {

    private final OrganizerService organizerService;

    @GetMapping

    public ApiResponse<List<OrganizersModel>> getOrganizerCount() {
        return organizerService.getOrganizerCount();
    }
}
