package observer;

import model.order.Order;

public class CustomerObserver implements Observer {
    private final Integer customerId;
    private final String customerName;

    public CustomerObserver(Integer customerId, String customerName) {
        this.customerId = customerId;
        this.customerName = customerName;
    }

    @Override
    public void update(String eventType, Order order) {
        if (!order.getCustomerId().equals(this.customerId)) {
            return;
        }
        System.out.print("\n[CUSTOMER NOTIFICATION] Hi " + customerName
                + ", your order #" + order.getOrderId());
        switch (eventType) {
            case "ORDER_PLACED" ->
                    System.out.print(" has been placed successfully!\n");
            case "ORDER_APPROVED" ->
                    System.out.println(" has been approved by admin!\n");
            case "ORDER_READY_FOR_DELIVERY" ->
                    System.out.println(" is ready and waiting for a delivery agent.\n");
            case "ORDER_OUT_FOR_DELIVERY" ->
                    System.out.println(" is out for delivery by agent "
                    + order.getAssignedAgentName());
            case "ORDER_DELIVERED" ->
                    System.out.println(" has been delivered. Enjoy your meal!\n");
            default -> {
            }
        }
//        switch (eventType) {
//            case "ORDER_PLACED":
//                System.out.println("\n[CUSTOMER NOTIFICATION] Hi " + customerName
//                        + ", your order #" + order.getOrderId() + " has been placed successfully!");
//                break;
//            case "ORDER_APPROVED":
//                System.out.println("\n[CUSTOMER NOTIFICATION] Hi " + customerName
//                        + ", your order #" + order.getOrderId() + " has been approved by admin!");
//                break;
//            case "ORDER_READY_FOR_DELIVERY":
//                System.out.println("\n[CUSTOMER NOTIFICATION] Hi " + customerName
//                        + ", your order #" + order.getOrderId() + " is ready and waiting for a delivery agent.");
//                break;
//            case "ORDER_OUT_FOR_DELIVERY":
//                System.out.println("\n[CUSTOMER NOTIFICATION] Hi " + customerName
//                        + ", your order #" + order.getOrderId() + " is out for delivery by agent '"
//                        + order.getAssignedAgentName() + "'!");
//                break;
//            case "ORDER_DELIVERED":
//                System.out.println("\n[CUSTOMER NOTIFICATION] Hi " + customerName
//                        + ", your order #" + order.getOrderId() + " has been delivered. Enjoy your meal!");
    }

    public Integer getCustomerId() {
        return customerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CustomerObserver obj))
            return false;
        return customerId.equals(obj.customerId);
    }

    @Override
    public int hashCode() {
        return customerId.hashCode();
    }
}
