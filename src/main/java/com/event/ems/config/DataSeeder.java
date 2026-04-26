package com.event.ems.config;

import com.event.ems.model.*;
import com.event.ems.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
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
            VenueRepo venueRepo,
            PasswordEncoder encoder) {
        return args -> {

            if (userRepo.count() > 0) {
                return;
            }

            UserModel admin = new UserModel();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("admin"));
            admin.setFullname("System Admin");
            admin.setEmail("admin@ems.com");
            admin.setPhone("0710000000");
            admin.setDepartment("IT");
            admin.setRole(Role.ADMIN);
            userRepo.save(admin);

            UserModel student1 = new UserModel();
            student1.setUsername("TG001");
            student1.setPassword(encoder.encode("123456"));
            student1.setFullname("John Silva");
            student1.setEmail("john@ems.com");
            student1.setDepartment("Software Engineering");
            student1.setRole(Role.STUDENT);
            userRepo.save(student1);

            StudentModel s1 = new StudentModel();
            s1.setUser(student1);
            s1.setBatch("2022");
            s1.setYear(1);
            student1.setStudentDetails(s1);
            studentRepo.save(s1);

            UserModel organizer = new UserModel();
            organizer.setUsername("organizer1");
            organizer.setPassword(encoder.encode("123456"));
            organizer.setFullname("Nimal Perera");
            organizer.setEmail("nimal@ems.com");
            organizer.setPhone("0711111111");
            organizer.setDepartment("Event Management");
            organizer.setRole(Role.ORGANIZER);

            OrganizersModel o1 = new OrganizersModel();
            o1.setUser(organizer);
            o1.setPosition("Event Coordinator");
            o1.setClubName("Tech Club");

            organizer.setOrganizerDetails(o1);

            userRepo.save(organizer);

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
            event1.setBudget(new BigDecimal("50000.00"));
            eventRepo.save(event1);

            EventModel event2 = new EventModel();
            event2.setTitle("Annual Music Festival");
            event2.setDescription("Enjoy live music, food stalls, and fun activities");
            event2.setEventDate(LocalDate.now().plusDays(10));
            event2.setStartTime(LocalTime.of(16, 0));
            event2.setEndTime(LocalTime.of(22, 0));
            event2.setMaxParticipants(150);
            event2.setPosterUrl("festival.jpg");
            event2.setStatus(EventStatus.ACCEPTED);
            event2.setType(EventType.FESTIVAL);
            event2.setVenue(hall);
            event2.setCreatedBy(organizer);
            event2.setBudget(new BigDecimal("120000.00"));

            eventRepo.save(event2);

            RegistrationModel r1 = new RegistrationModel();
            r1.setEvent(event1);
            r1.setUser(student1);
            r1.setRegistrationDate(LocalDateTime.now());
            r1.setStatus(RegitrationStatus.APPROVED);
            registrationRepo.save(r1);
        };
    }
}
