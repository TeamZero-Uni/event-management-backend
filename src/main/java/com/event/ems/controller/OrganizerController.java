package com.event.ems.controller;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.OrganizerDto;
import com.event.ems.service.OrganizersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/organizer")
@RequiredArgsConstructor
public class OrganizerController {
    private final OrganizersService service;

    @PostMapping
    public ApiResponse register(@RequestBody OrganizerDto dto){
        ApiResponse res = service.addOrganizer(dto);
        return res;
    }

    @GetMapping
    public ApiResponse getOrganizers(){
        return service.getAllOrganizers();
    }

    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Long id){
        return service.getOrganizerById(id);
    }

    @PutMapping("/{id}")
    public ApiResponse updateById(@PathVariable Long id, @RequestBody OrganizerDto dto){
        return service.updateOrganizer(id, dto);
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteById(@PathVariable Long id){
        return service.deleteOrganizer(id);
    }
}
