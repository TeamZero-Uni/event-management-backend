package com.event.ems.controller;

import com.event.ems.dto.ConformRequest;
import com.event.ems.dto.ContactEmailRequest;
import com.event.ems.factory.EmailFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/email")
public class EmailController {

    @Autowired
    private EmailFactory emailFactory;

    @PostMapping("/send")
    public String sendMail(@RequestBody ContactEmailRequest request) {
        emailFactory.getService("CONTACT").send(request);
        return "Email sent successfully!";
    }

    @PostMapping("/conform")
    public String conformMail(@RequestBody ConformRequest request) {
        emailFactory.getService("REGISTRATION").send(request);
        return "Email sent successfully!";
    }
}