package com.logistics.cargo_api.service;

import com.logistics.cargo_api.entity.Driver;
import com.logistics.cargo_api.entity.enums.DriverStatus;
import com.logistics.cargo_api.exception.EntityNotFoundException;
import com.logistics.cargo_api.exception.InvalidStatusTransitionException;
import com.logistics.cargo_api.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverService {

    public static final int MAX_ALLOWED_FATIGUE = 80;

    private final DriverRepository driverRepository;

    @Transactional
    public Driver addDriver(String firstName, String lastName, String licenseNumber) {
        if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
            throw new IllegalArgumentException("Ім'я та прізвище водія не можуть бути порожніми");
        }
        if (licenseNumber == null || licenseNumber.isBlank()) {
            throw new IllegalArgumentException("Номер посвідчення не може бути порожнім");
        }

        Driver driver = Driver.builder()
                .firstName(firstName)
                .lastName(lastName)
                .licenseNumber(licenseNumber)
                .fatigueLevel(0)
                .status(DriverStatus.AVAILABLE)
                .build();

        return driverRepository.save(driver);
    }

    @Transactional(readOnly = true)
    public Driver findById(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Водій", id));
    }

    @Transactional(readOnly = true)
    public List<Driver> findAll() {
        return driverRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Driver> findAvailable() {
        return driverRepository.findByStatus(DriverStatus.AVAILABLE);
    }


    public void validateDispatchable(Driver driver) {
        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            throw new InvalidStatusTransitionException(
                    String.format("Водій %s не доступний (статус: %s)",
                            driver.getFullName(), driver.getStatus().getDisplayName()));
        }
        if (driver.getFatigueLevel() >= MAX_ALLOWED_FATIGUE) {
            throw new InvalidStatusTransitionException(
                    String.format("Водій %s занадто втомлений (%d%%) і не може бути відправлений у рейс",
                            driver.getFullName(), driver.getFatigueLevel()));
        }
    }

    @Transactional
    public Driver updateStatus(Long id, DriverStatus newStatus) {
        Driver driver = findById(id);
        driver.setStatus(newStatus);
        return driverRepository.save(driver);
    }

    @Transactional
    public Driver increaseFatigue(Long id, int amount) {
        Driver driver = findById(id);
        int newFatigue = Math.min(100, driver.getFatigueLevel() + amount);
        driver.setFatigueLevel(newFatigue);
        return driverRepository.save(driver);
    }

    @Transactional
    public Driver resetFatigue(Long id) {
        Driver driver = findById(id);
        driver.setFatigueLevel(0);
        return driverRepository.save(driver);
    }
}