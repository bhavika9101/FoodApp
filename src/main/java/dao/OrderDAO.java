package dao;

import util.DBConnection;

import java.sql.*;

public class OrderDAO {

    public int insertOrder(int customerId, String customerAddress, double subtotal,
            double discountAmount, double finalAmount, String paymentMode, String status) throws SQLException {
        String sql = "INSERT INTO orders (customer_id, customer_address, subtotal, discount_amount, final_amount, payment_mode, status) "
                +
                "VALUES (?, ?, ?, ?, ?, ?::payment_mode_enum, ?::order_status_enum)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, customerId);
            ps.setString(2, customerAddress);
            ps.setDouble(3, subtotal);
            ps.setDouble(4, discountAmount);
            ps.setDouble(5, finalAmount);
            ps.setString(6, paymentMode);
            ps.setString(7, status);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert order, no ID returned.");
    }

    public ResultSet getOrderById(int orderId) throws SQLException {
        String sql = "SELECT o.*, u.username AS customer_name " +
                "FROM orders o JOIN users u ON o.customer_id = u.user_id " +
                "WHERE o.order_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, orderId);
        return ps.executeQuery();
    }

    public ResultSet getOrdersByCustomerId(int customerId) throws SQLException {
        String sql = "SELECT o.*, u.username AS customer_name " +
                "FROM orders o JOIN users u ON o.customer_id = u.user_id " +
                "WHERE o.customer_id = ? ORDER BY o.created_at DESC";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, customerId);
        return ps.executeQuery();
    }

    public ResultSet getOrdersByStatus(String status) throws SQLException {
        String sql = "SELECT o.*, u.username AS customer_name " +
                "FROM orders o JOIN users u ON o.customer_id = u.user_id " +
                "WHERE o.status = ?::order_status_enum ORDER BY o.created_at";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, status);
        return ps.executeQuery();
    }

    public void updateOrderStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE orders SET status = ?::order_status_enum WHERE order_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

    public void updateAssignedAgent(int orderId, int agentId) throws SQLException {
        String sql = "UPDATE orders SET assigned_agent_id = ? WHERE order_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, agentId);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        }
    }

    public ResultSet getAllOrders() throws SQLException {
        String sql = "SELECT o.*, u.username AS customer_name " +
                "FROM orders o JOIN users u ON o.customer_id = u.user_id " +
                "ORDER BY o.created_at DESC";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        return ps.executeQuery();
    }

    public String getAssignedAgentName(int orderId) throws SQLException {
        String sql = "SELECT u.username FROM orders o " +
                "JOIN delivery_agent da ON o.assigned_agent_id = da.delivery_agent_id " +
                "JOIN users u ON da.delivery_agent_id = u.user_id " +
                "WHERE o.order_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        }
        return null;
    }

    public ResultSet getUnassignedApprovedOrders() throws SQLException {
        String sql = "SELECT o.*, u.username AS customer_name " +
                "FROM orders o JOIN users u ON o.customer_id = u.user_id " +
                "WHERE o.status = 'APPROVED' AND o.assigned_agent_id IS NULL " +
                "ORDER BY o.created_at";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        return ps.executeQuery();
    }
}
