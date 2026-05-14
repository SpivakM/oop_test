package com.logistics.cargo_api.repository;

import com.logistics.cargo_api.entity.Order;
import com.logistics.cargo_api.entity.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByVehicleId(Long vehicleId);
    List<Order> findByDriverId(Long driverId);
}