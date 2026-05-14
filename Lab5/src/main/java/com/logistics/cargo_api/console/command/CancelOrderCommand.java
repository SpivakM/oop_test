package com.logistics.cargo_api.console.command;

import com.logistics.cargo_api.service.OrderService;
import lombok.RequiredArgsConstructor;
import java.util.Scanner;

@RequiredArgsConstructor
public class CancelOrderCommand implements Command {
    private final OrderService orderService;

    @Override
    public String getDescription() {
        return "Скасувати замовлення";
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.print("Введіть ID замовлення для скасування: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            orderService.cancelOrder(id);
            System.out.println("✅ Замовлення скасовано (якщо це дозволяв його поточний стан).");
        } catch (NumberFormatException e) {
            System.out.println("❌ Помилка: ID має бути числом.");
        } catch (Exception e) {
            System.out.println("❌ Помилка скасування: " + e.getMessage());
        }
    }
}