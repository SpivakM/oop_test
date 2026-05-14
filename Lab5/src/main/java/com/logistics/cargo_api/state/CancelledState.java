package com.logistics.cargo_api.state;

import com.logistics.cargo_api.entity.Order;
import com.logistics.cargo_api.entity.enums.OrderStatus;
import com.logistics.cargo_api.exception.InvalidStatusTransitionException;

public class CancelledState implements OrderState {
    @Override
    public void next(Order order) {
        throw new InvalidStatusTransitionException("Скасоване замовлення не може бути відновлене або доставлене.");
    }

    @Override
    public void cancel(Order order) {
        throw new InvalidStatusTransitionException("Замовлення вже скасовано.");
    }

    @Override
    public String getStatusName() {
        return OrderStatus.CANCELLED.getDisplayName();
    }
}