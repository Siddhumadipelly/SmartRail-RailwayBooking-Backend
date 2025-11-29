package com.smartrail.railway_booking.repository;

import com.smartrail.railway_booking.model.Booking;
import com.smartrail.railway_booking.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // For "My Bookings" by email
    List<Booking> findByPassengerEmailIgnoreCase(String email);

    // For "My Bookings" by phone
    List<Booking> findByPassengerPhone(String phone);

    // For cancelling by PNR
    Optional<Booking> findByPnr(String pnr);

    // ✅ For seat availability: count all NON-CANCELLED bookings in a schedule
    long countByScheduleIdAndStatusNot(Long scheduleId, BookingStatus status);
}
