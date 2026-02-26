package service;

import model.enums.OrderStatus;
import model.order.MenuItem;
import model.order.Order;
import observer.EventManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public String getOrderInfo(Integer orderId){
        Order order = getOrderById(orderId);
        return String.format("%-5s %-20s %-50s %-10s %-5s", order.getOrderId(), order.getCustomerName(), order.getCustomerAddress(), order.getFinalAmount(),order.getPaymentMode());
    }

    public String getOrderDetails(Integer orderId){
        Order o = getOrderById(orderId);
        if(o == null){
            return null;
        }
        String preInfo =  "Order ID: "+ o.getOrderId() +
                "\nCustomer Name: "+ o.getCustomerName() +
                "\nCustomer address: "+ o.getCustomerAddress();
        StringBuilder information = new StringBuilder(preInfo);
        information.append("\n");
        information.append("Item:\n");
        for (Map.Entry<MenuItem, Integer> itemMap: o.getItems().entrySet()){
            information.append(itemMap.getKey().toString())
                    .append("                  ")
                    .append(itemMap.getValue());
        }
        information
                .append("\nTotal: ")
                .append(o.getSubtotal())
                .append("\nDiscount: ")
                .append(o.getDiscountAmount())
                .append("\nPayable amount: ")
                .append(o.getFinalAmount())
                .append("\nPayment mode: ")
                .append(o.getPaymentMode())
                .append("\nOrder status: ")
                .append(o.getStatus())
                .append("\nDelivery Agent: ");
        String name = o.getAssignedAgentName();
        information
                .append(name==null?"Not assigned yet.":name);
        return information.toString();


//        private final Integer orderId;
//        private final Integer customerId;
//        private final String customerName;
//        private final String customerAddress;
//        private final Map<MenuItem, Integer> items;
//        private final Double subtotal;
//        private final Double discountAmount;
//        private final Double finalAmount;
//        private PaymentMode paymentMode;
//        private OrderStatus status;
//        private Integer assignedAgentId;
//        private String assignedAgentName;
    }
}
