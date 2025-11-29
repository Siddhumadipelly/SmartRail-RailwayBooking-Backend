package com.smartrail.railway_booking.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequest {

    private Long scheduleId;

    private String name;
    private int age;
    private String gender;
    private String phone;
    private String email;

    private String seatNumber;   // optional, we still support this

    // ✅ new: class type user selected (SL, 3A, 2A, 1A, CC, EC)
    private String travelClass;
}
