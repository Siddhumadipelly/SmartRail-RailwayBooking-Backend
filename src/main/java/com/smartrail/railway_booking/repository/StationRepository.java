package com.smartrail.railway_booking.repository;

import com.smartrail.railway_booking.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StationRepository extends JpaRepository<Station, Long> {

    boolean existsByCode(String code);

    Optional<Station> findByCode(String code);
}
