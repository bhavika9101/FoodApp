package model.payment;

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

    public Double getThreshold() {
        return priceThreshold;
    }

    public Double getRate() {
        return discountRate;
    }
}
