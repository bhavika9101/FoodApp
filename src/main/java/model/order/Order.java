package model.order;

import model.enums.OrderStatus;
import model.enums.PaymentMode;
import util.IdGenerator;

import java.util.Map;

public class Order {
    private static final IdGenerator idGenerator = new IdGenerator();

    private final Integer orderId;
    private final Integer customerId;
    private String customerName;
    private final String customerAddress;
    private Map<MenuItem, Integer> items;
    private final Double subtotal;
    private final Double discountAmount;
    private final Double finalAmount;
    private PaymentMode paymentMode;
    private OrderStatus status;
    private Integer assignedAgentId;
    private String assignedAgentName;

    public Order(Integer customerId, String customerName, String customerAddress,
            Map<MenuItem, Integer> items, Double subtotal,
            Double discountAmount, Double finalAmount) {
        this.orderId = idGenerator.generateId();
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.items = new java.util.HashMap<>(items);
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.status = OrderStatus.PLACED;
    }

    public Order(int orderId, int customerId, String customerName, String customerAddress,
            double subtotal, double discountAmount, double finalAmount,
            PaymentMode paymentMode, OrderStatus status, Integer assignedAgentId, String assignedAgentName) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.items = new java.util.HashMap<>();
        this.subtotal = subtotal;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.paymentMode = paymentMode;
        this.status = status;
        this.assignedAgentId = assignedAgentId;
        this.assignedAgentName = assignedAgentName;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public Map<MenuItem, Integer> getItems() {
        return items;
    }

    public void setItems(Map<MenuItem, Integer> items) {
        this.items = items;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public Double getFinalAmount() {
        return finalAmount;
    }

    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(PaymentMode paymentMode) {
        this.paymentMode = paymentMode;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Integer getAssignedAgentId() {
        return assignedAgentId;
    }

    public void setAssignedAgentId(Integer assignedAgentId) {
        this.assignedAgentId = assignedAgentId;
    }

    public String getAssignedAgentName() {
        return assignedAgentName;
    }

    public void setAssignedAgentName(String assignedAgentName) {
        this.assignedAgentName = assignedAgentName;
    }
}
