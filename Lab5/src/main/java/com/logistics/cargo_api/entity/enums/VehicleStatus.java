package com.logistics.cargo_api.entity.enums;

import lombok.Getter;

@Getter
public enum VehicleStatus {
    AVAILABLE("Доступний"),
    BUSY("Зайнятий"),
    MAINTENANCE("На обслуговуванні");

    private final String displayName;

    VehicleStatus(String displayName) {
        this.displayName = displayName;
    }

}
