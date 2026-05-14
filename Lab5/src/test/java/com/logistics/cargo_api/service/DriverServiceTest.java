package com.logistics.cargo_api.service;

import com.logistics.cargo_api.entity.Driver;
import com.logistics.cargo_api.entity.enums.DriverStatus;
import com.logistics.cargo_api.exception.InvalidStatusTransitionException;
import com.logistics.cargo_api.repository.DriverRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DriverService Tests")
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverService driverService;

    private Driver buildDriver(Long id, DriverStatus status, int fatigue) {
        return Driver.builder()
                .id(id)
                .firstName("Іван")
                .lastName("Петренко")
                .licenseNumber("АВ123456")
                .fatigueLevel(fatigue)
                .status(status)
                .build();
    }

    @Test
    @DisplayName("✅ findDispatchable — повертає доступних водіїв з низькою втомою")
    void findDispatchable_returnsReadyDrivers() {
        List<Driver> drivers = List.of(
                buildDriver(1L, DriverStatus.AVAILABLE, 10),
                buildDriver(2L, DriverStatus.AVAILABLE, 40)
        );
        when(driverRepository.findByStatusAndFatigueLevelLessThan(
                DriverStatus.AVAILABLE, DriverService.MAX_ALLOWED_FATIGUE))
                .thenReturn(drivers);

        List<Driver> result = driverService.findDispatchable();

        assertThat(result).hasSize(2);
        verify(driverRepository).findByStatusAndFatigueLevelLessThan(
                DriverStatus.AVAILABLE, DriverService.MAX_ALLOWED_FATIGUE);
    }

    @Test
    @DisplayName("✅ sendOnLeave — позитивний: AVAILABLE → ON_LEAVE")
    void sendOnLeave_availableDriver_updatesStatus() {
        Driver driver = buildDriver(1L, DriverStatus.AVAILABLE, 20);
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(driverRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Driver updated = driverService.sendOnLeave(1L);

        assertThat(updated.getStatus()).isEqualTo(DriverStatus.ON_LEAVE);
        verify(driverRepository).save(driver);
    }

    @Test
    @DisplayName("❌ sendOnLeave — негативний: BUSY водія не можна відправити")
    void sendOnLeave_busyDriver_throwsException() {
        Driver driver = buildDriver(1L, DriverStatus.BUSY, 20);
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> driverService.sendOnLeave(1L))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("у рейсі");
    }

    @Test
    @DisplayName("✅ returnFromLeave — позитивний: ON_LEAVE → AVAILABLE + fatigue reset")
    void returnFromLeave_driverOnLeave_resetsFatigue() {
        Driver driver = buildDriver(1L, DriverStatus.ON_LEAVE, 70);
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(driverRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Driver updated = driverService.returnFromLeave(1L);

        assertThat(updated.getStatus()).isEqualTo(DriverStatus.AVAILABLE);
        assertThat(updated.getFatigueLevel()).isZero();
        verify(driverRepository).save(driver);
    }

    @Test
    @DisplayName("❌ returnFromLeave — негативний: водій не у відпустці")
    void returnFromLeave_driverNotOnLeave_throwsException() {
        Driver driver = buildDriver(1L, DriverStatus.AVAILABLE, 10);
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));

        assertThatThrownBy(() -> driverService.returnFromLeave(1L))
                .isInstanceOf(InvalidStatusTransitionException.class)
                .hasMessageContaining("не перебуває");
    }
}
