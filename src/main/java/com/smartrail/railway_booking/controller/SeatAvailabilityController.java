package com.smartrail.railway_booking.controller;

import com.smartrail.railway_booking.dto.SeatAvailabilityResponse;
import com.smartrail.railway_booking.model.BookingStatus;
import com.smartrail.railway_booking.model.TrainSchedule;
import com.smartrail.railway_booking.repository.BookingRepository;
import com.smartrail.railway_booking.repository.TrainScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seat-availability")
@CrossOrigin(origins = "http://localhost:3000")
public class SeatAvailabilityController {

    @Autowired
    private TrainScheduleRepository trainScheduleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @GetMapping("/{scheduleId}")
    public ResponseEntity<SeatAvailabilityResponse> getSeatAvailability(
            @PathVariable Long scheduleId) {

        TrainSchedule schedule = trainScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        // Count all NON-CANCELLED bookings for this schedule
        long bookedSeats = bookingRepository
                .countByScheduleIdAndStatusNot(scheduleId, BookingStatus.CANCELLED);

        int totalSeats = schedule.getTotalSeats();
        int availableSeats = totalSeats - (int) bookedSeats;
        if (availableSeats < 0) availableSeats = 0;

        SeatAvailabilityResponse response = new SeatAvailabilityResponse(
                scheduleId,
                totalSeats,
                bookedSeats,
                availableSeats
        );

        return ResponseEntity.ok(response);
    }
}
