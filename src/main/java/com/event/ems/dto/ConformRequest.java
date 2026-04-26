package com.event.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConformRequest {
    private String eventTitle;
    private String eventDate;
    private String eventLocation;
    private String studentName;
    private String studentEmail;
    private String studentTel;
}
