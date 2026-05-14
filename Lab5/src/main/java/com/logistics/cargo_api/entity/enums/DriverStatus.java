package com.logistics.cargo_api.entity.enums;

import lombok.Getter;

@Getter
public enum DriverStatus {
    AVAILABLE("Доступний"),
    BUSY("Зайнятий"),
    ON_LEAVE("У відпустці");

    private final String displayName;

    DriverStatus(String displayName) {
        this.displayName = displayName;
    }

}
