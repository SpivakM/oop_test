package com.logistics.cargo_api.console.command;

import com.logistics.cargo_api.entity.Order;
import com.logistics.cargo_api.service.OrderService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
public class ShowAllOrdersCommand implements Command {
    private final OrderService orderService;

    @Override
    public String getDescription() {
        return "Показати всі замовлення";
    }

    @Override
    public void execute(Scanner scanner) {
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            System.out.println("Замовлень поки немає.");
            return;
        }
        System.out.println("--- Список замовлень ---");
        orders.forEach(order -> System.out.println(order.toString()));
    }
}