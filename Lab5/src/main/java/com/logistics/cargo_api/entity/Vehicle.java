package com.logistics.cargo_api.entity;

import com.logistics.cargo_api.entity.enums.VehicleStatus;
import com.logistics.cargo_api.entity.enums.VehicleType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicleType;

    @Column(nullable = false)
    private double maxWeightKg;

    @Column(name = "max_volume_m3", nullable = false)
    private double maxVolumeM3;

    @Column(nullable = false)
    private double currentMileageKm;

    @Column(name = "fuel_consumption_per_100km", nullable = false)
    private double fuelConsumptionPer100km;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    @Override
    public String toString() {
        return String.format(
                "[ID: %d] %s | %-20s | Вант.: %.0f кг / %.0f м³ | Пробіг: %.0f км | %s",
                id, licensePlate, vehicleType.getDisplayName(),
                maxWeightKg, maxVolumeM3, currentMileageKm, status.getDisplayName());
    }
}