package service;

import model.order.MenuItem;
import model.order.Order;

import java.util.Map;

public class InvoiceService {

    public static void printInvoice(Order order) {
        String line = "+" + "-".repeat(50) + "+";
        String doubleLine = "+" + "=".repeat(50) + "+";

        System.out.println();
        System.out.println(doubleLine);
        System.out.printf("| %-48s |%n", "          FOOD ORDER INVOICE");
        System.out.println(doubleLine);
        System.out.printf("| %-20s %-27s |%n", "Order ID:", "#" + order.getOrderId());
        System.out.printf("| %-20s %-27s |%n", "Customer:", order.getCustomerName());
        System.out.printf("| %-20s %-27s |%n", "Address:", order.getCustomerAddress());
        System.out.printf("| %-20s %-27s |%n", "Status:", order.getStatus().getDisplayName());
        System.out.println(line);
        System.out.printf("| %-18s %5s %10s %11s |%n", "Item", "Qty", "Price", "Total");
        System.out.println(line);

        for (Map.Entry<MenuItem, Integer> entry : order.getItems().entrySet()) {
            MenuItem item = entry.getKey();
            Integer qty = entry.getValue();
            Double itemTotal = item.getPrice() * qty;
            System.out.printf("| %-18s %5d %10.2f %11.2f |%n",
                    item.getName(), qty, item.getPrice(), itemTotal);
        }

        System.out.println(line);
        System.out.printf("| %-35s %12.2f |%n", "Subtotal:", order.getSubtotal());
        System.out.printf("| %-35s %12.2f |%n", "Discount:", order.getDiscountAmount());
        System.out.printf("| %-35s %12.2f |%n", "Final Amount:", order.getFinalAmount());
        System.out.println(line);
        System.out.printf("| %-20s %-27s |%n", "Payment Mode:",
                order.getPaymentMode() != null ? order.getPaymentMode().getDisplayName() : "N/A");
        System.out.printf("| %-20s %-27s |%n", "Delivery Agent:",
                order.getAssignedAgentName() != null ? order.getAssignedAgentName() : "Not Assigned Yet");
        System.out.println(doubleLine);
        System.out.println();
    }
}
