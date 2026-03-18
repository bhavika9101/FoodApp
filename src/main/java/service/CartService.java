package service;

import dao.CartDAO;
import dao.CartItemDAO;
import model.order.MenuItem;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class CartService {
    private final CartDAO cartDAO = new CartDAO();
    private final CartItemDAO cartItemDAO = new CartItemDAO();

    public void addToCart(int customerId, MenuItem item, int quantity) {
        try {
            int cartId = cartDAO.getOrCreateCart(customerId);
            cartItemDAO.insert(cartId, item.getId(), quantity);
            System.out.println("Added " + quantity + "x '" + item.getName() + "' to cart.");
        } catch (SQLException e) {
            System.out.println("Error adding to cart: " + e.getMessage());
        }
    }

    public void removeFromCart(int customerId, int itemId) {
        try {
            int cartId = cartDAO.getCartIdByCustomerId(customerId);
            if (cartId == -1) {
                System.out.println("Cart is empty.");
                return;
            }
            cartItemDAO.delete(cartId, itemId);
            System.out.println("Item removed from cart.");
        } catch (SQLException e) {
            System.out.println("Error removing from cart: " + e.getMessage());
        }
    }

    public void updateQuantity(int customerId, int itemId, int newQuantity) {
        try {
            int cartId = cartDAO.getCartIdByCustomerId(customerId);
            if (cartId == -1) {
                System.out.println("Cart is empty.");
                return;
            }
            cartItemDAO.updateQuantity(cartId, itemId, newQuantity);
            System.out.println("Quantity updated.");
        } catch (SQLException e) {
            System.out.println("Error updating quantity: " + e.getMessage());
        }
    }

    public Map<MenuItem, Integer> getCartItems(int customerId) {
        try {
            int cartId = cartDAO.getCartIdByCustomerId(customerId);
            if (cartId == -1) {
                return new LinkedHashMap<>();
            }
            return cartItemDAO.getCartItemsByCartId(cartId);
        } catch (SQLException e) {
            System.out.println("Error fetching cart items: " + e.getMessage());
            return new LinkedHashMap<>();
        }
    }

    public double calculateTotalValue(int customerId) {
        try {
            int cartId = cartDAO.getCartIdByCustomerId(customerId);
            if (cartId == -1) {
                return 0.0;
            }
            return cartItemDAO.calculateCartValue(cartId);
        } catch (SQLException e) {
            System.out.println("Error calculating cart value: " + e.getMessage());
            return 0.0;
        }
    }

    public void clearCart(int customerId) {
        try {
            int cartId = cartDAO.getCartIdByCustomerId(customerId);
            if (cartId != -1) {
                cartItemDAO.deleteByCartId(cartId);
            }
        } catch (SQLException e) {
            System.out.println("Error clearing cart: " + e.getMessage());
        }
    }

    public boolean isCartEmpty(int customerId) {
        try {
            int cartId = cartDAO.getCartIdByCustomerId(customerId);
            if (cartId == -1) {
                return true;
            }
            return cartItemDAO.isEmpty(cartId);
        } catch (SQLException e) {
            System.out.println("Error checking cart: " + e.getMessage());
            return true;
        }
    }
}
