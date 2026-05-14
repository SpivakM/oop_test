package com.logistics.cargo_api.repository;

import com.logistics.cargo_api.entity.Driver;
import com.logistics.cargo_api.entity.enums.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    List<Driver> findByStatus(DriverStatus status);
    List<Driver> findByFatigueLevelLessThan(int maxFatigue);
}