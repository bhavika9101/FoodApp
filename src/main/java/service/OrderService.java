package service;

import dao.OrderDAO;
import dao.OrderItemDAO;
import model.enums.OrderStatus;
import model.enums.PaymentMode;
import model.order.MenuItem;
import model.order.Order;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderService {
    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();

    public Order addOrder(int customerId, String customerName, String customerAddress,
            Map<MenuItem, Integer> items, double subtotal,
            double discountAmount, double finalAmount, String paymentMode) {
        try {
            int orderId = orderDAO.insertOrder(customerId, customerAddress, subtotal,
                    discountAmount, finalAmount, paymentMode, "PLACED");

            for (Map.Entry<MenuItem, Integer> entry : items.entrySet()) {
                MenuItem item = entry.getKey();
                int qty = entry.getValue();
                orderItemDAO.insert(orderId, item.getId(), qty, item.getPrice());
            }

            Order order = new Order(orderId, customerId, customerName, customerAddress,
                    subtotal, discountAmount, finalAmount,
                    PaymentMode.valueOf(paymentMode), OrderStatus.PLACED, null, null);
            order.setItems(items);

            System.out.println("Order #" + orderId + " placed successfully.");
            return order;
        } catch (SQLException e) {
            System.out.println("Error creating order: " + e.getMessage());
            return null;
        }
    }

    public Order getOrderById(int orderId) {
        try (ResultSet rs = orderDAO.getOrderById(orderId)) {
            if (rs.next()) {
                return buildOrderFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching order: " + e.getMessage());
        }
        return null;
    }

    public List<Order> getOrdersByCustomerId(int customerId) {
        List<Order> orders = new ArrayList<>();
        try (ResultSet rs = orderDAO.getOrdersByCustomerId(customerId)) {
            while (rs.next()) {
                orders.add(buildOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching customer orders: " + e.getMessage());
        }
        return orders;
    }

    public List<Order> getPendingOrders() {
        return getOrdersByStatus("PLACED");
    }

    public List<Order> getApprovedOrders() {
        return getOrdersByStatus("APPROVED");
    }

    public List<Order> getOrdersByStatus(String status) {
        List<Order> orders = new ArrayList<>();
        try (ResultSet rs = orderDAO.getOrdersByStatus(status)) {
            while (rs.next()) {
                orders.add(buildOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching orders by status: " + e.getMessage());
        }
        return orders;
    }

    public void updateOrderStatus(int orderId, OrderStatus status) {
        try {
            orderDAO.updateOrderStatus(orderId, status.name());
        } catch (SQLException e) {
            System.out.println("Error updating order status: " + e.getMessage());
        }
    }

    public void updateAssignedAgent(int orderId, int agentId) {
        try {
            orderDAO.updateAssignedAgent(orderId, agentId);
        } catch (SQLException e) {
            System.out.println("Error updating assigned agent: " + e.getMessage());
        }
    }

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        try (ResultSet rs = orderDAO.getAllOrders()) {
            while (rs.next()) {
                orders.add(buildOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching all orders: " + e.getMessage());
        }
        return orders;
    }

    public Map<MenuItem, Integer> getOrderItems(int orderId) {
        try {
            return orderItemDAO.getAllByOrderId(orderId);
        } catch (SQLException e) {
            System.out.println("Error fetching order items: " + e.getMessage());
            return Map.of();
        }
    }

    private Order buildOrderFromResultSet(ResultSet rs) throws SQLException {
        Integer assignedAgentId = rs.getInt("assigned_agent_id");
        if (rs.wasNull()) {
            assignedAgentId = null;
        }
        String agentName = null;
        if (assignedAgentId != null) {
            agentName = orderDAO.getAssignedAgentName(rs.getInt("order_id"));
        }

        return new Order(
                rs.getInt("order_id"),
                rs.getInt("customer_id"),
                rs.getString("customer_name"),
                rs.getString("customer_address"),
                rs.getDouble("subtotal"),
                rs.getDouble("discount_amount"),
                rs.getDouble("final_amount"),
                PaymentMode.valueOf(rs.getString("payment_mode")),
                OrderStatus.valueOf(rs.getString("status")),
                assignedAgentId,
                agentName);
    }

    public List<Order> getUnassignedApprovedOrders() {
        List<Order> orders = new ArrayList<>();
        try (ResultSet rs = orderDAO.getUnassignedApprovedOrders()) {
            while (rs.next()) {
                orders.add(buildOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching unassigned orders: " + e.getMessage());
        }
        return orders;
    }
}
