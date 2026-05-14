package com.logistics.cargo_api.console.command;

import com.logistics.cargo_api.entity.Vehicle;
import com.logistics.cargo_api.service.VehicleService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
public class ShowVehiclesCommand implements Command {
    private final VehicleService vehicleService;

    @Override
    public String getDescription() {
        return "Показати всі транспортні засоби";
    }

    @Override
    public void execute(Scanner scanner) {
        List<Vehicle> vehicles = vehicleService.findAll();
        if (vehicles.isEmpty()) {
            System.out.println("Транспортних засобів поки немає.");
            return;
        }
        System.out.println("--- Список транспортних засобів ---");
        vehicles.forEach(vehicle -> System.out.println(vehicle.toString()));
    }
}
