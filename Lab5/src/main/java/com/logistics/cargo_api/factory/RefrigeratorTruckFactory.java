package com.logistics.cargo_api.factory;

import com.logistics.cargo_api.entity.Vehicle;
import com.logistics.cargo_api.entity.enums.VehicleStatus;
import com.logistics.cargo_api.entity.enums.VehicleType;
import org.springframework.stereotype.Component;

@Component
public class RefrigeratorTruckFactory implements VehicleFactory {
    @Override
    public Vehicle createVehicle(String licensePlate, double maxWeightKg, double maxVolumeM3, double fuelConsumptionPer100km) {
        return Vehicle.builder()
                .licensePlate(licensePlate)
                .vehicleType(VehicleType.REFRIGERATOR_TRUCK)
                .maxWeightKg(maxWeightKg)
                .maxVolumeM3(maxVolumeM3)
                .currentMileageKm(0)
                .fuelConsumptionPer100km(fuelConsumptionPer100km)
                .status(VehicleStatus.AVAILABLE)
                .build();
    }
}