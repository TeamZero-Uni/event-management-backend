package com.event.ems.service;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.StudentCreateRequest;
import com.event.ems.dto.StudentResponse;
import com.event.ems.factory.EmailFactory;
import com.event.ems.model.Role;
import com.event.ems.dto.UserProfileDTO;
import com.event.ems.model.EventModel;
import com.event.ems.model.RegistrationModel;
import com.event.ems.model.StudentModel;
import com.event.ems.model.UserModel;
import com.event.ems.model.UserModel;
import com.event.ems.repo.*;
import com.event.ems.service.email.EmailService;
import jakarta.servlet.Registration;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {

    private final UserRepo userRepo;
    private final StudentRepo studentRepo;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RegistrationRepo registrationRepo;
    private final NotificationRepo notificationRepo;
    private final EventRepo eventRepo;
    private final EmailFactory emailFactory;

    public ApiResponse<List<StudentResponse>> getAllStudents() {
        List<StudentModel> students = studentRepo.findAll();

        List<StudentResponse> studentResponses = students.stream()
                .map(student -> new StudentResponse(
                        student.getId(),
                        student.getBatch(),
                        student.getYear(),
                        student.getUser().getUserId(),
                        student.getUser().getUsername(),
                        student.getUser().getFullname(),
                        student.getUser().getEmail()
                ))
                .toList();

        return new ApiResponse<>(
                true,
                "Students fetched successfully",
                studentResponses,
                LocalDateTime.now()
        );
    }

    public ApiResponse<StudentResponse> createStudent(StudentCreateRequest request) {
        validateRequest(request);

        String username = normalizeText(request.getUsername());
        if (username == null) {
            username = userService.generateUsername(Role.STUDENT);
        }

        if (userRepo.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        String email = request.getEmail().trim();
        if (userRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        UserModel user = new UserModel();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
        user.setFullname(normalizeText(request.getFullname()) != null ? request.getFullname().trim() : username);
        user.setEmail(email);
        user.setDepartment(request.getDepartment().trim());
        user.setRole(Role.STUDENT);

        UserModel savedUser = userRepo.save(user);

        StudentModel student = new StudentModel();
        student.setBatch(String.valueOf(request.getBatch()));
        student.setYear(1);
        student.setUser(savedUser);
        StudentModel savedStudent = studentRepo.save(student);

        savedUser.setStudentDetails(savedStudent);

        EmailService emailService = emailFactory.getService("CREDENTIALS");

        emailService.send(Map.of(
                "email", email,
                "username", username,
                "password", request.getPassword()
        ));

        StudentResponse response = new StudentResponse(
                savedStudent.getId(),
                savedStudent.getBatch(),
                savedStudent.getYear(),
                savedUser.getUserId(),
                savedUser.getUsername(),
                savedUser.getFullname(),
                savedUser.getEmail()
        );

        return new ApiResponse<>(
                true,
                "Student created successfully",
                response,
                LocalDateTime.now()
        );
    }

    private void validateRequest(StudentCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (normalizeText(request.getPassword()) == null) {
            throw new IllegalArgumentException("Password is required");
        }
        if (normalizeText(request.getDepartment()) == null) {
            throw new IllegalArgumentException("Department is required");
        }
        if (normalizeText(request.getEmail()) == null) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getBatch() == null) {
            throw new IllegalArgumentException("Batch is required");
        }
        if (request.getRole() != null && !"STUDENT".equalsIgnoreCase(request.getRole().trim())) {
            throw new IllegalArgumentException("Role must be STUDENT");
        }
    }

    private String normalizeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    @Transactional
    public ApiResponse<Void> deleteStudent(Long studentId) {
        StudentModel student = studentRepo.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        UserModel user = student.getUser();
        Long userId = user.getUserId();

        registrationRepo.deleteAllByUserId(userId);
        notificationRepo.deleteAllByUserId(userId);
        eventRepo.deleteAllByCreatedByUserId(userId);

        studentRepo.delete(student);
        userRepo.delete(user);

        return new ApiResponse<>(
                true,
                "Student deleted successfully",
                null,
                LocalDateTime.now()
        );
    }

    public UserProfileDTO getStudentProfile(String username) {
        UserModel user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileDTO dto = new UserProfileDTO();
        dto.setName(user.getFullname());
        dto.setEmail(user.getEmail());
        dto.setTel(user.getPhone());
        dto.setDepartment(user.getDepartment());
        dto.setTgNumber(user.getUsername());
        dto.setAvatar(user.getAvatar());

        Optional<StudentModel> studentDetails = studentRepo.findByUser(user);
        if (studentDetails.isPresent()) {
            dto.setBatch(studentDetails.get().getBatch());
        } else {
            dto.setBatch("N/A");
        }

        return dto;
    }

    public void updateStudentProfile(String username, String name, String email, String tel, String avatarUrl) {
        UserModel user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (name != null && !name.isBlank()) user.setFullname(name);
        if (email != null && !email.isBlank()) user.setEmail(email);
        if (tel != null && !tel.isBlank()) user.setPhone(tel);
        if (avatarUrl != null && !avatarUrl.isBlank()) {
            if (!avatarUrl.startsWith("http://") && !avatarUrl.startsWith("https://")) {
                throw new RuntimeException("Invalid avatar URL");
            }
            user.setAvatar(avatarUrl);
        }

        userRepo.save(user);
    }

    public List<EventModel> getRegisteredEvents(String username) {
        UserModel user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return registrationRepo.findByUser(user).stream()
                .filter(reg -> reg.getStatus() != null && reg.getStatus().toString().equals("APPROVED"))
                .map(RegistrationModel::getEvent)
                .toList();
    }
}