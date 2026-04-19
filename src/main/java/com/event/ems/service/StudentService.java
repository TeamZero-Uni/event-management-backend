package com.event.ems.service;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.StudentResponse;
import com.event.ems.model.StudentModel;
import com.event.ems.repo.StudentRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepo studentRepo;

    public ApiResponse<List<StudentResponse>> getAllStudents() {
        List<StudentModel> students = studentRepo.findAll();

        List<StudentResponse> studentResponses = students.stream()
                .map(student -> new StudentResponse(
                        student.getId(),
                        student.getBatch(),
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
}

