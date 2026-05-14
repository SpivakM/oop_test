package com.logistics.cargo_api.factory;

import com.logistics.cargo_api.entity.Vehicle;
import com.logistics.cargo_api.entity.enums.VehicleStatus;
import com.logistics.cargo_api.entity.enums.VehicleType;
import org.springframework.stereotype.Component;

@Component
public class VanFactory implements VehicleFactory {

    @Override
    public Vehicle createVehicle(String licensePlate, double maxWeightKg, double maxVolumeM3, double fuelConsumptionPer100km) {

        if (maxWeightKg > 3500) {
            throw new IllegalArgumentException("Вантажопідйомність фургона (VAN) не може перевищувати 3500 кг");
        }

        return Vehicle.builder()
                .licensePlate(licensePlate)
                .vehicleType(VehicleType.VAN)
                .maxWeightKg(maxWeightKg)
                .maxVolumeM3(maxVolumeM3)
                .currentMileageKm(0)
                .fuelConsumptionPer100km(fuelConsumptionPer100km)
                .status(VehicleStatus.AVAILABLE)
                .build();
    }
}