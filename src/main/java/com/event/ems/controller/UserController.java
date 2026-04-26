package com.event.ems.controller;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.UpdateUserRequest;
import com.event.ems.dto.UserResponse;
import com.event.ems.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/all")
	public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
		List<UserResponse> users = userService.getAllUsers();
		return ResponseEntity.ok(new ApiResponse<>(true, "Users fetched successfully", users, LocalDateTime.now()));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<UserResponse>> updateUserById(
			@PathVariable("id") Long id,
			@RequestBody UpdateUserRequest request,
			Authentication authentication) {
		UserResponse updated = userService.updateUserById(id, request, authentication.getName());
		return ResponseEntity.ok(new ApiResponse<>(true, "User updated successfully", updated, LocalDateTime.now()));
	}
}
