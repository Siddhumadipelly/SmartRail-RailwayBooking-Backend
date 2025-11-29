package com.smartrail.railway_booking.controller;

import com.smartrail.railway_booking.dto.BookingRequest;
import com.smartrail.railway_booking.model.*;
import com.smartrail.railway_booking.repository.BookingRepository;
import com.smartrail.railway_booking.repository.PassengerRepository;
import com.smartrail.railway_booking.repository.TrainScheduleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final TrainScheduleRepository scheduleRepository;
    private final PassengerRepository passengerRepository;

    public BookingController(BookingRepository bookingRepository,
                             TrainScheduleRepository scheduleRepository,
                             PassengerRepository passengerRepository) {
        this.bookingRepository = bookingRepository;
        this.scheduleRepository = scheduleRepository;
        this.passengerRepository = passengerRepository;
    }

    // ---------- 1) Create a booking ----------
    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        try {
            // 1. Get schedule
            TrainSchedule schedule = scheduleRepository.findById(request.getScheduleId())
                    .orElseThrow(() -> new RuntimeException("Schedule not found"));

            // 2. Save passenger
            Passenger passenger = Passenger.builder()
                    .name(request.getName())
                    .age(request.getAge())
                    .gender(request.getGender())
                    .phone(request.getPhone())
                    .email(request.getEmail())
                    .build();
            passenger = passengerRepository.save(passenger);

            // 3. Determine base fare (fallback if DB has null)
            double baseFare = (schedule.getBaseFare() != null)
                    ? schedule.getBaseFare()
                    : 200.0; // safe default

            // 4. Determine multiplier based on travel class
            String travelClass = request.getTravelClass();
            if (travelClass == null || travelClass.isBlank()) {
                // default to SL if not provided
                travelClass = "SL";
            }

            double multiplier = getMultiplierForClass(travelClass);
            double finalFare = baseFare * multiplier;

            // 5. Build booking
            Booking booking = Booking.builder()
                    .pnr(generatePNR())
                    .schedule(schedule)
                    .passenger(passenger)
                    .seatNumber(request.getSeatNumber() != null ? request.getSeatNumber() : "S1")
                    .status(BookingStatus.BOOKED)
                    .bookingTime(LocalDateTime.now())
                    .travelClass(travelClass)
                    .fare(finalFare)
                    .build();

            Booking saved = bookingRepository.save(booking);
            return ResponseEntity.ok(saved);

        } catch (Exception ex) {
            ex.printStackTrace();
            // send error message to frontend so it can show "Booking failed"
            return ResponseEntity.status(500).body("Booking failed: " + ex.getMessage());
        }
    }

    // ---------- helper: multiplier ----------
    private double getMultiplierForClass(String travelClass) {
        // normalize
        String cls = travelClass.toUpperCase();

        // you can tune these values
        return switch (cls) {
            case "SL" -> 1.0;
            case "3A" -> 1.8;
            case "2A" -> 2.2;
            case "1A" -> 3.0;
            case "CC" -> 1.6;
            case "EC" -> 2.5;
            default -> 1.0; // unknown => treat as SL
        };
    }

    // ---------- 2) Get ALL bookings (for Admin page) ----------
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingRepository.findAll());
    }

    // ---------- 3) Get bookings by email (for My Bookings) ----------
    @GetMapping("/by-email")
    public ResponseEntity<List<Booking>> getByEmail(@RequestParam String email) {
        List<Booking> bookings = bookingRepository.findByPassengerEmailIgnoreCase(email);
        return ResponseEntity.ok(bookings);
    }

    // ---------- 4) Get bookings by phone (optional My Bookings) ----------
    @GetMapping("/by-phone")
    public ResponseEntity<List<Booking>> getByPhone(@RequestParam String phone) {
        List<Booking> bookings = bookingRepository.findByPassengerPhone(phone);
        return ResponseEntity.ok(bookings);
    }

    // ---------- 5) Cancel booking by PNR ----------
    @PostMapping("/{pnr}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable String pnr) {
        return bookingRepository.findByPnr(pnr)
                .map(booking -> {
                    if (booking.getStatus() == BookingStatus.CANCELLED) {
                        return ResponseEntity.badRequest().body("Booking already cancelled");
                    }
                    booking.setStatus(BookingStatus.CANCELLED);
                    Booking saved = bookingRepository.save(booking);
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() ->
                        ResponseEntity.status(404).body("Booking not found for PNR: " + pnr)
                );
    }

    // ---------- Helper to generate simple PNR ----------
    private String generatePNR() {
        String prefix = "PNR";
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(prefix);
        for (int i = 0; i < 7; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
