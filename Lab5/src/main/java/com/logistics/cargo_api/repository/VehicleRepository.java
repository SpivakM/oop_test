package com.logistics.cargo_api.repository;

import com.logistics.cargo_api.entity.Vehicle;
import com.logistics.cargo_api.entity.enums.VehicleStatus;
import com.logistics.cargo_api.entity.enums.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByStatus(VehicleStatus status);
    List<Vehicle> findByVehicleTypeAndStatus(VehicleType type, VehicleStatus status);
    Optional<Vehicle> findByLicensePlate(String licensePlate);
}