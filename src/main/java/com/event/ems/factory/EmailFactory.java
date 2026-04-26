package com.event.ems.factory;

import com.event.ems.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EmailFactory {

    private final Map<String, EmailService> emailServices;

    @Autowired
    public EmailFactory(Map<String, EmailService> emailServices) {
        this.emailServices = emailServices;
    }

    public EmailService getService(String type) {
        EmailService service = emailServices.get(type);
        if (service == null) {
            throw new IllegalArgumentException("Invalid email type: " + type);
        }
        return service;
    }
}