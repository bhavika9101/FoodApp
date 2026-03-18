package dao;

import model.order.MenuItem;
import util.DBConnection;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class CartItemDAO {

    public void insert(int cartId, int itemId, int quantity) throws SQLException {
        String sql = "INSERT INTO cart_item (cart_id, item_id, quantity) VALUES (?, ?, ?) " +
                "ON CONFLICT (cart_id, item_id) DO UPDATE SET quantity = cart_item.quantity + EXCLUDED.quantity";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ps.setInt(2, itemId);
            ps.setInt(3, quantity);
            ps.executeUpdate();
        }
    }

    public void updateQuantity(int cartId, int itemId, int newQuantity) throws SQLException {
        if (newQuantity <= 0) {
            delete(cartId, itemId);
            return;
        }
        String sql = "UPDATE cart_item SET quantity = ? WHERE cart_id = ? AND item_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, cartId);
            ps.setInt(3, itemId);
            ps.executeUpdate();
        }
    }

    public void delete(int cartId, int itemId) throws SQLException {
        String sql = "DELETE FROM cart_item WHERE cart_id = ? AND item_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ps.setInt(2, itemId);
            ps.executeUpdate();
        }
    }

    public void deleteByCartId(int cartId) throws SQLException {
        String sql = "DELETE FROM cart_item WHERE cart_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ps.executeUpdate();
        }
    }

    public double calculateCartValue(int cartId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(ci.quantity * mi.item_price), 0) AS total " +
                "FROM cart_item ci JOIN menu_item mi ON ci.item_id = mi.item_id " +
                "WHERE ci.cart_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, cartId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        }
        return 0.0;
    }

    public Map<MenuItem, Integer> getCartItemsByCartId(int cartId) throws SQLException {
        String sql = "SELECT mi.item_id, mi.item_name, mi.item_price, ci.quantity " +
                "FROM cart_item ci JOIN menu_item mi ON ci.item_id = mi.item_id " +
                "WHERE ci.cart_id = ?";
        Map<MenuItem, Integer> items = new LinkedHashMap<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, cartId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MenuItem item = new MenuItem(
                            rs.getInt("item_id"),
                            rs.getString("item_name"),
                            rs.getDouble("item_price"));
                    items.put(item, rs.getInt("quantity"));
                }
            }
        }
        return items;
    }

    public int getQuantity(int cartId, int itemId) throws SQLException {
        String sql = "SELECT quantity FROM cart_item WHERE cart_id = ? AND item_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ps.setInt(2, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity");
                }
            }
        }
        return 0;
    }

    public boolean isEmpty(int cartId) throws SQLException {
        String sql = "SELECT 1 FROM cart_item WHERE cart_id = ? LIMIT 1";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, cartId);
            try (ResultSet rs = ps.executeQuery()) {
                return !rs.next();
            }
        }
    }
}
