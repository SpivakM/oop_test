package com.logistics.cargo_api.console.command;

import java.util.Scanner;

public class ExitCommand implements Command {
    @Override
    public String getDescription() {
        return "Вийти з програми";
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.println("Вихід із логістичної системи. До побачення!");
        System.exit(0);
    }
}