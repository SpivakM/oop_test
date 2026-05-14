package com.logistics.cargo_api.decorator;

public abstract class CostDecorator implements DeliveryCost {
    protected final DeliveryCost wrapper;

    public CostDecorator(DeliveryCost wrapper) {
        this.wrapper = wrapper;
    }

    @Override
    public double getCost() {
        return wrapper.getCost();
    }

    @Override
    public String getDetails() {
        return wrapper.getDetails();
    }
}