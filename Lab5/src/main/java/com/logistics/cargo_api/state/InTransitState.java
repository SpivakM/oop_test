package com.logistics.cargo_api.state;

import com.logistics.cargo_api.entity.Order;
import com.logistics.cargo_api.entity.enums.OrderStatus;
import com.logistics.cargo_api.exception.InvalidStatusTransitionException;
import java.time.LocalDateTime;

public class InTransitState implements OrderState {
    @Override
    public void next(Order order) {
        order.setStatus(OrderStatus.DELIVERED);
        order.setDeliveredAt(LocalDateTime.now());
    }

    @Override
    public void cancel(Order order) {
        throw new InvalidStatusTransitionException("Неможливо скасувати замовлення, яке вже в дорозі.");
    }

    @Override
    public String getStatusName() {
        return OrderStatus.IN_TRANSIT.getDisplayName();
    }
}