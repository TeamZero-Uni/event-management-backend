package com.event.ems.service;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.OrganizerDto;
import com.event.ems.model.OrganizersModel;
import com.event.ems.model.Role;
import com.event.ems.model.UserModel;
import com.event.ems.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrganizersService {
    private final UserRepo repo;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public ApiResponse getAllOrganizers(){

        List<UserModel> organizer = repo.findAll()
                .stream()
                .filter(user -> user.getRole() == Role.ORGANIZER)
                .toList();

        return new ApiResponse("Success", organizer, null);
    }

    public ApiResponse getOrganizerById(Long id){

        UserModel organizer = repo.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        if(organizer.getRole() != Role.ORGANIZER){
            throw new RuntimeException("User is not a Organizer");
        }

        return new ApiResponse("Success", organizer,  null);
    }

    public ApiResponse addOrganizer(OrganizerDto dto){
        UserModel user = mapper.map(dto, UserModel.class);
        user.setRole(Role.ORGANIZER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        OrganizersModel organizer = new OrganizersModel();
        organizer.setPosition(dto.getPosition());
        organizer.setClubName(dto.getClubName());

        organizer.setUser(user);
        user.setOrganizerDetails(organizer);
        repo.save(user);
        return new ApiResponse("created new organizer", null, null);
    }

    public ApiResponse updateOrganizer(Long id, OrganizerDto dto){

        UserModel user = repo.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        if(user.getRole() != Role.ORGANIZER){
            throw new RuntimeException("User is not a Organizer");
        }

        user.setUsername(dto.getUsername());
        user.setFullname(dto.getFullname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setDepartment(dto.getDepartment());

        if(user.getOrganizerDetails() != null){
            user.getOrganizerDetails().setPosition(dto.getPosition());
            user.getOrganizerDetails().setClubName(dto.getClubName());
        }

        repo.save(user);

        return new ApiResponse("Organizer updated successfully", null, null);
    }

    public ApiResponse deleteOrganizer(Long id){

        UserModel user = repo.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        if(user.getRole() != Role.ORGANIZER){
            throw new RuntimeException("User is not a Organizer");
        }

        repo.delete(user);

        return new ApiResponse("Organizer deleted successfully", null, null);
    }
}
