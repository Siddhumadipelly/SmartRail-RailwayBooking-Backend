package com.smartrail.railway_booking.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    private String name;      // used only for signup
    private String email;
    private String password;
}
