package com.event.ems.service;

import com.event.ems.dto.AuthRequest;
import com.event.ems.dto.SimpleResponse;
import com.event.ems.model.UserDetailsModel;
import com.event.ems.model.UserModel;
import com.event.ems.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepo userRepo;
    private final PasswordEncoder  passwordEncoder;

    public SimpleResponse registerUser(AuthRequest req) {
        if(userRepo.findByUsername(req.getUsername()).isPresent()){
            return new SimpleResponse(null, "User already exists");
        }
        UserModel user = new UserModel();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(req.getRole());

        UserDetailsModel data = new UserDetailsModel(
                req.getUserId(),
                req.getFullName(),
                req.getEmail(),
                req.getPhone(),
                req.getAddress(),
                req.getDepartment(),
                req.getBatch(),
                user
        );
        user.setUserDetails(data);
        userRepo.save(user);

        return new SimpleResponse(null, "User registered successfully");
    }

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
}
