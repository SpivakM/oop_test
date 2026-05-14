package com.logistics.cargo_api.state;

import com.logistics.cargo_api.entity.Order;

public interface OrderState {
    void next(Order order);
    void cancel(Order order);
    String getStatusName();
}