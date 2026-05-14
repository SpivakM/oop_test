package com.logistics.cargo_api.entity;

import com.logistics.cargo_api.entity.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private double totalAmount;

    @Column(nullable = false)
    private double fuelCost;

    @Column(nullable = false)
    private double amortizationCost;

    @Column(nullable = false)
    private double cargoSurcharge;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    public String getDetailedInfo() {
        return String.format(
                "ІНВОЙС №%d (Замовлення №%d)\nДата: %s\nСтатус: %s\n" +
                        "Пальне: %.2f грн | Амортизація: %.2f грн | Надбавка: %.2f грн\n" +
                        "------------------------------\n%s\n------------------------------\nРАЗОМ ДО ОПЛАТИ: %.2f грн",
                id, order.getId(), issuedAt, status.getDisplayName(),
                fuelCost, amortizationCost, cargoSurcharge,
                details, totalAmount
        );
    }
}
