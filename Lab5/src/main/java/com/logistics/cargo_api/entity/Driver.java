package com.logistics.cargo_api.entity;

import com.logistics.cargo_api.entity.enums.DriverStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 20)
    private String licenseNumber;

    @Column(nullable = false)
    private int fatigueLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverStatus status;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return String.format("[ID: %d] %-25s | Посвідчення: %s | Втома: %d%% | %s",
                id, getFullName(), licenseNumber, fatigueLevel, status.getDisplayName());
    }
}