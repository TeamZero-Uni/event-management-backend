package com.event.ems.config;

import com.event.ems.model.*;
import com.event.ems.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seedDatabase(
            UserRepo userRepo,
            RegistrationRepo registrationRepo,
            EventRepo eventRepo,
            StudentRepo studentRepo,
            VenueRepo venueRepo) {
        return args -> {

            if (userRepo.count() > 0) {
                return;
            }

            UserModel admin = new UserModel();
            admin.setUsername("admin");
            admin.setPassword("admin");
            admin.setFullname("System Admin");
            admin.setEmail("admin@ems.com");
            admin.setPhone("0710000000");
            admin.setDepartment("IT");
            admin.setRole(Role.ADMIN);
            userRepo.save(admin);

            UserModel student1 = new UserModel();
            student1.setUsername("student1");
            student1.setPassword("123456");
            student1.setFullname("John Silva");
            student1.setEmail("john@ems.com");
            student1.setDepartment("Software Engineering");
            student1.setRole(Role.STUDENT);
            userRepo.save(student1);

            StudentModel s1 = new StudentModel();
            s1.setUser(student1);
            s1.setBatch("2022");
            studentRepo.save(s1);

            VenueModel hall = new VenueModel();
            hall.setPlaceName("Main Hall");
            hall.setCapacity(200);
            hall.setIsAvailable(true);
            venueRepo.save(hall);

            EventModel event1 = new EventModel();
            event1.setTitle("Spring Boot Workshop");
            event1.setDescription("Learn Spring Boot fundamentals");
            event1.setEventDate(LocalDate.now().plusDays(5));
            event1.setStartTime(LocalTime.of(10,0));
            event1.setEndTime(LocalTime.of(13,0));
            event1.setMaxParticipants(100);
            event1.setPosterUrl("poster1.jpg");
            event1.setStatus(EventStatus.ACCEPTED);
            event1.setType(EventType.WORKSHOP);
            event1.setVenue(hall);
            event1.setCreatedBy(admin);
            eventRepo.save(event1);

            RegistrationModel r1 = new RegistrationModel();
            r1.setEvent(event1);
            r1.setUser(student1);
            r1.setRegistrationDate(LocalDateTime.now());
            r1.setStatus("APPROVED");
            registrationRepo.save(r1);
        };
    }
}
