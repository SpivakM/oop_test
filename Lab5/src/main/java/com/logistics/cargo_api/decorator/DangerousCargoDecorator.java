package com.logistics.cargo_api.decorator;

public class DangerousCargoDecorator extends CostDecorator {
    private final double multiplier = 1.5;

    public DangerousCargoDecorator(DeliveryCost wrapper) {
        super(wrapper);
    }

    @Override
    public double getCost() {
        return super.getCost() * multiplier;
    }

    @Override
    public String getDetails() {
        return super.getDetails() + "\n + Націнка за небезпечний вантаж: +" + (int) ((multiplier - 1) * 100) + "%";
    }
}
