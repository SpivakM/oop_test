package com.logistics.cargo_api.decorator;

public class RefrigeratedCargoDecorator extends CostDecorator {
    private final double multiplier = 1.3;

    public RefrigeratedCargoDecorator(DeliveryCost wrapper) {
        super(wrapper);
    }

    @Override
    public double getCost() {
        return super.getCost() * multiplier;
    }

    @Override
    public String getDetails() {
        return super.getDetails() + "\n + Надбавка за рефрижераторний вантаж: +" + (int) ((multiplier - 1) * 100) + "%";
    }
}
