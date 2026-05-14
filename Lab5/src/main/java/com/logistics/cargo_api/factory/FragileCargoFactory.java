package com.logistics.cargo_api.factory;

import com.logistics.cargo_api.entity.Cargo;
import com.logistics.cargo_api.entity.enums.CargoStatus;
import com.logistics.cargo_api.entity.enums.CargoType;
import org.springframework.stereotype.Component;

@Component
public class FragileCargoFactory implements CargoFactory {

    @Override
    public Cargo createCargo(String name, double weightKg, double volumeM3) {
        if (weightKg > 1000) {
            throw new IllegalArgumentException("Крихкий вантаж (FRAGILE) не може важити більше 1000 кг однією партією. Розділіть на менші частини.");
        }

        return Cargo.builder()
                .name(name)
                .weightKg(weightKg)
                .volumeM3(volumeM3)
                .cargoType(CargoType.FRAGILE)
                .status(CargoStatus.ON_WAREHOUSE)
                .build();
    }
}