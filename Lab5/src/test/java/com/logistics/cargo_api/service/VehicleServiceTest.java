package com.logistics.cargo_api.service;

import com.logistics.cargo_api.entity.Cargo;
import com.logistics.cargo_api.entity.Vehicle;
import com.logistics.cargo_api.entity.enums.CargoStatus;
import com.logistics.cargo_api.entity.enums.CargoType;
import com.logistics.cargo_api.entity.enums.VehicleStatus;
import com.logistics.cargo_api.entity.enums.VehicleType;
import com.logistics.cargo_api.exception.CapacityExceededException;
import com.logistics.cargo_api.exception.EntityNotFoundException;
import com.logistics.cargo_api.exception.IncompatibleVehicleException;
import com.logistics.cargo_api.factory.RefrigeratorTruckFactory;
import com.logistics.cargo_api.factory.TruckFactory;
import com.logistics.cargo_api.factory.VanFactory;
import com.logistics.cargo_api.repository.VehicleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleService Tests")
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private VanFactory vanFactory;

    @Mock
    private TruckFactory truckFactory;

    @Mock
    private RefrigeratorTruckFactory refrigeratorFactory;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle buildVehicle(VehicleType type, double maxWeight, double maxVolume) {
        return Vehicle.builder()
                .id(1L).licensePlate("АА0001ВВ")
                .vehicleType(type)
                .maxWeightKg(maxWeight).maxVolumeM3(maxVolume)
                .currentMileageKm(0).fuelConsumptionPer100km(25.0)
                .status(VehicleStatus.AVAILABLE)
                .build();
    }

    private Cargo buildCargo(CargoType type, double weight, double volume) {
        return Cargo.builder()
                .id(1L).name("Тестовий вантаж")
                .cargoType(type).status(CargoStatus.ON_WAREHOUSE)
                .weightKg(weight).volumeM3(volume)
                .build();
    }


    @Test
    @DisplayName("✅ addVehicle — позитивний: фургон успішно збережений")
    void addVehicle_validParams_returnsSavedVehicle() {
        Vehicle saved = buildVehicle(VehicleType.VAN, 1500, 10);
        when(vanFactory.createVehicle(anyString(), anyDouble(), anyDouble(), anyDouble())).thenReturn(saved);
        when(vehicleRepository.save(any())).thenReturn(saved);

        Vehicle result = vehicleService.addVehicle("АА0001ВВ", VehicleType.VAN, 1500, 10, 12.0);

        assertThat(result).isNotNull();
        assertThat(result.getVehicleType()).isEqualTo(VehicleType.VAN);
        assertThat(result.getStatus()).isEqualTo(VehicleStatus.AVAILABLE);
    }

    @Test
    @DisplayName("❌ addVehicle — негативний: порожній номерний знак")
    void addVehicle_emptyPlate_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> vehicleService.addVehicle("", VehicleType.TRUCK, 10000, 50, 28.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Номерний знак");
        verifyNoInteractions(vehicleRepository);
    }

    @Test
    @DisplayName("❌ addVehicle — негативний: нульова вантажопідйомність")
    void addVehicle_zeroMaxWeight_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> vehicleService.addVehicle("АА0001ВВ", VehicleType.TRUCK, 0, 50, 28.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Технічні характеристики");
    }


    @Test
    @DisplayName("✅ findById — транспортний засіб знайдено")
    void findById_existingId_returnsVehicle() {
        Vehicle v = buildVehicle(VehicleType.TRUCK, 10000, 60);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(v));

        Vehicle result = vehicleService.findById(1L);

        assertThat(result.getVehicleType()).isEqualTo(VehicleType.TRUCK);
    }

    @Test
    @DisplayName("❌ findById — негативний: не знайдено → EntityNotFoundException")
    void findById_notFound_throwsEntityNotFoundException() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("99");
    }


    @Test
    @DisplayName("✅ validateCompatibility — стандартний вантаж у вантажівці: OK")
    void validateCompatibility_standardCargoInTruck_noException() {
        Vehicle truck = buildVehicle(VehicleType.TRUCK, 10000, 60);
        Cargo cargo   = buildCargo(CargoType.STANDARD, 500, 5);

        assertThatNoException().isThrownBy(
                () -> vehicleService.validateCompatibility(truck, List.of(cargo)));
    }

    @Test
    @DisplayName("✅ validateCompatibility — рефрижераторний вантаж у рефрижераторі: OK")
    void validateCompatibility_refrigeratedCargoInRefrigeratorTruck_noException() {
        Vehicle refTruck = buildVehicle(VehicleType.REFRIGERATOR_TRUCK, 8000, 45);
        Cargo cargo      = buildCargo(CargoType.REFRIGERATED, 1000, 8);

        assertThatNoException().isThrownBy(
                () -> vehicleService.validateCompatibility(refTruck, List.of(cargo)));
    }

    @Test
    @DisplayName("❌ validateCompatibility — рефрижераторний вантаж у звичайній вантажівці")
    void validateCompatibility_refrigeratedCargoInTruck_throwsIncompatibleVehicleException() {
        Vehicle truck = buildVehicle(VehicleType.TRUCK, 10000, 60);
        Cargo cargo   = buildCargo(CargoType.REFRIGERATED, 500, 5);

        assertThatThrownBy(() -> vehicleService.validateCompatibility(truck, List.of(cargo)))
                .isInstanceOf(IncompatibleVehicleException.class)
                .hasMessageContaining("рефрижераторний");
    }

    @Test
    @DisplayName("❌ validateCompatibility — небезпечний вантаж у фургоні")
    void validateCompatibility_dangerousCargoInVan_throwsIncompatibleVehicleException() {
        Vehicle van = buildVehicle(VehicleType.VAN, 1500, 12);
        Cargo cargo = buildCargo(CargoType.DANGEROUS, 200, 2);

        assertThatThrownBy(() -> vehicleService.validateCompatibility(van, List.of(cargo)))
                .isInstanceOf(IncompatibleVehicleException.class)
                .hasMessageContaining("Небезпечний");
    }

    @Test
    @DisplayName("❌ validateCompatibility — перевищення вантажопідйомності")
    void validateCompatibility_weightExceeded_throwsCapacityExceededException() {
        Vehicle truck = buildVehicle(VehicleType.TRUCK, 1000, 60);
        Cargo cargo   = buildCargo(CargoType.STANDARD, 2000, 5);

        assertThatThrownBy(() -> vehicleService.validateCompatibility(truck, List.of(cargo)))
                .isInstanceOf(CapacityExceededException.class)
                .hasMessageContaining("вага");
    }

    @Test
    @DisplayName("❌ validateCompatibility — перевищення об'єму")
    void validateCompatibility_volumeExceeded_throwsCapacityExceededException() {
        Vehicle truck = buildVehicle(VehicleType.TRUCK, 10000, 10);
        Cargo cargo   = buildCargo(CargoType.STANDARD, 500, 20);

        assertThatThrownBy(() -> vehicleService.validateCompatibility(truck, List.of(cargo)))
                .isInstanceOf(CapacityExceededException.class)
                .hasMessageContaining("об'єм");
    }

    @Test
    @DisplayName("✅ validateCompatibility — сукупна вага кількох вантажів в межах нормі")
    void validateCompatibility_multipleCargoWithinCapacity_noException() {
        Vehicle truck  = buildVehicle(VehicleType.TRUCK, 10000, 60);
        Cargo cargo1   = buildCargo(CargoType.STANDARD, 3000, 10);
        Cargo cargo2   = buildCargo(CargoType.STANDARD, 4000, 15);

        assertThatNoException().isThrownBy(
                () -> vehicleService.validateCompatibility(truck, List.of(cargo1, cargo2)));
    }

    @Test
    @DisplayName("✅ addMileage — пробіг збільшується правильно")
    void addMileage_validKm_updatesOdometer() {
        Vehicle vehicle = buildVehicle(VehicleType.TRUCK, 10000, 60);
        vehicle.setCurrentMileageKm(45000);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Vehicle updated = vehicleService.addMileage(1L, 540.0);

        assertThat(updated.getCurrentMileageKm()).isEqualTo(45540.0);
    }
}
