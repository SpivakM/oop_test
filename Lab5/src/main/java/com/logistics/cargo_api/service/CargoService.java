package com.logistics.cargo_api.service;

import com.logistics.cargo_api.entity.Cargo;
import com.logistics.cargo_api.entity.enums.CargoStatus;
import com.logistics.cargo_api.entity.enums.CargoType;
import com.logistics.cargo_api.exception.EntityNotFoundException;
import com.logistics.cargo_api.factory.*;
import com.logistics.cargo_api.repository.CargoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CargoService {

    private final CargoRepository cargoRepository;

    private final StandardCargoFactory standardCargoFactory;
    private final FragileCargoFactory fragileCargoFactory;
    private final DangerousCargoFactory dangerousCargoFactory;
    private final RefrigeratedCargoFactory refrigeratedCargoFactory;

    @Transactional
    public Cargo addCargo(String name, double weightKg, double volumeM3, CargoType type) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Назва вантажу не може бути порожньою");
        }
        if (weightKg <= 0) {
            throw new IllegalArgumentException("Вага вантажу повинна бути більше 0");
        }
        if (volumeM3 <= 0) {
            throw new IllegalArgumentException("Об'єм вантажу повинен бути більше 0");
        }
        if (type == null) {
            throw new IllegalArgumentException("Тип вантажу не може бути null");
        }

        CargoFactory factory = getFactoryByType(type);

        Cargo cargo = factory.createCargo(name, weightKg, volumeM3);

        return cargoRepository.save(cargo);
    }

    @Transactional(readOnly = true)
    public Cargo findById(Long id) {
        return cargoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Вантаж", id));
    }

    @Transactional(readOnly = true)
    public List<Cargo> findAllOnWarehouse() {
        return cargoRepository.findByStatus(CargoStatus.ON_WAREHOUSE);
    }

    @Transactional(readOnly = true)
    public List<Cargo> findAll() {
        return cargoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Cargo> searchByName(String keyword) {
        return cargoRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Transactional
    public Cargo updateStatus(Long id, CargoStatus newStatus) {
        Cargo cargo = findById(id);
        cargo.setStatus(newStatus);
        return cargoRepository.save(cargo);
    }

    private CargoFactory getFactoryByType(CargoType type) {
        return switch (type) {
            case STANDARD -> standardCargoFactory;
            case FRAGILE -> fragileCargoFactory;
            case DANGEROUS -> dangerousCargoFactory;
            case REFRIGERATED -> refrigeratedCargoFactory;
        };
    }
}