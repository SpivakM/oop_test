package com.logistics.cargo_api.service;

import com.logistics.cargo_api.entity.Cargo;
import com.logistics.cargo_api.entity.Driver;
import com.logistics.cargo_api.entity.Order;
import com.logistics.cargo_api.entity.Route;
import com.logistics.cargo_api.entity.Vehicle;
import com.logistics.cargo_api.entity.enums.CargoStatus;
import com.logistics.cargo_api.entity.enums.DriverStatus;
import com.logistics.cargo_api.entity.enums.OrderStatus;
import com.logistics.cargo_api.entity.enums.VehicleStatus;
import com.logistics.cargo_api.exception.EntityNotFoundException;
import com.logistics.cargo_api.exception.InvalidStatusTransitionException;
import com.logistics.cargo_api.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RouteService routeService;
    private final VehicleService vehicleService;
    private final DriverService driverService;
    private final CargoService cargoService;

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Замовлення", id));
    }

    public Order getOrderById(Long id) {
        return findById(id);
    }

    @Transactional
    public Order createOrder(Long routeId, Long vehicleId, Long driverId, List<Long> cargoIds) {
        if (cargoIds == null || cargoIds.isEmpty()) {
            throw new IllegalArgumentException("Замовлення має містити хоча б один вантаж");
        }

        Route route = routeService.findById(routeId);
        Vehicle vehicle = vehicleService.findById(vehicleId);
        Driver driver = driverService.findById(driverId);

        if (vehicle.getStatus() != VehicleStatus.AVAILABLE) {
            throw new InvalidStatusTransitionException(
                    String.format("Транспортний засіб %s не доступний (статус: %s)",
                            vehicle.getLicensePlate(), vehicle.getStatus().getDisplayName()));
        }

        driverService.validateDispatchable(driver);

        List<Cargo> cargos = new ArrayList<>();
        for (Long cargoId : cargoIds) {
            Cargo cargo = cargoService.findById(cargoId);
            if (cargo.getStatus() != CargoStatus.ON_WAREHOUSE) {
                throw new InvalidStatusTransitionException(
                        String.format("Вантаж '%s' має статус %s і не може бути доданий до замовлення",
                                cargo.getName(), cargo.getStatus().getDisplayName()));
            }
            cargos.add(cargo);
        }

        vehicleService.validateCompatibility(vehicle, cargos);

        Order order = Order.builder()
                .route(route)
                .vehicle(vehicle)
                .driver(driver)
                .cargos(cargos)
                .status(OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return orderRepository.save(order);
    }

    @Transactional
    public Order dispatchOrder(Long id) {
        Order order = findById(id);
        return dispatchOrder(order);
    }

    @Transactional
    public Order confirmDelivery(Long id) {
        Order order = findById(id);
        return confirmDelivery(order);
    }

    @Transactional
    public Order advanceOrder(Long id) {
        Order order = findById(id);
        if (order.getStatus() == OrderStatus.PENDING) {
            return dispatchOrder(order);
        }
        if (order.getStatus() == OrderStatus.IN_TRANSIT) {
            return confirmDelivery(order);
        }

        order.getOrderState().next(order);
        return orderRepository.save(order);
    }

    @Transactional
    public Order cancelOrder(Long id) {
        Order order = findById(id);

        order.getOrderState().cancel(order);

        List<Cargo> updated = new ArrayList<>();
        for (Cargo cargo : order.getCargos()) {
            updated.add(cargoService.updateStatus(cargo.getId(), CargoStatus.ON_WAREHOUSE));
        }
        order.setCargos(updated);

        return orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("Замовлення", id);
        }
        orderRepository.deleteById(id);
    }

    @Transactional
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    private Order dispatchOrder(Order order) {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidStatusTransitionException(
                    order.getStatus().getDisplayName(), OrderStatus.IN_TRANSIT.getDisplayName());
        }

        order.getOrderState().next(order);

        Vehicle updatedVehicle = vehicleService.updateStatus(order.getVehicle().getId(), VehicleStatus.BUSY);
        Driver updatedDriver = driverService.updateStatus(order.getDriver().getId(), DriverStatus.BUSY);
        List<Cargo> updatedCargos = new ArrayList<>();
        for (Cargo cargo : order.getCargos()) {
            updatedCargos.add(cargoService.updateStatus(cargo.getId(), CargoStatus.IN_TRANSIT));
        }

        order.setVehicle(updatedVehicle);
        order.setDriver(updatedDriver);
        order.setCargos(updatedCargos);

        return orderRepository.save(order);
    }

    private Order confirmDelivery(Order order) {
        if (order.getStatus() != OrderStatus.IN_TRANSIT) {
            throw new InvalidStatusTransitionException(
                    order.getStatus().getDisplayName(), OrderStatus.DELIVERED.getDisplayName());
        }

        order.getOrderState().next(order);

        double distanceKm = order.getRoute().getDistanceKm();
        Vehicle updatedVehicle = vehicleService.addMileage(order.getVehicle().getId(), distanceKm);
        updatedVehicle = vehicleService.updateStatus(updatedVehicle.getId(), VehicleStatus.AVAILABLE);
        Driver updatedDriver = driverService.increaseFatigue(order.getDriver().getId(), calculateFatigue(distanceKm));
        updatedDriver = driverService.updateStatus(updatedDriver.getId(), DriverStatus.AVAILABLE);

        List<Cargo> updatedCargos = new ArrayList<>();
        for (Cargo cargo : order.getCargos()) {
            updatedCargos.add(cargoService.updateStatus(cargo.getId(), CargoStatus.DELIVERED));
        }

        order.setVehicle(updatedVehicle);
        order.setDriver(updatedDriver);
        order.setCargos(updatedCargos);

        return orderRepository.save(order);
    }

    private int calculateFatigue(double distanceKm) {
        return Math.min(100, (int) Math.round(distanceKm / 20.0));
    }
}
