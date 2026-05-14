package com.logistics.cargo_api.service;

import com.logistics.cargo_api.decorator.BaseDeliveryCost;
import com.logistics.cargo_api.decorator.DangerousCargoDecorator;
import com.logistics.cargo_api.decorator.DeliveryCost;
import com.logistics.cargo_api.decorator.FragileInsuranceDecorator;
import com.logistics.cargo_api.decorator.RefrigeratedCargoDecorator;
import com.logistics.cargo_api.entity.Cargo;
import com.logistics.cargo_api.entity.Invoice;
import com.logistics.cargo_api.entity.Order;
import com.logistics.cargo_api.entity.enums.CargoType;
import com.logistics.cargo_api.entity.enums.InvoiceStatus;
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
        if (invoiceRepository.existsByOrderId(orderId)) {
            return invoiceRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new EntityNotFoundException("Інвойс", orderId));
        }

        Order order = orderService.findById(orderId);

        validateOrderStatus(order);

        double distanceKm = order.getRoute().getDistanceKm();
        double fuelCost = distanceKm
                * (order.getVehicle().getFuelConsumptionPer100km() / 100.0)
                * FUEL_PRICE_PER_LITRE;
        double amortizationCost = distanceKm * AMORTIZATION_PER_KM;
        double baseCost = fuelCost + amortizationCost;

        DeliveryCost costCalculator = new BaseDeliveryCost(distanceKm, fuelCost, amortizationCost);

        CargoType surchargeType = getHighestSurchargeType(order);
        if (surchargeType == CargoType.DANGEROUS) {
            costCalculator = new DangerousCargoDecorator(costCalculator);
        } else if (surchargeType == CargoType.REFRIGERATED) {
            costCalculator = new RefrigeratedCargoDecorator(costCalculator);
        } else if (surchargeType == CargoType.FRAGILE) {
            costCalculator = new FragileInsuranceDecorator(costCalculator);
        }

        double totalAmount = costCalculator.getCost();
        double cargoSurcharge = totalAmount - baseCost;

        Invoice invoice = Invoice.builder()
                .order(order)
                .fuelCost(fuelCost)
                .amortizationCost(amortizationCost)
                .cargoSurcharge(cargoSurcharge)
                .totalAmount(totalAmount)
                .details(costCalculator.getDetails())
                .issuedAt(LocalDateTime.now())
                .status(InvoiceStatus.ISSUED)
                .build();

        return invoiceRepository.save(invoice);
    }

    double calculateCargoSurcharge(Order order, double baseCost) {
        if (order.getCargos() == null || order.getCargos().isEmpty()) {
            return 0.0;
        }
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

    private void validateOrderStatus(Order order) {
        if (order.getStatus() == OrderStatus.PENDING) {
            throw new InvalidStatusTransitionException(
                    "Неможливо виставити інвойс для замовлення, яке ще не в дорозі.");
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidStatusTransitionException(
                    "Неможливо виставити інвойс для скасованого замовлення.");
        }
    }

    private CargoType getHighestSurchargeType(Order order) {
        CargoType result = null;
        double maxRate = 0.0;
        if (order.getCargos() == null) {
            return null;
        }
        for (Cargo cargo : order.getCargos()) {
            double rate = switch (cargo.getCargoType()) {
                case DANGEROUS    -> DANGEROUS_SURCHARGE;
                case REFRIGERATED -> REFRIGERATED_SURCHARGE;
                case FRAGILE      -> FRAGILE_SURCHARGE;
                case STANDARD     -> 0.0;
            };
            if (rate > maxRate) {
                maxRate = rate;
                result = cargo.getCargoType();
            }
        }
        return result;
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
