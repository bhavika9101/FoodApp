package model.payment;

import model.user.Admin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

public class Discount {
    private final Double priceThreshold;
    private final Double discountRate;

    public Discount(Double priceThreshold, Double discountRate) {
        this.priceThreshold = priceThreshold;
        this.discountRate = discountRate;
    }

    public Double getPriceThreshold() {
        return priceThreshold;
    }

    public Double getDiscountRate() {
        return discountRate;
    }
}
