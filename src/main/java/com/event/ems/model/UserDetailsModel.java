package com.event.ems.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;
    private String phone;
    private String address;
    private String department;
    private String batch;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserModel user;
}