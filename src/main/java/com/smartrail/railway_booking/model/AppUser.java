package com.smartrail.railway_booking.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    // NOTE: For a real app you must hash the password.
    // Here we store plain text only for learning/demo.
    @Column(nullable = false, length = 120)
    private String password;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
