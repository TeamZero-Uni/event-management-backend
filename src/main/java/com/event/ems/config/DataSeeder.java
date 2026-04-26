package com.event.ems.config;

import com.event.ems.model.*;
import com.event.ems.repo.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

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

            UserModel student2 = new UserModel();
            student2.setUsername("TG002");
            student2.setPassword(encoder.encode("123456"));
            student2.setFullname("John S");
            student2.setEmail("joh@ems.com");
            student2.setDepartment("Software Engineering");
            student2.setRole(Role.STUDENT);
            userRepo.save(student2);

            StudentModel s2 = new StudentModel();
            s2.setUser(student2);
            s2.setBatch("2022");
            student2.setStudentDetails(s2);
            studentRepo.save(s2);

            UserModel organizer = new UserModel();
            organizer.setUsername("ORG001");
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
        };
    }
}
