package com.logistics.cargo_api.factory;

import com.logistics.cargo_api.entity.Cargo;

public interface CargoFactory {
    Cargo createCargo(String name, double weightKg, double volumeM3);
}