package com.logistics.cargo_api.service;

import com.logistics.cargo_api.entity.Cargo;
import com.logistics.cargo_api.entity.Vehicle;
import com.logistics.cargo_api.entity.enums.CargoType;
import com.logistics.cargo_api.entity.enums.VehicleStatus;
import com.logistics.cargo_api.entity.enums.VehicleType;
import com.logistics.cargo_api.exception.CapacityExceededException;
import com.logistics.cargo_api.exception.EntityNotFoundException;
import com.logistics.cargo_api.exception.IncompatibleVehicleException;
import com.logistics.cargo_api.exception.InvalidStatusTransitionException;
import com.logistics.cargo_api.factory.RefrigeratorTruckFactory;
import com.logistics.cargo_api.factory.TruckFactory;
import com.logistics.cargo_api.factory.VanFactory;
import com.logistics.cargo_api.factory.VehicleFactory;
import com.logistics.cargo_api.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    private final VanFactory vanFactory;
    private final TruckFactory truckFactory;
    private final RefrigeratorTruckFactory refrigeratorFactory;

    @Transactional
    public Vehicle addVehicle(String licensePlate, VehicleType type,
                              double maxWeightKg, double maxVolumeM3,
                              double fuelConsumptionPer100km) {

        if (licensePlate == null || licensePlate.isBlank()) {
            throw new IllegalArgumentException("Номерний знак не може бути порожнім");
        }
        if (maxWeightKg <= 0 || maxVolumeM3 <= 0 || fuelConsumptionPer100km <= 0) {
            throw new IllegalArgumentException("Технічні характеристики повинні бути > 0");
        }

        VehicleFactory factory = getFactoryByType(type);
        Vehicle vehicle = factory.createVehicle(licensePlate, maxWeightKg, maxVolumeM3, fuelConsumptionPer100km);

        return vehicleRepository.save(vehicle);
    }

    @Transactional(readOnly = true)
    public Vehicle findById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Транспортний засіб", id));
    }

    @Transactional(readOnly = true)
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Vehicle> findAvailable() {
        return vehicleRepository.findByStatus(VehicleStatus.AVAILABLE);
    }


    public void validateCompatibility(Vehicle vehicle, List<Cargo> cargos) {
        for (Cargo cargo : cargos) {
            if (cargo.getCargoType() == CargoType.REFRIGERATED
                    && vehicle.getVehicleType() != VehicleType.REFRIGERATOR_TRUCK) {
                throw new IncompatibleVehicleException(
                        String.format("Рефрижераторний вантаж '%s' потребує рефрижераторний транспорт. " +
                                        "Обране авто (%s) не підходить.",
                                cargo.getName(), vehicle.getVehicleType().getDisplayName()));
            }
            if (cargo.getCargoType() == CargoType.DANGEROUS
                    && vehicle.getVehicleType() == VehicleType.VAN) {
                throw new IncompatibleVehicleException(
                        String.format("Небезпечний вантаж '%s' не може перевозитись фургоном. " +
                                        "Потрібна вантажівка або рефрижератор.",
                                cargo.getName()));
            }
        }

        double totalWeight = cargos.stream().mapToDouble(Cargo::getWeightKg).sum();
        double totalVolume = cargos.stream().mapToDouble(Cargo::getVolumeM3).sum();

        if (totalWeight > vehicle.getMaxWeightKg()) {
            throw new CapacityExceededException(
                    String.format("Загальна вага вантажів (%.1f кг) перевищує вантажопідйомність авто (%.1f кг)",
                            totalWeight, vehicle.getMaxWeightKg()));
        }
        if (totalVolume > vehicle.getMaxVolumeM3()) {
            throw new CapacityExceededException(
                    String.format("Загальний об'єм вантажів (%.1f м³) перевищує об'єм кузова (%.1f м³)",
                            totalVolume, vehicle.getMaxVolumeM3()));
        }
    }

    @Transactional
    public Vehicle updateStatus(Long id, VehicleStatus newStatus) {
        Vehicle vehicle = findById(id);
        vehicle.setStatus(newStatus);
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle sendToMaintenance(Long id) {
        Vehicle vehicle = findById(id);
        if (vehicle.getStatus() == VehicleStatus.BUSY) {
            throw new InvalidStatusTransitionException(
                    String.format("Авто %s зараз у рейсі та не може бути на сервісі.",
                            vehicle.getLicensePlate()));
        }
        if (vehicle.getStatus() == VehicleStatus.MAINTENANCE) {
            throw new InvalidStatusTransitionException(
                    String.format("Авто %s вже на сервісі.", vehicle.getLicensePlate()));
        }
        vehicle.setStatus(VehicleStatus.MAINTENANCE);
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle completeMaintenance(Long id) {
        Vehicle vehicle = findById(id);
        if (vehicle.getStatus() != VehicleStatus.MAINTENANCE) {
            throw new InvalidStatusTransitionException(
                    String.format("Авто %s не перебуває на сервісі.", vehicle.getLicensePlate()));
        }
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle addMileage(Long id, double km) {
        Vehicle vehicle = findById(id);
        vehicle.setCurrentMileageKm(vehicle.getCurrentMileageKm() + km);
        return vehicleRepository.save(vehicle);
    }

    private VehicleFactory getFactoryByType(VehicleType type) {
        return switch (type) {
            case VAN -> vanFactory;
            case TRUCK -> truckFactory;
            case REFRIGERATOR_TRUCK -> refrigeratorFactory;
        };
    }
}
