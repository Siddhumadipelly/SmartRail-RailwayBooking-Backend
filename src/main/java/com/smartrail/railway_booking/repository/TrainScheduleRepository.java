package com.smartrail.railway_booking.repository;

import com.smartrail.railway_booking.model.TrainSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TrainScheduleRepository extends JpaRepository<TrainSchedule, Long> {

    // 🔍 Find trains based on source, destination, date
    List<TrainSchedule> findBySourceStation_CodeAndDestinationStation_CodeAndTravelDate(
            String sourceCode,
            String destinationCode,
            LocalDate travelDate
    );

    // 🧮 Count how many bookings exist for a schedule (to calculate available seats)
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.schedule.id = :scheduleId AND b.status <> 'CANCELLED'")
    int countBookingsByScheduleId(Long scheduleId);
}
