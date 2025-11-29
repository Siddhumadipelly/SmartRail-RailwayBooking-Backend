package com.smartrail.railway_booking.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trains")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "train_number", nullable = false, unique = true, length = 10)
    private String trainNumber;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String type;

    @Column(nullable = false)
    private boolean active;
}
