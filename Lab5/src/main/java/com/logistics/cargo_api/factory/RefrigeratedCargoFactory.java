package com.logistics.cargo_api.factory;

import com.logistics.cargo_api.entity.Cargo;
import com.logistics.cargo_api.entity.enums.CargoStatus;
import com.logistics.cargo_api.entity.enums.CargoType;
import org.springframework.stereotype.Component;

@Component
public class RefrigeratedCargoFactory implements CargoFactory {

    @Override
    public Cargo createCargo(String name, double weightKg, double volumeM3) {

        String formattedName = name;
        if (!name.toUpperCase().startsWith("[РЕФ]")) {
            formattedName = "[РЕФ] " + name;
        }

        return Cargo.builder()
                .name(formattedName)
                .weightKg(weightKg)
                .volumeM3(volumeM3)
                .cargoType(CargoType.REFRIGERATED) // Фабрика жорстко задає свій тип
                .status(CargoStatus.ON_WAREHOUSE)
                .build();
    }
}