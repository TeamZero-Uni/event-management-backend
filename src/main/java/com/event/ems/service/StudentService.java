package com.event.ems.service;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.StudentDto;
import com.event.ems.model.Role;
import com.event.ems.model.StudentModel;
import com.event.ems.model.UserModel;
import com.event.ems.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {

    private final UserRepo repo;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public ApiResponse getAllStudents(){

        List<UserModel> students = repo.findAll()
                .stream()
                .filter(user -> user.getRole() == Role.STUDENT)
                .toList();

        return new ApiResponse("Success", students, null);
    }

    public ApiResponse getStudentById(Long id){

        UserModel student = repo.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if(student.getRole() != Role.STUDENT){
            throw new RuntimeException("User is not a student");
        }

        return new ApiResponse("Success", student,  null);
    }

    public ApiResponse addStudent(StudentDto dto){
        UserModel user = mapper.map(dto, UserModel.class);
        user.setRole(Role.STUDENT);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        StudentModel student = new StudentModel();
        student.setBatch(dto.getBatch());

        student.setUser(user);
        user.setStudentDetails(student);
        repo.save(user);
        return new ApiResponse("created new student", null, null);
    }

    public ApiResponse updateStudent(Long id, StudentDto dto){

        UserModel user = repo.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if(user.getRole() != Role.STUDENT){
            throw new RuntimeException("User is not a student");
        }

        user.setUsername(dto.getUsername());
        user.setFullname(dto.getFullname());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setDepartment(dto.getDepartment());

        if(user.getStudentDetails() != null){
            user.getStudentDetails().setBatch(dto.getBatch());
        }

        repo.save(user);

        return new ApiResponse("Student updated successfully", null, null);
    }

    public ApiResponse deleteStudent(Long id){

        UserModel user = repo.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if(user.getRole() != Role.STUDENT){
            throw new RuntimeException("User is not a student");
        }

        repo.delete(user);

        return new ApiResponse("Student deleted successfully", null, null);
    }
}
