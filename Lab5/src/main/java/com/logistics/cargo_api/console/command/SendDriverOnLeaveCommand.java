package com.logistics.cargo_api.console.command;

import com.logistics.cargo_api.service.DriverService;
import lombok.RequiredArgsConstructor;

import java.util.Scanner;

@RequiredArgsConstructor
public class SendDriverOnLeaveCommand implements Command {
    private final DriverService driverService;

    @Override
    public String getDescription() {
        return "Відправити водія у відпустку";
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.print("Введіть ID водія: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            driverService.sendOnLeave(id);
            System.out.println("✅ Водія переведено у відпустку.");
        } catch (NumberFormatException e) {
            System.out.println("❌ Помилка: ID має бути числом.");
        } catch (Exception e) {
            System.out.println("❌ Помилка: " + e.getMessage());
        }
    }
}
