package com.logistics.cargo_api.decorator;

public class FragileInsuranceDecorator extends CostDecorator {
    private final double multiplier = 1.2;

    public FragileInsuranceDecorator(DeliveryCost wrapper) {
        super(wrapper);
    }

    @Override
    public double getCost() {
        return super.getCost() * multiplier;
    }

    @Override
    public String getDetails() {
        return super.getDetails() + "\n + Надбавка за крихкий вантаж: +" + (int) ((multiplier - 1) * 100) + "%";
    }
}
