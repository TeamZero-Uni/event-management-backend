package com.event.ems.service;

import com.event.ems.dto.ApiResponse;
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

    public ApiResponse<List<StudentModel>> getStudentsCount() {
        List<StudentModel> students = studentRepo.findAll();
        return new ApiResponse<>(
                true,
                "Students fetched successfully",
                students,
                LocalDateTime.now()
        );
    }
}

