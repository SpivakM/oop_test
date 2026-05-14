package com.logistics.cargo_api.factory;

import com.logistics.cargo_api.entity.Cargo;
import com.logistics.cargo_api.entity.enums.CargoStatus;
import com.logistics.cargo_api.entity.enums.CargoType;
import org.springframework.stereotype.Component;

@Component
public class DangerousCargoFactory implements CargoFactory {

    @Override
    public Cargo createCargo(String name, double weightKg, double volumeM3) {
        String formattedName = "[НЕБЕЗПЕЧНО] " + name;

        return Cargo.builder()
                .name(formattedName)
                .weightKg(weightKg)
                .volumeM3(volumeM3)
                .cargoType(CargoType.DANGEROUS)
                .status(CargoStatus.ON_WAREHOUSE)
                .build();
    }
}