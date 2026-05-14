package com.logistics.cargo_api.console.command;

import com.logistics.cargo_api.service.OrderService;
import lombok.RequiredArgsConstructor;
import java.util.Scanner;

@RequiredArgsConstructor
public class AdvanceOrderCommand implements Command {
    private final OrderService orderService;

    @Override
    public String getDescription() {
        return "Просунути замовлення на наступний етап (State Pattern)";
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.print("Введіть ID замовлення: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            orderService.advanceOrder(id); // Звертається до нашого нового методу в сервісі
            System.out.println("✅ Статус замовлення успішно оновлено!");
        } catch (NumberFormatException e) {
            System.out.println("❌ Помилка: ID має бути числом.");
        } catch (Exception e) {
            System.out.println("❌ Помилка: " + e.getMessage());
        }
    }
}