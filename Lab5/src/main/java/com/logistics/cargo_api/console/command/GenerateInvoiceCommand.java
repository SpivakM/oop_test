package com.logistics.cargo_api.console.command;

import com.logistics.cargo_api.entity.Invoice;
import com.logistics.cargo_api.service.InvoiceService;
import lombok.RequiredArgsConstructor;

import java.util.Scanner;

@RequiredArgsConstructor
public class GenerateInvoiceCommand implements Command {
    private final InvoiceService invoiceService;

    @Override
    public String getDescription() {
        return "Згенерувати інвойс для замовлення";
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.print("Введіть ID замовлення: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            Invoice invoice = invoiceService.generateInvoice(id);
            System.out.println(invoice.getDetailedInfo());
        } catch (NumberFormatException e) {
            System.out.println("❌ Помилка: ID має бути числом.");
        } catch (Exception e) {
            System.out.println("❌ Помилка генерації інвойсу: " + e.getMessage());
        }
    }
}
