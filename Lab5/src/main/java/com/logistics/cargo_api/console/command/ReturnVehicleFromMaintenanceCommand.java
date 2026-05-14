package com.logistics.cargo_api.console.command;

import com.logistics.cargo_api.service.VehicleService;
import lombok.RequiredArgsConstructor;

import java.util.Scanner;

@RequiredArgsConstructor
public class ReturnVehicleFromMaintenanceCommand implements Command {
    private final VehicleService vehicleService;

    @Override
    public String getDescription() {
        return "Повернути авто з сервісу";
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.print("Введіть ID авто: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            vehicleService.completeMaintenance(id);
            System.out.println("✅ Авто повернено до роботи.");
        } catch (NumberFormatException e) {
            System.out.println("❌ Помилка: ID має бути числом.");
        } catch (Exception e) {
            System.out.println("❌ Помилка: " + e.getMessage());
        }
    }
}
