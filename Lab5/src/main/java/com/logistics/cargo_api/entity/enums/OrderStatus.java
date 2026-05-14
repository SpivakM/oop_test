package com.logistics.cargo_api.entity.enums;

import com.logistics.cargo_api.state.*;
import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("Очікує відправлення") {
        @Override
        public OrderState getState() { return new PendingState(); }
    },
    IN_TRANSIT("В дорозі") {
        @Override
        public OrderState getState() { return new InTransitState(); }
    },
    DELIVERED("Доставлено") {
        @Override
        public OrderState getState() { return new DeliveredState(); }
    },
    CANCELLED("Скасовано") {
        @Override
        public OrderState getState() { return new CancelledState(); }
    };

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public abstract OrderState getState();
}