package com.logistics.cargo_api.console.command;

import com.logistics.cargo_api.entity.Route;
import com.logistics.cargo_api.service.RouteService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
public class ShowRoutesCommand implements Command {
    private final RouteService routeService;

    @Override
    public String getDescription() {
        return "Показати всі маршрути";
    }

    @Override
    public void execute(Scanner scanner) {
        List<Route> routes = routeService.findAll();
        if (routes.isEmpty()) {
            System.out.println("Маршрутів поки немає.");
            return;
        }
        System.out.println("--- Список маршрутів ---");
        routes.forEach(route -> System.out.println(route.toString()));
    }
}
