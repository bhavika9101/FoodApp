package model.payment;

public class UpiPayment implements PaymentStrategy{
    @Override
    public void pay(Double amount) {
        System.out.println(amount + " paid via UPI.");
    }
}
