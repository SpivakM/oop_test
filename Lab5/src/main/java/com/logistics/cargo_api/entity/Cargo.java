package com.logistics.cargo_api.entity;

import com.logistics.cargo_api.entity.enums.CargoStatus;
import com.logistics.cargo_api.entity.enums.CargoType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cargo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private double weightKg;

    @Column(name = "volume_m3", nullable = false)
    private double volumeM3;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CargoType cargoType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CargoStatus status;

    @Override
    public String toString() {
        return String.format("[ID: %d] %-35s | Тип: %-18s | %.1f кг / %.1f м³ | %s",
                id, name, cargoType.getDisplayName(),
                weightKg, volumeM3, status.getDisplayName());
    }
}