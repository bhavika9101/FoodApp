package service;

import dao.DiscountDAO;
import model.payment.Discount;

import java.sql.SQLException;
import java.util.List;

public class DiscountService {
    private static final DiscountDAO discountDAO = new DiscountDAO();

    public static Boolean add(Discount discount) {
        try {
            return discountDAO.insert(discount.getThreshold(), discount.getRate());
        } catch (SQLException e) {
            System.out.println("Error adding discount: " + e.getMessage());
            return false;
        }
    }

    public static Boolean remove(Double threshold) {
        try {
            return discountDAO.delete(threshold);
        } catch (SQLException e) {
            System.out.println("Error removing discount: " + e.getMessage());
            return false;
        }
    }

    public static Discount getDiscount(Double amount) {
        try {
            return discountDAO.findByAmount(amount);
        } catch (SQLException e) {
            System.out.println("Error getting discount: " + e.getMessage());
            return new Discount(0.0, 0.0);
        }
    }

    public static void printAllDiscounts() {
        try {
            List<Discount> discounts = discountDAO.getAll();
            if (discounts.isEmpty()) {
                System.out.println("No discounts configured.");
                return;
            }
            System.out.println("\n--- Active Discounts ---");
            for (Discount d : discounts) {
                System.out.printf("  Orders above Rs.%.2f → %.1f%% off%n",
                        d.getThreshold(), d.getRate() * 100);
            }
        } catch (SQLException e) {
            System.out.println("Error listing discounts: " + e.getMessage());
        }
    }
}