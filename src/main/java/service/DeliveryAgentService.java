package service;

import model.enums.DeliveryAgentStatus;
import model.order.Order;
import model.user.DeliveryAgent;
import model.user.User;

public class DeliveryAgentService extends BaseService {
    private static Integer deliveryAgentCount = 0;
    private static final Integer DELIVERY_AGENT_COUNT_LIMIT = 2;

    @Override
    public User signUp(String type, String username, String password) {
        if (!type.equalsIgnoreCase("delivery_agent")) {
            System.out.println("Invalid user type. Sign Up unsuccessful.");
            return null;
        }
        if (deliveryAgentCount.equals(DELIVERY_AGENT_COUNT_LIMIT)) {
            System.out.println("Can't create more than two Delivery agents.");
            return null;
        }
        User user = super.signUp(type, username, password);
        if (user == null)
            return null;
        deliveryAgentCount++;
        return user;
    }

    public DeliveryAgent getAgentByUsername(String username) {
        User user = getUserByUsername(username);
        if (user instanceof DeliveryAgent) {
            return (DeliveryAgent) user;
        }
        return null;
    }

    public DeliveryAgent findAvailableAgent() {
        for (User user : getLoggedInUsers()) {
            if (user instanceof DeliveryAgent) {
                DeliveryAgent agent = (DeliveryAgent) user;
                if (agent.isAvailable()) {
                    return agent;
                }
            }
        }
        return null;
    }

    public void markOrderAsDelivered(DeliveryAgent agent, OrderService orderService, AdminService adminService) {
        Integer currentOrderId = agent.getCurrentOrderId();
        if (currentOrderId == null) {
            System.out.println("No order assigned to you currently.");
            return;
        }
        Order order = orderService.getOrderById(currentOrderId);
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }
        if (order.getStatus() != model.enums.OrderStatus.OUT_FOR_DELIVERY) {
            System.out.println("You must start the delivery first before marking it as delivered.");
            System.out.println("Current status: " + order.getStatus().getDisplayName());
            return;
        }

        agent.setStatus(DeliveryAgentStatus.AVAILABLE);
        agent.setCurrentOrderId(null);
        orderService.updateOrderStatus(currentOrderId, model.enums.OrderStatus.DELIVERED);
        System.out.println(
                "Order #" + currentOrderId + " marked as DELIVERED. You are now available for new deliveries.");

        if (adminService != null) {
            adminService.processDeliveryQueue();
        }
    }

    public void viewAssignedOrder(DeliveryAgent agent, OrderService orderService) {
        Integer currentOrderId = agent.getCurrentOrderId();
        if (currentOrderId == null) {
            System.out.println("No order assigned to you currently.");
            return;
        }
        Order order = orderService.getOrderById(currentOrderId);
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }
        System.out.println("\n--- Assigned Order Details ---");
        System.out.println("  Order ID     : #" + order.getOrderId());
        System.out.println("  Customer     : " + order.getCustomerName());
        System.out.println("  Address      : " + order.getCustomerAddress());
        System.out.println("  Order Status : " + order.getStatus().getDisplayName());
        System.out.println("  Final Amount : Rs." + String.format("%.2f", order.getFinalAmount()));
        System.out.println("-----------------------------");
    }

    public Boolean isAnyAgentLoggedIn() {
        return isAnyUserLoggedIn();
    }

    public void startDelivery(DeliveryAgent agent, OrderService orderService) {
        Integer currentOrderId = agent.getCurrentOrderId();
        if (currentOrderId == null) {
            System.out.println("No order assigned to you.");
            return;
        }
        Order order = orderService.getOrderById(currentOrderId);
        if (order != null && order.getStatus() == model.enums.OrderStatus.READY_FOR_DELIVERY) {
            orderService.updateOrderStatus(currentOrderId, model.enums.OrderStatus.OUT_FOR_DELIVERY);
            System.out.println("You have picked up order #" + currentOrderId + ". Now delivering...");
        } else {
            System.out.println("Order is not ready for delivery.");
        }
    }
}
