package com.event.ems.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDate eventDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private Integer maxParticipants;

    private String posterUrl;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @Enumerated(EnumType.STRING)
    private EventType type;

    @ManyToOne
    @JoinColumn(name = "venue_id")
    private VenueModel venue;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private UserModel createdBy;
}