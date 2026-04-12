package com.event.ems.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "registrations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private EventModel event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;

    private LocalDateTime registrationDate;

    @Enumerated(EnumType.STRING)
    private RegitrationStatus status;

    private String email;
    private String tel_number;

    @PrePersist
    protected void onCreate() {
        this.registrationDate = LocalDateTime.now();
    }
}