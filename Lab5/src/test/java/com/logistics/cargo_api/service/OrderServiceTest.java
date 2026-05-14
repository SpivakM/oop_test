package com.logistics.cargo_api.service;

import com.logistics.cargo_api.entity.*;
import com.logistics.cargo_api.entity.enums.*;
import com.logistics.cargo_api.exception.InvalidStatusTransitionException;
import com.logistics.cargo_api.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Tests")
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private RouteService    routeService;
    @Mock private VehicleService  vehicleService;
    @Mock private DriverService   driverService;
    @Mock private CargoService    cargoService;

    @InjectMocks
    private OrderService orderService;


    private Route   route;
    private Vehicle vehicle;
    private Driver  driver;
    private Cargo   cargo1;
    private Order   pendingOrder;

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

        cargo1 = Cargo.builder()
                .id(1L).name("Побутова техніка")
                .cargoType(CargoType.STANDARD).status(CargoStatus.ON_WAREHOUSE)
                .weightKg(800).volumeM3(5.5)
                .build();

        pendingOrder = Order.builder()
                .id(1L).route(route).vehicle(vehicle).driver(driver)
                .cargos(new ArrayList<>(List.of(cargo1)))
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }


    @Test
    @DisplayName("✅ createOrder — позитивний: замовлення зі сумісним вантажем")
    void createOrder_validParams_returnsPendingOrder() {
        when(routeService.findById(1L)).thenReturn(route);
        when(vehicleService.findById(1L)).thenReturn(vehicle);
        when(driverService.findById(1L)).thenReturn(driver);
        when(cargoService.findById(1L)).thenReturn(cargo1);
        doNothing().when(driverService).validateDispatchable(driver);
        doNothing().when(vehicleService).validateCompatibility(eq(vehicle), any());
        when(orderRepository.save(any())).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o = Order.builder()
                    .id(100L).route(o.getRoute()).vehicle(o.getVehicle())
                    .driver(o.getDriver()).cargos(o.getCargos())
                    .status(OrderStatus.PENDING).createdAt(LocalDateTime.now())
                    .build();
            return o;
        });

        Order result = orderService.createOrder(1L, 1L, 1L, List.of(1L));

        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(result.getRoute().getOrigin()).isEqualTo("Львів");
        assertThat(result.getCargos()).hasSize(1);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("❌ createOrder — негативний: порожній список вантажів")
    void createOrder_emptyCargoList_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> orderService.createOrder(1L, 1L, 1L, List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("хоча б один вантаж");
    }

    @Test
    @DisplayName("❌ createOrder — негативний: null список вантажів")
    void createOrder_nullCargoList_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> orderService.createOrder(1L, 1L, 1L, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("❌ createOrder — негативний: авто не доступне")
    void createOrder_vehicleNotAvailable_throwsException() {
        vehicle.setStatus(VehicleStatus.BUSY);
        when(routeService.findById(1L)).thenReturn(route);
        when(vehicleService.findById(1L)).thenReturn(vehicle);

        assertThatThrownBy(() -> orderService.createOrder(1L, 1L, 1L, List.of(1L)))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("не доступний");
    }

    @Test
    @DisplayName("❌ createOrder — негативний: вантаж не на складі")
    void createOrder_cargoNotOnWarehouse_throwsException() {
        cargo1.setStatus(CargoStatus.IN_TRANSIT);
        when(routeService.findById(1L)).thenReturn(route);
        when(vehicleService.findById(1L)).thenReturn(vehicle);
        when(driverService.findById(1L)).thenReturn(driver);
        doNothing().when(driverService).validateDispatchable(driver);
        when(cargoService.findById(1L)).thenReturn(cargo1);

        assertThatThrownBy(() -> orderService.createOrder(1L, 1L, 1L, List.of(1L)))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("В дорозі");
    }


    @Test
    @DisplayName("✅ dispatchOrder — позитивний: PENDING → IN_TRANSIT")
    void dispatchOrder_pendingOrder_statusChangesToInTransit() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(pendingOrder));
        when(vehicleService.updateStatus(any(), any())).thenReturn(vehicle);
        when(driverService.updateStatus(any(), any())).thenReturn(driver);
        when(cargoService.updateStatus(any(), any())).thenReturn(cargo1);
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.dispatchOrder(1L);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.IN_TRANSIT);
        assertThat(result.getDepartedAt()).isNotNull();
        verify(vehicleService).updateStatus(1L, VehicleStatus.BUSY);
        verify(driverService).updateStatus(1L, DriverStatus.BUSY);
        verify(cargoService).updateStatus(1L, CargoStatus.IN_TRANSIT);
    }

    @Test
    @DisplayName("❌ dispatchOrder — негативний: вже IN_TRANSIT → виняток")
    void dispatchOrder_alreadyInTransit_throwsInvalidStatusTransitionException() {
        pendingOrder.setStatus(OrderStatus.IN_TRANSIT);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(pendingOrder));

        assertThatThrownBy(() -> orderService.dispatchOrder(1L))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }

    @Test
    @DisplayName("❌ dispatchOrder — негативний: скасоване замовлення")
    void dispatchOrder_cancelledOrder_throwsException() {
        pendingOrder.setStatus(OrderStatus.CANCELLED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(pendingOrder));

        assertThatThrownBy(() -> orderService.dispatchOrder(1L))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }


    @Test
    @DisplayName("✅ confirmDelivery — позитивний: IN_TRANSIT → DELIVERED")
    void confirmDelivery_inTransitOrder_statusChangesToDelivered() {
        pendingOrder.setStatus(OrderStatus.IN_TRANSIT);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(pendingOrder));
        when(vehicleService.addMileage(any(), anyDouble())).thenReturn(vehicle);
        when(vehicleService.updateStatus(any(), any())).thenReturn(vehicle);
        when(driverService.increaseFatigue(any(), anyInt())).thenReturn(driver);
        when(driverService.updateStatus(any(), any())).thenReturn(driver);
        when(cargoService.updateStatus(any(), any())).thenReturn(cargo1);
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.confirmDelivery(1L);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        assertThat(result.getDeliveredAt()).isNotNull();
        verify(vehicleService).addMileage(eq(1L), eq(540.0));
        verify(vehicleService).updateStatus(1L, VehicleStatus.AVAILABLE);
        verify(driverService).updateStatus(1L, DriverStatus.AVAILABLE);
        verify(cargoService).updateStatus(1L, CargoStatus.DELIVERED);
    }

    @Test
    @DisplayName("❌ confirmDelivery — негативний: замовлення ще PENDING → виняток")
    void confirmDelivery_pendingOrder_throwsException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(pendingOrder));

        assertThatThrownBy(() -> orderService.confirmDelivery(1L))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }


    @Test
    @DisplayName("✅ cancelOrder — позитивний: PENDING → CANCELLED")
    void cancelOrder_pendingOrder_statusChangesToCancelled() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(pendingOrder));
        when(cargoService.updateStatus(any(), any())).thenReturn(cargo1);
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Order result = orderService.cancelOrder(1L);

        assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(cargoService).updateStatus(1L, CargoStatus.ON_WAREHOUSE);
    }

    @Test
    @DisplayName("❌ cancelOrder — негативний: IN_TRANSIT не можна скасувати")
    void cancelOrder_inTransitOrder_throwsInvalidStatusTransitionException() {
        pendingOrder.setStatus(OrderStatus.IN_TRANSIT);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(pendingOrder));

        assertThatThrownBy(() -> orderService.cancelOrder(1L))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining(OrderStatus.PENDING.getDisplayName());
    }

    @Test
    @DisplayName("❌ cancelOrder — негативний: вже DELIVERED → виняток")
    void cancelOrder_deliveredOrder_throwsException() {
        pendingOrder.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(pendingOrder));

        assertThatThrownBy(() -> orderService.cancelOrder(1L))
                .isInstanceOf(InvalidStatusTransitionException.class);
    }
}