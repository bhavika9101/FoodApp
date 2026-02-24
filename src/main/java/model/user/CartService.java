package model.user;

import model.order.Cart;
import model.order.MenuItem;
import model.payment.Discount;
import service.DiscountService;

import java.util.HashMap;
import java.util.Map;

public class CartService {
//    add customer id
    private Cart cart;
    private final Map<MenuItem, Integer> cartItemMap;
    public CartService(){
        cartItemMap = new HashMap<>();
    }

    public void addToCart(MenuItem menuItem){
        cartItemMap.put(menuItem, cartItemMap.getOrDefault(menuItem, 0)+1);
    }
    public void removeFromCart(MenuItem menuItem){
        cartItemMap.remove(menuItem);
    }
    public void clearCart(){
        cartItemMap.clear();
    }
    public Double calculateTotalValue(){
        Double total = 0.0;
        for(Map.Entry<MenuItem, Integer> cartItem: cartItemMap.entrySet()){
            Double unitPrice = cartItem.getKey().getPrice();
            Integer quantity = cartItem.getValue();
            total += unitPrice*quantity;
        }
        return total;
    }
    public Double findDiscount(Double cartValue){
        Discount discount = DiscountService.getDiscount(cartValue);
        return (cartValue*discount.getDiscountRate());
    }
    public Double findFinalAmount(Double cartValue, Double discountAmount){
        return cartValue-discountAmount;
    }
//    this dispatches final cart to order
    public Cart finalizeCart(){
        new Cart(cartItemMap, )
    }
}
