package com.event.ems.controller;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.StudentCreateRequest;
import com.event.ems.dto.StudentResponse;
import com.event.ems.dto.UserProfileDTO;
import com.event.ems.model.EventModel;
import com.event.ems.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("all")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(@RequestBody StudentCreateRequest request) {
        return ResponseEntity.ok(studentService.createStudent(request));
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentService.deleteStudent(studentId));
    }

    @GetMapping("profile")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getProfile(Authentication auth) {
        UserProfileDTO profile = studentService.getStudentProfile(auth.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Profile fetched successfully", profile, LocalDateTime.now()));
    }

    @PutMapping("profile")
    public ResponseEntity<ApiResponse<String>> updateProfile(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "tel", required = false) String tel,
            @RequestParam(value = "avatar", required = false) String avatarUrl,
            Authentication auth) {

        try {
            studentService.updateStudentProfile(auth.getName(), name, email, tel, avatarUrl);
            return ResponseEntity.ok(new ApiResponse<>(true, "Profile updated successfully", null, LocalDateTime.now()));
        } catch (Exception e) {
            System.out.println("PROFILE UPDATE ERROR");
            e.printStackTrace();
            throw new RuntimeException("Failed to update profile");
        }
    }

    @GetMapping("my-events")
    public ResponseEntity<ApiResponse<List<EventModel>>> getMyEvents(Authentication auth) {
        try {
            List<EventModel> events = studentService.getRegisteredEvents(auth.getName());
            return ResponseEntity.ok(new ApiResponse<>(true, "Registered events fetched", events, LocalDateTime.now()));
        } catch (Exception e) {
            System.out.println("MY EVENTS ERROR");
            e.printStackTrace();
            throw e;
        }
    }
}