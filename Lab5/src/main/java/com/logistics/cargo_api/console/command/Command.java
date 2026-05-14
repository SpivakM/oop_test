package com.logistics.cargo_api.console.command;

import java.util.Scanner;

public interface Command {
    String getDescription();

    void execute(Scanner scanner);
}