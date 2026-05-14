package com.logistics.cargo_api.decorator;

public class FragileInsuranceDecorator extends CostDecorator {
    private final double insurancePremium = 500.0;

    public FragileInsuranceDecorator(DeliveryCost wrapper) {
        super(wrapper);
    }

    @Override
    public double getCost() {
        return super.getCost() + insurancePremium;
    }

    @Override
    public String getDetails() {
        return super.getDetails() + "\n + Обов'язкове страхування крихкого вантажу: " + insurancePremium + " грн";
    }
}