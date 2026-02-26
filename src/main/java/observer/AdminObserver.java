package observer;

import model.order.Order;

public class AdminObserver implements Observer {
    private final String adminName;

    public AdminObserver(String adminName) {
        this.adminName = adminName;
    }

    @Override
    public void update(String eventType, Order order) {
        switch (eventType) {
            case "ORDER_PLACED" -> System.out.println("\n[ADMIN NOTIFICATION] New order #" + order.getOrderId()
                    + " placed by customer " + order.getCustomerName());
            case "ORDER_DELIVERED" -> System.out.println("\n[ADMIN NOTIFICATION] Order #" + order.getOrderId()
                    + " has been delivered successfully.");
            default -> {
            }
        }
    }

    public String getAdminName() {
        return adminName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AdminObserver obj))
            return false;
        return adminName.equals(obj.adminName);
    }

    @Override
    public int hashCode() {
        return adminName.hashCode();
    }
}
