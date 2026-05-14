package com.logistics.cargo_api.console.command;

import com.logistics.cargo_api.entity.Cargo;
import com.logistics.cargo_api.service.CargoService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
public class ShowWarehouseCargoCommand implements Command {
    private final CargoService cargoService;

    @Override
    public String getDescription() {
        return "Показати вантажі на складі";
    }

    @Override
    public void execute(Scanner scanner) {
        List<Cargo> cargos = cargoService.findAllOnWarehouse();
        if (cargos.isEmpty()) {
            System.out.println("На складі немає доступних вантажів.");
            return;
        }
        System.out.println("--- Вантажі на складі ---");
        cargos.forEach(cargo -> System.out.println(cargo.toString()));
    }
}
