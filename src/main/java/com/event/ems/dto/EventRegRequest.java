package com.event.ems.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EventRegRequest {
    private Long event_id;
    private String username;
    private String email;
    private String telNumber;
    private String status;
}
