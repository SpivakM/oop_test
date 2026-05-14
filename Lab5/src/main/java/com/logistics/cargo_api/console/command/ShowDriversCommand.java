package com.logistics.cargo_api.console.command;

import com.logistics.cargo_api.entity.Driver;
import com.logistics.cargo_api.service.DriverService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
public class ShowDriversCommand implements Command {
    private final DriverService driverService;

    @Override
    public String getDescription() {
        return "Показати всіх водіїв";
    }

    @Override
    public void execute(Scanner scanner) {
        List<Driver> drivers = driverService.findAll();
        if (drivers.isEmpty()) {
            System.out.println("Водіїв поки немає.");
            return;
        }
        System.out.println("--- Список водіїв ---");
        drivers.forEach(driver -> System.out.println(driver.toString()));
    }
}
