package com.event.ems.service;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.OrganizerCreateRequest;
import com.event.ems.dto.OrganizerResponse;
import com.event.ems.factory.EmailFactory;
import com.event.ems.model.OrganizersModel;
import com.event.ems.model.Role;
import com.event.ems.model.UserModel;
import com.event.ems.repo.*;
import com.event.ems.service.email.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class OrganizerService {
    private final OrganizerRepo organizerRepo;
    private final UserRepo userRepo;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RegistrationRepo registrationRepo;
    private final NotificationRepo notificationRepo;
    private final EventRepo eventRepo;
    private final EmailFactory emailFactory;

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

    public ApiResponse<OrganizerResponse> createOrganizer(OrganizerCreateRequest request) {
        validateRequest(request);
        System.out.println(request);
        String username = normalizeText(request.getUsername());
        if (username == null) {
            username = userService.generateUsername(Role.ORGANIZER);
        }

        if (userRepo.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        String email = request.getEmail().trim();
        if (userRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        UserModel user = new UserModel();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
        user.setEmail(email);
        user.setRole(Role.ORGANIZER);
        user.setFullname(request.getUsername().trim());

        UserModel savedUser = userRepo.save(user);

        OrganizersModel organizer = new OrganizersModel();
        organizer.setUser(savedUser);
        organizer.setClubName(request.getClubName().trim());
        organizer.setPosition(null);
        OrganizersModel savedOrganizer = organizerRepo.save(organizer);

        savedUser.setOrganizerDetails(savedOrganizer);

        EmailService emailService = emailFactory.getService("CREDENTIALS");

        emailService.send(Map.of(
                "email", email,
                "username", username,
                "password", request.getPassword()
        ));

        OrganizerResponse response = new OrganizerResponse(
                savedOrganizer.getId(),
                savedUser.getUserId(),
                savedOrganizer.getPosition(),
                savedOrganizer.getClubName()
        );

        return new ApiResponse<>(
                true,
                "Organizer created successfully",
                response,
                LocalDateTime.now()
        );
    }

    private void validateRequest(OrganizerCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (normalizeText(request.getPassword()) == null) {
            throw new IllegalArgumentException("Password is required");
        }
        if (normalizeText(request.getEmail()) == null) {
            throw new IllegalArgumentException("Email is required");
        }
        if (normalizeText(request.getClubName()) == null) {
            throw new IllegalArgumentException("Club name is required");
        }
        if (request.getRole() != null && !"ORGANIZER".equalsIgnoreCase(request.getRole().trim())) {
            throw new IllegalArgumentException("Role must be ORGANIZER");
        }
    }

    private String normalizeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    public ApiResponse<Void> deleteOrganizer(Long organizerId) {
        OrganizersModel organizer = organizerRepo.findById(organizerId)
                .orElseThrow(() -> new IllegalArgumentException("Organizer not found"));

        UserModel user = organizer.getUser();
        Long userId = user.getUserId();

        registrationRepo.deleteAllByUserId(userId);
        notificationRepo.deleteAllByUserId(userId);
        eventRepo.deleteAllByCreatedByUserId(userId);

        organizerRepo.delete(organizer);
        userRepo.delete(user);

        return new ApiResponse<>(
                true,
                "Organizer deleted successfully",
                null,
                LocalDateTime.now()
        );
    }

}
