package model.payment;

public class CashPayment implements PaymentStrategy{
    public CashPayment() {}

    @Override
    public void pay(Double amount) {
        System.out.println(amount + "paid via cash.");
    }
}
