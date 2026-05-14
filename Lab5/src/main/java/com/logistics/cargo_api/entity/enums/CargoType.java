package com.logistics.cargo_api.entity.enums;

import lombok.Getter;

@Getter
public enum CargoType {
    STANDARD("Стандартний"),
    FRAGILE("Крихкий"),
    DANGEROUS("Небезпечний"),
    REFRIGERATED("Рефрижераторний");

    private final String displayName;

    CargoType(String displayName) { this.displayName = displayName; }

}