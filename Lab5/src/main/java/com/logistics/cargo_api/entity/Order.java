package com.logistics.cargo_api.entity;

import com.logistics.cargo_api.entity.enums.OrderStatus;
import com.logistics.cargo_api.state.OrderState;
import com.logistics.cargo_api.state.PendingState;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "order_cargo",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "cargo_id")
    )
    @Builder.Default
    private List<Cargo> cargos = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime departedAt;

    private LocalDateTime deliveredAt;

    public OrderState getOrderState() {
        if (this.status == null) {
            return new PendingState();
        }
        return this.status.getState();
    }

    @Override
    public String toString() {
        return String.format(
                "[ID: %d] Маршрут: %s→%s | Авто: %s | Водій: %s | Вантажів: %d | Статус: %s | Створено: %s",
                id,
                route.getOrigin(), route.getDestination(),
                vehicle.getLicensePlate(),
                driver.getFullName(),
                cargos.size(),
                status.getDisplayName(),
                createdAt.format(FMT)
        );
    }
}