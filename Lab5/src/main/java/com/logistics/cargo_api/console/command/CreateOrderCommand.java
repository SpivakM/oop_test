package com.logistics.cargo_api.console.command;

import com.logistics.cargo_api.entity.Cargo;
import com.logistics.cargo_api.entity.Driver;
import com.logistics.cargo_api.entity.Order;
import com.logistics.cargo_api.entity.Route;
import com.logistics.cargo_api.entity.Vehicle;
import com.logistics.cargo_api.service.CargoService;
import com.logistics.cargo_api.service.DriverService;
import com.logistics.cargo_api.service.OrderService;
import com.logistics.cargo_api.service.RouteService;
import com.logistics.cargo_api.service.VehicleService;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
public class CreateOrderCommand implements Command {
    private final OrderService orderService;
    private final RouteService routeService;
    private final VehicleService vehicleService;
    private final DriverService driverService;
    private final CargoService cargoService;

    @Override
    public String getDescription() {
        return "Створити нове замовлення";
    }

    @Override
    public void execute(Scanner scanner) {
        try {
            printAvailableData();

            System.out.print("Введіть ID маршруту: ");
            long routeId = Long.parseLong(scanner.nextLine());

            System.out.print("Введіть ID транспорту: ");
            long vehicleId = Long.parseLong(scanner.nextLine());

            System.out.print("Введіть ID водія: ");
            long driverId = Long.parseLong(scanner.nextLine());

            System.out.print("Введіть ID вантажів (через кому): ");
            List<Long> cargoIds = parseIds(scanner.nextLine());

            Order order = orderService.createOrder(routeId, vehicleId, driverId, cargoIds);
            System.out.println("✅ Замовлення створено: " + order);
        } catch (NumberFormatException e) {
            String message = e.getMessage();
            if (message != null && !message.isBlank()) {
                System.out.println("❌ Помилка: " + message);
            } else {
                System.out.println("❌ Помилка: ID мають бути числовими значеннями.");
            }
        } catch (Exception e) {
            System.out.println("❌ Помилка створення замовлення: " + e.getMessage());
        }
    }

    private void printAvailableData() {
        List<Route> routes = routeService.findAll();
        if (!routes.isEmpty()) {
            System.out.println("--- Доступні маршрути ---");
            routes.forEach(route -> System.out.println(route.toString()));
        }

        List<Vehicle> vehicles = vehicleService.findAvailable();
        if (!vehicles.isEmpty()) {
            System.out.println("--- Доступний транспорт ---");
            vehicles.forEach(vehicle -> System.out.println(vehicle.toString()));
        }

        List<Driver> drivers = driverService.findAvailable();
        if (!drivers.isEmpty()) {
            System.out.println("--- Доступні водії ---");
            drivers.forEach(driver -> System.out.println(driver.toString()));
        }

        List<Cargo> cargos = cargoService.findAllOnWarehouse();
        if (!cargos.isEmpty()) {
            System.out.println("--- Вантажі на складі ---");
            cargos.forEach(cargo -> System.out.println(cargo.toString()));
        }
    }

    private List<Long> parseIds(String input) {
        if (input == null || input.isBlank()) {
            return new ArrayList<>();
        }
        List<Long> ids = new ArrayList<>();
        for (String rawValue : Arrays.stream(input.split(",")).toList()) {
            String value = rawValue.trim();
            if (value.isBlank()) {
                continue;
            }
            try {
                ids.add(Long.parseLong(value));
            } catch (NumberFormatException e) {
                throw new NumberFormatException("Невірний ID вантажу: " + value);
            }
        }
        return ids;
    }
}
