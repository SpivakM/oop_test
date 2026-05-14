package com.logistics.cargo_api.console.command;

import com.logistics.cargo_api.entity.Invoice;
import com.logistics.cargo_api.facade.OrderFulfillmentFacade;
import lombok.RequiredArgsConstructor;

import java.util.Scanner;

@RequiredArgsConstructor
public class CompleteOrderProcessCommand implements Command {
    private final OrderFulfillmentFacade orderFulfillmentFacade;

    @Override
    public String getDescription() {
        return "Завершити доставку та сформувати інвойс (Facade)";
    }

    @Override
    public void execute(Scanner scanner) {
        System.out.print("Введіть ID замовлення: ");
        try {
            Long id = Long.parseLong(scanner.nextLine());
            Invoice invoice = orderFulfillmentFacade.completeOrderProcess(id);
            System.out.println("✅ Доставка завершена, інвойс сформовано.");
            System.out.println(invoice.getDetailedInfo());
        } catch (NumberFormatException e) {
            System.out.println("❌ Помилка: ID має бути числом.");
        } catch (Exception e) {
            System.out.println("❌ Помилка завершення замовлення: " + e.getMessage());
        }
    }
}
