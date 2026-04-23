package com.event.ems.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The person receiving the notification
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;

    // The event the notification is about
    @ManyToOne
    @JoinColumn(name = "event_id")
    private EventModel event;

    // Keeps the event id even when event FK is cleared before deletion.
    private Long eventReferenceId;

    @Column(columnDefinition = "TEXT")
    private String message;

    private Boolean isRead = false;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate(){
        createdAt = LocalDateTime.now();
    }
}