package dao;

import model.order.MenuItem;
import util.DBConnection;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class OrderItemDAO {

    public void insert(int orderId, int itemId, int quantity, double unitPrice) throws SQLException {
        String sql = "INSERT INTO order_item (order_id, item_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, itemId);
            ps.setInt(3, quantity);
            ps.setDouble(4, unitPrice);
            ps.executeUpdate();
        }
    }

    public Map<MenuItem, Integer> getAllByOrderId(int orderId) throws SQLException {
        String sql = "SELECT oi.quantity, oi.unit_price, mi.item_id, mi.item_name, mi.item_price " +
                "FROM order_item oi JOIN menu_item mi ON oi.item_id = mi.item_id " +
                "WHERE oi.order_id = ?";
        Map<MenuItem, Integer> items = new LinkedHashMap<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MenuItem item = new MenuItem(
                            rs.getInt("item_id"),
                            rs.getString("item_name"),
                            rs.getDouble("unit_price"));
                    items.put(item, rs.getInt("quantity"));
                }
            }
        }
        return items;
    }
}
