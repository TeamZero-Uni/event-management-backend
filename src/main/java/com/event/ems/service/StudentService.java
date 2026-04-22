package com.event.ems.service;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.StudentResponse;
import com.event.ems.dto.UserProfileDTO;
import com.event.ems.model.EventModel;
import com.event.ems.model.RegistrationModel;
import com.event.ems.model.StudentModel;
import com.event.ems.model.UserModel;
import com.event.ems.repo.RegistrationRepo;
import com.event.ems.repo.StudentRepo;
import com.event.ems.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {

    private final UserRepo userRepo;
    private final StudentRepo studentRepo;
    private final RegistrationRepo registrationRepo;

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