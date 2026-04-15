package com.event.ems.controller;

import com.event.ems.dto.ContactEmailRequest;
import com.event.ems.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public String sendMail(
            @RequestBody ContactEmailRequest request
            ) {
        emailService.sendContactEmail(request.getName(), request.getFrom(), request.getSubject(), request.getBody());
        return "Email sent successfully!";
    }
}