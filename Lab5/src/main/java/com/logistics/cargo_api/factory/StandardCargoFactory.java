package com.logistics.cargo_api.factory;

import com.logistics.cargo_api.entity.Cargo;
import com.logistics.cargo_api.entity.enums.CargoStatus;
import com.logistics.cargo_api.entity.enums.CargoType;
import org.springframework.stereotype.Component;

@Component
public class StandardCargoFactory implements CargoFactory {

    @Override
    public Cargo createCargo(String name, double weightKg, double volumeM3) {
        return Cargo.builder()
                .name(name)
                .weightKg(weightKg)
                .volumeM3(volumeM3)
                .cargoType(CargoType.STANDARD)
                .status(CargoStatus.ON_WAREHOUSE)
                .build();
    }
}