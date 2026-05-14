package com.logistics.cargo_api.decorator;

public class BaseDeliveryCost implements DeliveryCost {
    private final double distanceKm;
    private final double ratePerKm = 15.0;

    public BaseDeliveryCost(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    @Override
    public double getCost() {
        return distanceKm * ratePerKm;
    }

    @Override
    public String getDetails() {
        return String.format("Базова доставка (%.0f км * %.2f) = %.2f грн",
                distanceKm, ratePerKm, getCost());
    }
}