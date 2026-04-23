package com.event.ems.controller;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.StudentCreateRequest;
import com.event.ems.dto.StudentResponse;
import com.event.ems.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
