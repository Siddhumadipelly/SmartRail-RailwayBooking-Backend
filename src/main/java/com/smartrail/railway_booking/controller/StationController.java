package com.smartrail.railway_booking.controller;

import com.smartrail.railway_booking.model.Station;
import com.smartrail.railway_booking.repository.StationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/stations")
@CrossOrigin(origins = "*")
public class StationController {

    private final StationRepository stationRepository;

    public StationController(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    // 1) Add a new station
    @PostMapping
    public ResponseEntity<?> addStation(@RequestBody Station station) {
        if (stationRepository.existsByCode(station.getCode())) {
            return ResponseEntity.badRequest().body("Station code already exists");
        }
        Station saved = stationRepository.save(station);
        return ResponseEntity.ok(saved);
    }

    // 2) Get all stations
    @GetMapping
    public ResponseEntity<List<Station>> getAllStations() {
        List<Station> stations = stationRepository.findAll();
        return ResponseEntity.ok(stations);
    }

    // 3) Get station by code
    @GetMapping("/{code}")
    public ResponseEntity<?> getByCode(@PathVariable String code) {
        String upperCode = code.toUpperCase();
        Optional<Station> optionalStation = stationRepository.findByCode(upperCode);

        if (optionalStation.isPresent()) {
            return ResponseEntity.ok(optionalStation.get());   // 200 + Station JSON
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)              // 404
                    .body("Station not found");
        }
    }
}
