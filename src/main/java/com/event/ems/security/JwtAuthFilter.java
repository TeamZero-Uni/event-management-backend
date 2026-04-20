package com.event.ems.security;

import com.event.ems.model.UserModel;
import com.event.ems.repo.UserRepo;
import com.event.ems.utils.JwtHelp;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtHelp jwt;
    private  final UserRepo repo;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();
        System.out.println("req path : " + path);

        if (path.equals("/api/v1/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        System.out.println("Auth Header : " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("Auth Header is Not Valid ");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        System.out.println("token : " + token);

        String username = jwt.extractUsername(token);
        System.out.println("Extract Username : " + username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserModel userData = repo.findByUsername(username).orElse(null);
            if (userData != null && jwt.isValid(token, userData)) {

                UserDetails userDetails = User.builder()
                        .username(userData.getUsername())
                        .password(userData.getPassword())
                        .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + userData.getRole().name())))
                        .build();

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(auth);

                System.out.println("Authentication Success");
                System.out.println("Authorities: " + userDetails.getAuthorities());
            }
        }
        filterChain.doFilter(request, response);
    }
}
