package com.event.ems.service;

import com.event.ems.dto.EventRegRequest;
import com.event.ems.model.*;
import com.event.ems.repo.EventRepo;
import com.event.ems.repo.RegistrationRepo;
import com.event.ems.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class RegisterService {

    private final RegistrationRepo repo;
    private final UserRepo userRepo;
    private final EventRepo eventRepo;

    public void registerEvent(EventRegRequest request) {

        UserModel user = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        EventModel event = eventRepo.findById(request.getEvent_id())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        boolean alreadyRegistered = repo.existsByUser_UserIdAndEvent_Id(
                user.getUserId(),
                event.getId()
        );

        if (alreadyRegistered) {
            throw new RuntimeException("Already registered for this event");
        }

        RegistrationModel registration = new RegistrationModel();
        registration.setUser(user);
        registration.setEvent(event);
        registration.setEmail(request.getEmail());
        registration.setTel_number(request.getTelNumber());
        registration.setStatus(RegitrationStatus.valueOf(request.getStatus()));

        repo.save(registration);
    }
}