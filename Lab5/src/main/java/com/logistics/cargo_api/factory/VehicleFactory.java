package com.logistics.cargo_api.factory;

import com.logistics.cargo_api.entity.Vehicle;

public interface VehicleFactory {
    Vehicle createVehicle(String licensePlate, double maxWeightKg, double maxVolumeM3, double fuelConsumptionPer100km);
}