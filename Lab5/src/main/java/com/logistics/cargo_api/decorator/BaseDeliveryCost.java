package com.logistics.cargo_api.decorator;

public class BaseDeliveryCost implements DeliveryCost {
    private final double distanceKm;
    private final double fuelCost;
    private final double amortizationCost;

    public BaseDeliveryCost(double distanceKm, double fuelCost, double amortizationCost) {
        this.distanceKm = distanceKm;
        this.fuelCost = fuelCost;
        this.amortizationCost = amortizationCost;
    }

    @Override
    public double getCost() {
        return fuelCost + amortizationCost;
    }

    @Override
    public String getDetails() {
        return String.format(
                "Базова доставка (%.0f км)\n - Пальне: %.2f грн\n - Амортизація: %.2f грн\nРазом: %.2f грн",
                distanceKm, fuelCost, amortizationCost, getCost());
    }
}
