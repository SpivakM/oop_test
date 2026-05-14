package com.logistics.cargo_api.state;

import com.logistics.cargo_api.entity.Order;
import com.logistics.cargo_api.entity.enums.OrderStatus;
import java.time.LocalDateTime;

public class PendingState implements OrderState {
    @Override
    public void next(Order order) {
        order.setStatus(OrderStatus.IN_TRANSIT);
        order.setDepartedAt(LocalDateTime.now());
    }

    @Override
    public void cancel(Order order) {
        order.setStatus(OrderStatus.CANCELLED);
    }

    @Override
    public String getStatusName() {
        return OrderStatus.PENDING.getDisplayName();
    }
}