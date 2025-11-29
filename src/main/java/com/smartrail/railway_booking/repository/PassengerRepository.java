package com.smartrail.railway_booking.repository;

import com.smartrail.railway_booking.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
}
