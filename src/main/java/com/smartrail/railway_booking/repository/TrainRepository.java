package com.smartrail.railway_booking.repository;

import com.smartrail.railway_booking.model.Train;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainRepository extends JpaRepository<Train, Long> {

    boolean existsByTrainNumber(String trainNumber);
}
