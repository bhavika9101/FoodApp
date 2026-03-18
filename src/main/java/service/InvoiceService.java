package service;

import model.order.MenuItem;
import model.order.Order;

import java.util.Map;

public class InvoiceService {
    private final OrderService orderService;

    public InvoiceService(OrderService orderService) {
        this.orderService = orderService;
    }

    public void printInvoice(int orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            System.out.println("Order #" + orderId + " not found.");
            return;
        }

        Map<MenuItem, Integer> items = orderService.getOrderItems(orderId);

        System.out.println("\n======================================");
        System.out.println("           FOOD APP INVOICE           ");
        System.out.println("======================================");
        System.out.printf("  Order ID      : #%d%n", order.getOrderId());
        System.out.printf("  Customer      : %s%n", order.getCustomerName());
        System.out.printf("  Address       : %s%n", order.getCustomerAddress());
        System.out.printf("  Status        : %s%n", order.getStatus().getDisplayName());

        if (order.getAssignedAgentName() != null) {
            System.out.printf("  Delivery Agent: %s%n", order.getAssignedAgentName());
        }

        System.out.println("--------------------------------------");
        System.out.println("  Items:");
        for (Map.Entry<MenuItem, Integer> entry : items.entrySet()) {
            MenuItem item = entry.getKey();
            int qty = entry.getValue();
            System.out.printf("    %-20s x%d  Rs.%.2f%n",
                    item.getName(), qty, item.getPrice() * qty);
        }

        System.out.println("--------------------------------------");
        System.out.printf("  Subtotal      : Rs.%.2f%n", order.getSubtotal());
        System.out.printf("  Discount      : Rs.%.2f%n", order.getDiscountAmount());
        System.out.printf("  Final Amount  : Rs.%.2f%n", order.getFinalAmount());
        System.out.printf("  Payment Mode  : %s%n", order.getPaymentMode().getDisplayName());
        System.out.println("======================================\n");
    }
}
