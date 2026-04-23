package com.event.ems.service;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.StudentCreateRequest;
import com.event.ems.dto.StudentResponse;
import com.event.ems.model.Role;
import com.event.ems.model.StudentModel;
import com.event.ems.model.UserModel;
import com.event.ems.repo.StudentRepo;
import com.event.ems.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepo studentRepo;
    private final UserRepo userRepo;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

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
}
