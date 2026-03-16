package com.event.ems.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private EventModel event;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;

    private Boolean attended;

    @ManyToOne
    @JoinColumn(name = "marked_by")
    private UserModel markedBy;

    private LocalDateTime markedAt;
}