package com.event.ems.controller;

import com.event.ems.dto.ApiResponse;
import com.event.ems.dto.UserResponse;
import com.event.ems.model.Role;
import com.event.ems.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping
	public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
		List<UserResponse> users = userService.getAllUsers();
		return ResponseEntity.ok(new ApiResponse<>(true, "Users fetched successfully", users, LocalDateTime.now()));
	}

	@PostMapping("/generate-username")
	public ResponseEntity<ApiResponse<String>> generateUsernameFromBody(@RequestBody Map<String, String> body) {
		String roleInput = body != null ? body.get("role") : null;
		Role parsedRole = parseAllowedRole(roleInput);
		System.out.println(parsedRole);
		String generatedUsername = userService.generateUsername(parsedRole);
		System.out.println(generatedUsername);
		return buildGeneratedUsernameResponse(generatedUsername);
	}

	private ResponseEntity<ApiResponse<String>> buildGeneratedUsernameResponse(String generatedUsername) {
		return ResponseEntity.ok(new ApiResponse<>(true, "Username generated successfully", generatedUsername, LocalDateTime.now()));
	}

	private Role parseAllowedRole(String role) {
		if (role == null || role.trim().isEmpty()) {
			throw new IllegalArgumentException("Role is required in request body. Example: { role: STUDENT }");
		}

		try {
			Role parsedRole = Role.valueOf(role.trim().toUpperCase());
			if (parsedRole != Role.STUDENT && parsedRole != Role.ORGANIZER) {
				throw new IllegalArgumentException("Invalid role. Allowed values: STUDENT, ORGANIZER");
			}
			return parsedRole;
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid role. Allowed values: STUDENT, ORGANIZER");
		}
	}
}
