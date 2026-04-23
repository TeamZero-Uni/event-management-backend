package com.event.ems.service;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.NotificationRequest;
import com.event.ems.dto.NotificationResponse;
import com.event.ems.exception.EventNotFoundException;
import com.event.ems.exception.UserNotFoundException;
import com.event.ems.model.EventModel;
import com.event.ems.model.NotificationModel;
import com.event.ems.model.UserModel;
import com.event.ems.repo.EventRepo;
import com.event.ems.repo.NotificationRepo;
import com.event.ems.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepo notificationRepo;
    private final UserRepo userRepo;
    private final EventRepo eventRepo;

    public ApiResponse<NotificationResponse> createNotification(NotificationRequest request) {
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User id is required");
        }
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Message is required");
        }

        UserModel receiver = userRepo.findByUserId(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + request.getUserId()));

        EventModel event = null;
        if (request.getEventId() != null) {
            event = eventRepo.findById(request.getEventId())
                    .orElseThrow(() -> new EventNotFoundException("Event not found: " + request.getEventId()));
        }

        NotificationModel notification = new NotificationModel();
        notification.setUser(receiver);
        notification.setEvent(event);
        notification.setEventReferenceId(request.getEventId());
        notification.setMessage(request.getMessage().trim());
        notification.setIsRead(false);

        NotificationModel saved = notificationRepo.save(notification);

        NotificationResponse response = new NotificationResponse(
                saved.getId(),
                saved.getUser() != null ? saved.getUser().getUserId() : null,
                saved.getEvent() != null ? saved.getEvent().getId() : null,
                saved.getEventReferenceId(),
                saved.getMessage(),
                saved.getIsRead(),
                saved.getCreatedAt()
        );

        return new ApiResponse<>(true, "Notification created successfully", response, LocalDateTime.now());
    }

    // Fetch notifications based on strictly registered events and Organizer role
    public List<NotificationResponse> getMyNotifications(String username) {
        UserModel student = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Aluth Repo method eka call karanawa
        return notificationRepo.findBroadcastsForStudentEvents(student).stream()
                .map(n -> new NotificationResponse(
                        n.getId(),
                        n.getUser() != null ? n.getUser().getUserId() : null,
                        n.getEvent() != null ? n.getEvent().getId() : null, // event_id
                        n.getEventReferenceId(), // event_reference_id
                        n.getMessage(),
                        n.getIsRead(),
                        n.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    public void markAsRead(Long notificationId, String username) {
        NotificationModel notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setIsRead(true);
        notificationRepo.save(notification);
    }
}