package com.logistics.cargo_api;

import com.logistics.cargo_api.console.ConsoleMenu;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class LogisticsApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogisticsApplication.class, args);
	}


	@Bean
	public CommandLineRunner startConsole(ConsoleMenu consoleMenu) {
		return args -> consoleMenu.run();
	}
}