package com.logistics.cargo_api.service;

import com.logistics.cargo_api.entity.*;
import com.logistics.cargo_api.entity.enums.*;
import com.logistics.cargo_api.exception.InvalidStatusTransitionException;
import com.logistics.cargo_api.repository.InvoiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InvoiceService Tests")
class InvoiceServiceTest {

    @Mock private InvoiceRepository invoiceRepository;
    @Mock private OrderService      orderService;

    @InjectMocks
    private InvoiceService invoiceService;

    private Route   route;
    private Vehicle vehicle;
    private Driver  driver;

    @BeforeEach
    void setUp() {
        route = Route.builder()
                .id(1L).origin("Львів").destination("Київ").distanceKm(540).build();

        vehicle = Vehicle.builder()
                .id(1L).licensePlate("АА1234ВВ")
                .vehicleType(VehicleType.TRUCK)
                .maxWeightKg(10000).maxVolumeM3(60)
                .currentMileageKm(45000).fuelConsumptionPer100km(28.5)
                .status(VehicleStatus.AVAILABLE)
                .build();

        driver = Driver.builder()
                .id(1L).firstName("Іван").lastName("Петренко")
                .licenseNumber("АВ123456").fatigueLevel(20)
                .status(DriverStatus.AVAILABLE)
                .build();

        lenient().when(invoiceRepository.findByOrderId(1L)).thenReturn(Optional.empty());
    }

    private Order buildOrder(OrderStatus status, CargoType... cargoTypes) {
        List<Cargo> cargos = java.util.Arrays.stream(cargoTypes)
                .map(type -> Cargo.builder()
                        .id(1L).name("Вантаж").cargoType(type)
                        .weightKg(100).volumeM3(2.0)
                        .status(CargoStatus.IN_TRANSIT)
                        .build())
                .toList();
        return Order.builder()
                .id(1L).route(route).vehicle(vehicle).driver(driver)
                .cargos(new java.util.ArrayList<>(cargos))
                .status(status).createdAt(LocalDateTime.now())
                .build();
    }


    @Test
    @DisplayName("✅ generateInvoice — позитивний: стандартний вантаж без надбавки")
    void generateInvoice_standardCargo_noSurcharge() {
        Order order = buildOrder(OrderStatus.IN_TRANSIT, CargoType.STANDARD);
        when(orderService.findById(1L)).thenReturn(order);
        when(invoiceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Invoice invoice = invoiceService.generateInvoice(1L);

        double expectedFuel = 540 * (28.5 / 100.0) * 54.0;
        double expectedAmort = 540 * 8.5;

        assertThat(invoice.getFuelCost()).isCloseTo(expectedFuel, within(0.01));
        assertThat(invoice.getAmortizationCost()).isCloseTo(expectedAmort, within(0.01));
        assertThat(invoice.getCargoSurcharge()).isEqualTo(0.0);
        assertThat(invoice.getTotalAmount()).isCloseTo(expectedFuel + expectedAmort, within(0.01));
    }

    @Test
    @DisplayName("✅ generateInvoice — позитивний: небезпечний вантаж → 50% надбавка")
    void generateInvoice_dangerousCargo_hasFiftySurcharge() {
        Order order = buildOrder(OrderStatus.IN_TRANSIT, CargoType.DANGEROUS);
        when(orderService.findById(1L)).thenReturn(order);
        when(invoiceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Invoice invoice = invoiceService.generateInvoice(1L);

        double baseCost = invoice.getFuelCost() + invoice.getAmortizationCost();
        assertThat(invoice.getCargoSurcharge()).isCloseTo(baseCost * 0.50, within(0.01));
        assertThat(invoice.getTotalAmount()).isCloseTo(baseCost * 1.50, within(0.01));
    }

    @Test
    @DisplayName("✅ generateInvoice — позитивний: крихкий вантаж → 20% надбавка")
    void generateInvoice_fragileCargo_hasTwentySurcharge() {
        Order order = buildOrder(OrderStatus.IN_TRANSIT, CargoType.FRAGILE);
        when(orderService.findById(1L)).thenReturn(order);
        when(invoiceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Invoice invoice = invoiceService.generateInvoice(1L);

        double baseCost = invoice.getFuelCost() + invoice.getAmortizationCost();
        assertThat(invoice.getCargoSurcharge()).isCloseTo(baseCost * 0.20, within(0.01));
    }

    @Test
    @DisplayName("✅ generateInvoice — позитивний: рефрижераторний вантаж → 30% надбавка")
    void generateInvoice_refrigeratedCargo_hasThirtySurcharge() {
        Order order = buildOrder(OrderStatus.IN_TRANSIT, CargoType.REFRIGERATED);
        when(orderService.findById(1L)).thenReturn(order);
        when(invoiceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Invoice invoice = invoiceService.generateInvoice(1L);

        double baseCost = invoice.getFuelCost() + invoice.getAmortizationCost();
        assertThat(invoice.getCargoSurcharge()).isCloseTo(baseCost * 0.30, within(0.01));
    }

    @Test
    @DisplayName("✅ generateInvoice — позитивний: збірне перевезення → застосовується максимальна надбавка")
    void generateInvoice_mixedCargo_appliesHighestSurcharge() {
        Order order = buildOrder(OrderStatus.IN_TRANSIT, CargoType.FRAGILE, CargoType.DANGEROUS);
        when(orderService.findById(1L)).thenReturn(order);
        when(invoiceRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Invoice invoice = invoiceService.generateInvoice(1L);

        double baseCost = invoice.getFuelCost() + invoice.getAmortizationCost();
        assertThat(invoice.getCargoSurcharge()).isCloseTo(baseCost * 0.50, within(0.01));
    }

    @Test
    @DisplayName("✅ generateInvoice — ідемпотентний: повторний виклик повертає той самий інвойс")
    void generateInvoice_alreadyExists_returnsExistingInvoice() {
        Invoice existing = Invoice.builder().id(10L).build();
        when(invoiceRepository.findByOrderId(1L)).thenReturn(Optional.of(existing));

        Invoice result = invoiceService.generateInvoice(1L);

        assertThat(result.getId()).isEqualTo(10L);
        verify(orderService, never()).findById(any());
        verify(invoiceRepository, never()).save(any());
    }

    @Test
    @DisplayName("❌ generateInvoice — негативний: PENDING замовлення → виняток")
    void generateInvoice_pendingOrder_throwsInvalidStatusTransitionException() {
        Order order = buildOrder(OrderStatus.PENDING, CargoType.STANDARD);
        when(orderService.findById(1L)).thenReturn(order);

        assertThatThrownBy(() -> invoiceService.generateInvoice(1L))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("не в дорозі");
    }

    @Test
    @DisplayName("❌ generateInvoice — негативний: скасоване замовлення → виняток")
    void generateInvoice_cancelledOrder_throwsInvalidStatusTransitionException() {
        Order order = buildOrder(OrderStatus.CANCELLED, CargoType.STANDARD);
        when(orderService.findById(1L)).thenReturn(order);

        assertThatThrownBy(() -> invoiceService.generateInvoice(1L))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("скасованого");
    }


    @Test
    @DisplayName("✅ calculateCargoSurcharge — стандартний вантаж: 0%")
    void calculateCargoSurcharge_standardCargo_returnsZero() {
        Order order = buildOrder(OrderStatus.IN_TRANSIT, CargoType.STANDARD);
        double surcharge = invoiceService.calculateCargoSurcharge(order, 10000.0);
        assertThat(surcharge).isEqualTo(0.0);
    }

    @Test
    @DisplayName("✅ calculateCargoSurcharge — порожній список вантажів: 0")
    void calculateCargoSurcharge_emptyCargo_returnsZero() {
        Order order = buildOrder(OrderStatus.IN_TRANSIT);
        double surcharge = invoiceService.calculateCargoSurcharge(order, 5000.0);
        assertThat(surcharge).isEqualTo(0.0);
    }
}
