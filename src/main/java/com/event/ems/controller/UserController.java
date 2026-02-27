package com.event.ems.controller;

import com.event.ems.dto.AuthRequest;
import com.event.ems.dto.SimpleResponse;
import com.event.ems.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1/user")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<SimpleResponse> register(@RequestBody AuthRequest req) {
        SimpleResponse res = userService.registerUser(req);
        return ResponseEntity.status(
                res.getError() == null ?  HttpStatus.BAD_REQUEST : HttpStatus.CREATED
        ).body(res);
    }

    @GetMapping("/me")
    public String me(){
        return "me";
    }
}
