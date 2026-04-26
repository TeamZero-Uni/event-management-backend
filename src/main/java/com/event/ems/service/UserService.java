package com.event.ems.service;

import com.event.ems.dto.UpdateUserRequest;
import com.event.ems.dto.UserResponse;
import com.event.ems.model.Role;
import com.event.ems.model.UserModel;
import com.event.ems.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel data = userRepo.findByUsername(username).orElse(null);
        if(data == null) throw  new UsernameNotFoundException("Invalid username.");
        UserDetails user = User.builder()
                .username(data.getUsername())
                .password(data.getPassword())
                .build();
        return user;
    }

    public String generateUsername(Role role) {

        String prefix;

        switch (role) {
            case STUDENT:
                prefix = "TG";
                break;
            case ORGANIZER:
                prefix = "ORG";
                break;
            default:
                prefix = "USR";
        }

        String lastUsername = userRepo.findLastUsernameByPrefix(prefix);

        int nextNumber = 1;

        if (lastUsername != null) {
            String numberPart = lastUsername.substring(prefix.length());
            nextNumber = Integer.parseInt(numberPart) + 1;
        }

        return prefix + String.format("%03d", nextNumber);
    }

    public List<UserResponse> getAllUsers() {
        return userRepo.findAll().stream()
                .map(user -> new UserResponse(
                        user.getUserId(),
                        user.getUsername(),
                        user.getFullname(),
                        user.getAddress(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getDepartment(),
                        user.getRole(),
                        user.getCreatedAt()
                ))
                .toList();
    }

    public UserResponse updateUserById(Long userId, UpdateUserRequest request, String requesterUsername) {
        UserModel targetUser = userRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserModel requester = userRepo.findByUsername(requesterUsername)
                .orElseThrow(() -> new RuntimeException("Requester not found"));

        boolean isSelfUpdate = requester.getUserId().equals(targetUser.getUserId());
        boolean isAdmin = requester.getRole() == Role.ADMIN;

        if (!isSelfUpdate && !isAdmin) {
            throw new RuntimeException("You can update only your own profile");
        }

        String fullname = firstNonBlank(request.getFullname(), request.getName());
        String email = firstNonBlank(request.getEmail());
        String address = firstNonBlank(request.getAddress(), request.getLocation());
        String phone = firstNonBlank(request.getPhone(), request.getTel());

        if (fullname != null) targetUser.setFullname(fullname);
        if (address != null) targetUser.setAddress(address);
        if (phone != null) targetUser.setPhone(phone);

        if (email != null) {
            UserModel existingByEmail = userRepo.findByEmail(email).orElse(null);
            if (existingByEmail != null && !existingByEmail.getUserId().equals(targetUser.getUserId())) {
                throw new IllegalArgumentException("Email is already in use");
            }
            targetUser.setEmail(email);
        }

        userRepo.save(targetUser);

        return new UserResponse(
                targetUser.getUserId(),
                targetUser.getUsername(),
                targetUser.getFullname(),
                targetUser.getAddress(),
                targetUser.getEmail(),
                targetUser.getPhone(),
                targetUser.getDepartment(),
                targetUser.getRole(),
                targetUser.getCreatedAt()
        );
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }
}
