package com.event.ems.service;

import com.event.ems.model.UserModel;
import com.event.ems.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
}
