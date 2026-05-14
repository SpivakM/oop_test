package com.logistics.cargo_api.service;

import com.logistics.cargo_api.decorator.BaseDeliveryCost;
import com.logistics.cargo_api.decorator.DangerousCargoDecorator;
import com.logistics.cargo_api.decorator.DeliveryCost;
import com.logistics.cargo_api.decorator.FragileInsuranceDecorator;
import com.logistics.cargo_api.entity.Cargo;
import com.logistics.cargo_api.entity.Invoice;
import com.logistics.cargo_api.entity.Order;
import com.logistics.cargo_api.entity.enums.CargoType;
import com.logistics.cargo_api.entity.enums.OrderStatus;
import com.logistics.cargo_api.exception.EntityNotFoundException;
import com.logistics.cargo_api.exception.InvalidStatusTransitionException;
import com.logistics.cargo_api.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private static final double FUEL_PRICE_PER_LITRE = 54.0;

    private static final double AMORTIZATION_PER_KM = 8.5;

    private static final double FRAGILE_SURCHARGE     = 0.20;

    private static final double DANGEROUS_SURCHARGE   = 0.50;

    private static final double REFRIGERATED_SURCHARGE = 0.30;

    private final InvoiceRepository invoiceRepository;
    private final OrderService      orderService;

    @Transactional
    public Invoice generateInvoice(Long orderId) {
        Order order = orderService.findById(orderId);

        DeliveryCost costCalculator = new BaseDeliveryCost(order.getRoute().getDistanceKm());

        boolean hasFragile = order.getCargos().stream().anyMatch(c -> c.getCargoType() == CargoType.FRAGILE);
        if (hasFragile) {
            costCalculator = new FragileInsuranceDecorator(costCalculator);
        }

        boolean hasDangerous = order.getCargos().stream().anyMatch(c -> c.getCargoType() == CargoType.DANGEROUS);
        if (hasDangerous) {
            costCalculator = new DangerousCargoDecorator(costCalculator);
        }

        Invoice invoice = Invoice.builder()
                .order(order)
                .totalAmount(costCalculator.getCost())
                .details(costCalculator.getDetails())
                .build();

        return invoiceRepository.save(invoice);
    }

    double calculateCargoSurcharge(Order order, double baseCost) {
        double maxSurchargeRate = 0.0;
        for (Cargo cargo : order.getCargos()) {
            double rate = switch (cargo.getCargoType()) {
                case DANGEROUS    -> DANGEROUS_SURCHARGE;
                case REFRIGERATED -> REFRIGERATED_SURCHARGE;
                case FRAGILE      -> FRAGILE_SURCHARGE;
                case STANDARD     -> 0.0;
            };
            if (rate > maxSurchargeRate) {
                maxSurchargeRate = rate;
            }
        }
        return baseCost * maxSurchargeRate;
    }

    @Transactional(readOnly = true)
    public Optional<Invoice> findByOrderId(Long orderId) {
        return invoiceRepository.findByOrderId(orderId);
    }

    @Transactional(readOnly = true)
    public Invoice findById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Інвойс", id));
    }
}