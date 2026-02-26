package model.payment;

import model.user.User;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class PaymentFactory {
    private static final Map<String, Supplier<PaymentStrategy>> registry = new HashMap<>();

    static {
        registry.put("CASH", CashPayment::new);
        registry.put("UPI", UpiPayment::new);
    }

    public static PaymentStrategy createPayment(String type){
        Supplier<PaymentStrategy> constructor = registry.get(type);
        return constructor.get();
    }

    //    ocp maintained
    public static void registerNewUserType(String type, Supplier<PaymentStrategy> constructor){
        registry.put(type, constructor);
    }
}
