package com.smartrail.railway_booking.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "passengers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    // IMPORTANT CHANGE: use Integer instead of primitive int
    // so Hibernate/Jackson can handle nulls safely
    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false, length = 1)
    private String gender;   // "M", "F", "O" etc.

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(nullable = false, length = 100)
    private String email;
}
