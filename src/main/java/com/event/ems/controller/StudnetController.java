package com.event.ems.controller;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.StudentDto;
import com.event.ems.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudnetController {
    private final StudentService service;

    @PostMapping
    public ApiResponse registerStudent(@RequestBody StudentDto dto){
        return service.addStudent(dto);
    }

    @GetMapping
    public ApiResponse getStudents(){
        return service.getAllStudents();
    }

    @GetMapping("/{id}")
    public ApiResponse getById(@PathVariable Long id){
        return service.getStudentById(id);
    }

    @PutMapping("/{id}")
    public ApiResponse updateById(@PathVariable Long id, @RequestBody StudentDto dto){
        return service.updateStudent(id, dto);
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteById(@PathVariable Long id){
        return service.deleteStudent(id);
    }
}
