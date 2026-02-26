package service;

import model.enums.OrderStatus;
import model.order.Order;
import observer.EventManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderService {
    private final List<Order> allOrders = new ArrayList<>();
    private final EventManager eventManager;

    public OrderService(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void addOrder(Order order) {
        allOrders.add(order);
        eventManager.notifyObservers("ORDER_PLACED", order);
    }

    public Order getOrderById(Integer orderId) {
        return allOrders.stream()
                .filter(o -> o.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    public List<Order> getOrdersByCustomerId(Integer customerId) {
        return allOrders.stream()
                .filter(o -> o.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    public List<Order> getPendingOrders() {
        return allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.PLACED)
                .collect(Collectors.toList());
    }

    public List<Order> getApprovedOrders() {
        return allOrders.stream()
                .filter(o -> o.getStatus() == OrderStatus.APPROVED)
                .collect(Collectors.toList());
    }

    public void updateOrderStatus(Integer orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);
        if (order == null) {
            System.out.println("Order #" + orderId + " not found.");
            return;
        }
        order.setStatus(newStatus);

        switch (newStatus) {
            case APPROVED:
                eventManager.notifyObservers("ORDER_APPROVED", order);
                break;
            case READY_FOR_DELIVERY:
                eventManager.notifyObservers("ORDER_READY_FOR_DELIVERY", order);
                break;
            case OUT_FOR_DELIVERY:
                eventManager.notifyObservers("ORDER_OUT_FOR_DELIVERY", order);
                break;
            case DELIVERED:
                eventManager.notifyObservers("ORDER_DELIVERED", order);
                break;
            default:
                break;
        }
    }

    public List<Order> getAllOrders() {
        return allOrders;
    }
}
