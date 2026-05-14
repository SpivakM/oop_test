package com.logistics.cargo_api.console;

import com.logistics.cargo_api.console.command.*;
import com.logistics.cargo_api.service.CargoService;
import com.logistics.cargo_api.service.OrderService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

@Component
@RequiredArgsConstructor
public class ConsoleMenu implements CommandLineRunner {

    private final OrderService orderService;

    private final Map<Integer, Command> commands = new LinkedHashMap<>();

    @PostConstruct
    public void initCommands() {
        commands.put(1, new ShowAllOrdersCommand(orderService));
        commands.put(2, new AdvanceOrderCommand(orderService));
        commands.put(3, new CancelOrderCommand(orderService));

        commands.put(0, new ExitCommand());
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Логістична система управління вантажами ===");

        while (true) {
            System.out.println("\n--- Головне меню ---");

            commands.forEach((key, command) ->
                    System.out.println(key + ". " + command.getDescription())
            );
            System.out.print("Оберіть дію: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                Command command = commands.get(choice);

                if (command != null) {
                    command.execute(scanner);
                } else {
                    System.out.println("⚠️ Невідомий пункт меню. Спробуйте ще раз.");
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Будь ласка, введіть числове значення.");
            }
        }
    }
}