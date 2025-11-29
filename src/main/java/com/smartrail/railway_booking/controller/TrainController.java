package com.smartrail.railway_booking.controller;

import com.smartrail.railway_booking.model.Station;
import com.smartrail.railway_booking.model.Train;
import com.smartrail.railway_booking.model.TrainSchedule;
import com.smartrail.railway_booking.repository.StationRepository;
import com.smartrail.railway_booking.repository.TrainRepository;
import com.smartrail.railway_booking.repository.TrainScheduleRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/trains")
@CrossOrigin(origins = "*")
public class TrainController {

    private final TrainRepository trainRepository;
    private final TrainScheduleRepository scheduleRepository;
    private final StationRepository stationRepository;

    public TrainController(TrainRepository trainRepository,
                           TrainScheduleRepository scheduleRepository,
                           StationRepository stationRepository) {
        this.trainRepository = trainRepository;
        this.scheduleRepository = scheduleRepository;
        this.stationRepository = stationRepository;
    }

    // 1) Add a new train
    @PostMapping
    public ResponseEntity<?> addTrain(@RequestBody Train train) {
        if (trainRepository.existsByTrainNumber(train.getTrainNumber())) {
            return ResponseEntity.badRequest().body("Train number already exists");
        }
        Train saved = trainRepository.save(train);
        return ResponseEntity.ok(saved);
    }

    // 2) Get all trains
    @GetMapping
    public ResponseEntity<List<Train>> getAllTrains() {
        return ResponseEntity.ok(trainRepository.findAll());
    }

    // 3) Add schedule for a train (JSON body)
    @PostMapping("/{trainId}/schedules")
    public ResponseEntity<?> addSchedule(
            @PathVariable Long trainId,
            @RequestBody AddScheduleRequest request
    ) {
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new RuntimeException("Train not found"));

        Station source = stationRepository.findById(request.getSourceStationId())
                .orElseThrow(() -> new RuntimeException("Source station not found"));

        Station dest = stationRepository.findById(request.getDestinationStationId())
                .orElseThrow(() -> new RuntimeException("Destination station not found"));

        LocalDate travelDate = LocalDate.parse(request.getTravelDate());       // "2025-12-01"
        LocalTime departureTime = LocalTime.parse(request.getDepartureTime()); // "06:00:00"
        LocalTime arrivalTime = LocalTime.parse(request.getArrivalTime());     // "12:30:00"

        // if totalSeats not sent, default to 100
        Integer totalSeats = request.getTotalSeats() != null
                ? request.getTotalSeats()
                : 100;

        // ✅ base fare with default
        Double baseFare = request.getBaseFare() != null
                ? request.getBaseFare()
                : 200.0; // default base fare

        TrainSchedule schedule = TrainSchedule.builder()
                .train(train)
                .sourceStation(source)
                .destinationStation(dest)
                .travelDate(travelDate)
                .departureTime(departureTime)
                .arrivalTime(arrivalTime)
                .totalSeats(totalSeats)
                .baseFare(baseFare)
                .build();

        TrainSchedule saved = scheduleRepository.save(schedule);
        return ResponseEntity.ok(saved);
    }

    // 4) Search trains between two stations on a date
    @GetMapping("/search")
    public ResponseEntity<List<TrainSchedule>> searchTrains(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<TrainSchedule> schedules =
                scheduleRepository.findBySourceStation_CodeAndDestinationStation_CodeAndTravelDate(
                        from.toUpperCase(),
                        to.toUpperCase(),
                        date
                );
        return ResponseEntity.ok(schedules);
    }

    // ---------- DTO class for schedule request ----------
    public static class AddScheduleRequest {
        private Long sourceStationId;
        private Long destinationStationId;
        private String travelDate;     // YYYY-MM-DD
        private String departureTime;  // HH:mm:ss
        private String arrivalTime;    // HH:mm:ss
        private Integer totalSeats;    // total seats overall
        private Double baseFare;       // base fare for this schedule

        public Long getSourceStationId() {
            return sourceStationId;
        }

        public void setSourceStationId(Long sourceStationId) {
            this.sourceStationId = sourceStationId;
        }

        public Long getDestinationStationId() {
            return destinationStationId;
        }

        public void setDestinationStationId(Long destinationStationId) {
            this.destinationStationId = destinationStationId;
        }

        public String getTravelDate() {
            return travelDate;
        }

        public void setTravelDate(String travelDate) {
            this.travelDate = travelDate;
        }

        public String getDepartureTime() {
            return departureTime;
        }

        public void setDepartureTime(String departureTime) {
            this.departureTime = departureTime;
        }

        public String getArrivalTime() {
            return arrivalTime;
        }

        public void setArrivalTime(String arrivalTime) {
            this.arrivalTime = arrivalTime;
        }

        public Integer getTotalSeats() {
            return totalSeats;
        }

        public void setTotalSeats(Integer totalSeats) {
            this.totalSeats = totalSeats;
        }

        public Double getBaseFare() {
            return baseFare;
        }

        public void setBaseFare(Double baseFare) {
            this.baseFare = baseFare;
        }
    }
}
