package com.logistics.cargo_api.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InvoiceStatus {
    ISSUED("Виставлено"),
    PAID("Оплачено"),
    CANCELLED("Скасовано"),
    OVERDUE("Протерміновано");

    private final String displayName;
}