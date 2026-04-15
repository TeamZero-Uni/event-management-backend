package com.event.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactEmailRequest {
    private String name;
    private String from;
    private String subject;
    private String body;
}