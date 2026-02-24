package service;

import model.payment.Discount;

import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class DiscountService {
    private static volatile DiscountService instance;
    private static final Map<Double, Double> discountMap = new TreeMap<>();

//    Using Discount object future scalability, if complexity increases the map will be <Double, Discount>, we store entire object
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
    public static Discount getDiscount(Double totalAmount){
        Double discountPrice = null;
        for(Map.Entry<Double,Double> discount: discountMap.entrySet()){
            if(totalAmount < discount.getKey())
                break;
            discountPrice = discount.getKey();
        }
        return new Discount(discountPrice, discountMap.get(discountPrice));
    }
}