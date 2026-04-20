package com.event.ems.service;

import com.event.ems.dto.UserResponse;
import com.event.ems.model.Role;
import com.event.ems.model.UserModel;
import com.event.ems.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + data.getRole().name())))
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
}
