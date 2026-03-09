package com.event.ems.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "organizer_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizersModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private UserModel user;

    @Column(name = "position", length = 100)
    private String position;

    @Column(name = "club_name", length = 100)
    private String clubName;
}