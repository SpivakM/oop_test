package com.logistics.cargo_api.entity.enums;

import lombok.Getter;

@Getter
public enum VehicleType {
    VAN("Фургон"),
    TRUCK("Вантажівка"),
    REFRIGERATOR_TRUCK("Рефрижератор");

    private final String displayName;

    VehicleType(String displayName) { this.displayName = displayName; }

}