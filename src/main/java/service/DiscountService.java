package service;

import model.payment.Discount;

import java.util.TreeMap;
import java.util.Map;

public class DiscountService {
    private static final Map<Double, Double> discountMap = new TreeMap<>();

    public static boolean add(Discount discount) {
        return discountMap.putIfAbsent(discount.getPriceThreshold(),
                discount.getDiscountRate()) == null;
    }

    public static boolean edit(Discount discount) {
        return discountMap.replace(discount.getPriceThreshold(),
                discount.getDiscountRate()) != null;
    }

    public static boolean remove(Double priceThreshold) {
        return discountMap.remove(priceThreshold) != null;
    }

    public static Discount getDiscount(Double totalAmount) {
        Double discountPrice = null;
        for (Map.Entry<Double, Double> discount : discountMap.entrySet()) {
            if (totalAmount < discount.getKey())
                break;
            discountPrice = discount.getKey();
        }
        if (discountPrice == null) {
            return new Discount(0.0, 0.0);
        }
        return new Discount(discountPrice, discountMap.get(discountPrice));
    }

    public static void printAllDiscounts() {
        if (discountMap.isEmpty()) {
            System.out.println("No discounts configured.");
            return;
        }
        System.out.println("+----------------+----------------+");
        System.out.println("| Threshold (Rs) | Discount Rate  |");
        System.out.println("+----------------+----------------+");
        for (Map.Entry<Double, Double> entry : discountMap.entrySet()) {
            System.out.printf("  | %14.2f | %13.1f%% |%n", entry.getKey(), entry.getValue() * 100);
        }
        System.out.println("+----------------+----------------+");
    }
}