package com.logistics.cargo_api.state;

import com.logistics.cargo_api.entity.Order;
import com.logistics.cargo_api.entity.enums.OrderStatus;
import com.logistics.cargo_api.exception.InvalidStatusTransitionException;

public class DeliveredState implements OrderState {
    @Override
    public void next(Order order) {
        throw new InvalidStatusTransitionException("Замовлення вже доставлено. Наступних етапів немає.");
    }

    @Override
    public void cancel(Order order) {
        throw new InvalidStatusTransitionException("Неможливо скасувати успішно доставлене замовлення.");
    }

    @Override
    public String getStatusName() {
        return OrderStatus.DELIVERED.getDisplayName();
    }
}