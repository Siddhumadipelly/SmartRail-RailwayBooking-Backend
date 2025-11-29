package com.smartrail.railway_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SeatAvailabilityResponse {
    private Long scheduleId;
    private int totalSeats;
    private long bookedSeats;
    private int availableSeats;
}
