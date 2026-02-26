package model.payment;

import model.enums.PaymentMode;
import util.IdGenerator;

public class Payment {
    private static final IdGenerator idGenerator = new IdGenerator();
    private PaymentStrategy strategy;
    private final Integer paymentId;
    private final Integer orderId;
    private final Double amount;
    private final PaymentMode paymentMode;
    private Boolean isCompleted;

    public Payment(Integer orderId, Double amount, PaymentMode paymentMode, PaymentStrategy strategy) {
        this.paymentId = idGenerator.generateId();
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMode = paymentMode;
        this.isCompleted = Boolean.FALSE;
        this.strategy = strategy;
    }

    public void processPayment() {
        strategy.pay(amount);
        this.isCompleted = Boolean.TRUE;
        System.out.println("---------------------------------------------");
        System.out.println("  Payment Successful!");
        System.out.println("  Payment ID : " + paymentId);
        System.out.println("  Amount     : Rs." + String.format("%.2f", amount));
        System.out.println("  Mode       : " + paymentMode.getDisplayName());
        System.out.println("---------------------------------------------");
    }

    public Integer getPaymentId() {
        return paymentId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public Double getAmount() {
        return amount;
    }

    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }
}
