package com.logistics.cargo_api.facade;

import com.logistics.cargo_api.entity.Invoice;
import com.logistics.cargo_api.entity.Order;
import com.logistics.cargo_api.service.DriverService;
import com.logistics.cargo_api.service.InvoiceService;
import com.logistics.cargo_api.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderFulfillmentFacade {

    private final OrderService orderService;
    private final DriverService driverService;
    private final InvoiceService invoiceService;

    @Transactional
    public Invoice completeOrderProcess(Long orderId) {
        Order completedOrder = orderService.confirmDelivery(orderId);

        driverService.resetFatigue(completedOrder.getDriver().getId());

        Invoice generatedInvoice = invoiceService.generateInvoice(orderId);

        return generatedInvoice;
    }
}