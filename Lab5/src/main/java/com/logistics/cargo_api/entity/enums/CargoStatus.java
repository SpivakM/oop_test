package com.logistics.cargo_api.entity.enums;

import lombok.Getter;

@Getter
public enum CargoStatus {
    ON_WAREHOUSE("На складі"),
    IN_TRANSIT("В дорозі"),
    DELIVERED("Доставлено");

    private final String displayName;

    CargoStatus(String displayName) {
        this.displayName = displayName;
    }

}