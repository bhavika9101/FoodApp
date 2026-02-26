package service;

import model.order.Cart;
import model.order.MenuItem;
import model.payment.Discount;

import java.util.HashMap;
import java.util.Map;

public class CartService {
    private final Map<MenuItem, Integer> cartItemMap;

    public CartService() {
        cartItemMap = new HashMap<>();
    }

    public void addToCart(MenuItem menuItem, Integer quantity) {
        cartItemMap.put(menuItem, cartItemMap.getOrDefault(menuItem, 0) + quantity);
    }

    public void removeFromCart(MenuItem menuItem) {
        cartItemMap.remove(menuItem);
    }

    public void reduceQuantity(MenuItem menuItem, Integer quantity) {
        Integer currentQty = cartItemMap.get(menuItem);
        if (currentQty == null) {
            return;
        }
        Integer newQty = currentQty - quantity;
        if (newQty <= 0) {
            cartItemMap.remove(menuItem);
        } else {
            cartItemMap.put(menuItem, newQty);
        }
    }

    public void clearCart() {
        cartItemMap.clear();
    }

    public Double calculateTotalValue() {
        Double total = 0.0;
        for (Map.Entry<MenuItem, Integer> cartItem : cartItemMap.entrySet()) {
            Double unitPrice = cartItem.getKey().getPrice();
            Integer quantity = cartItem.getValue();
            total += unitPrice * quantity;
        }
        return total;
    }

    public Double findDiscount(Double cartValue) {
        Discount discount = DiscountService.getDiscount(cartValue);
        if (discount == null || discount.getDiscountRate() == null) {
            return 0.0;
        }
        return cartValue * discount.getDiscountRate();
    }

    public Double findFinalAmount(Double cartValue, Double discountAmount) {
        return cartValue - discountAmount;
    }

    public Cart finalizeCart(Integer customerId) {
        return new Cart(new HashMap<>(cartItemMap), customerId);
    }

    public Map<MenuItem, Integer> getCartItemMap() {
        return cartItemMap;
    }

    public Boolean isEmpty() {
        return cartItemMap.isEmpty();
    }

    public void printCart() {
        if (cartItemMap.isEmpty()) {
            System.out.println("Your cart is empty.");
            return;
        }
        System.out.println("+----+----------------------+-------+----------+----------+");
        System.out.println("| #  | Item                 | Qty   | Price    | Total    |");
        System.out.println("+----+----------------------+-------+----------+----------+");
        Integer index = 1;
        Double grandTotal = 0.0;
        for (Map.Entry<MenuItem, Integer> entry : cartItemMap.entrySet()) {
            MenuItem item = entry.getKey();
            Integer qty = entry.getValue();
            Double total = item.getPrice() * qty;
            grandTotal += total;
            System.out.printf("| %-2d | %-20s | %-5d | %8.2f | %8.2f |%n",
                    index++, item.getName(), qty, item.getPrice(), total);
        }
        System.out.println("+----+----------------------+-------+----------+----------+");
        System.out.printf("| %-42s | %8.2f |%n", "Cart Total", grandTotal);
        System.out.println("+----+----------------------+-------+----------+----------+");
    }
}
