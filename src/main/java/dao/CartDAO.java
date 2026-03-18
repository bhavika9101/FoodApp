package dao;

import util.DBConnection;

import java.sql.*;

public class CartDAO {

    public int insertCart(int customerId) throws SQLException {
        String sql = "INSERT INTO cart (customer_id) VALUES (?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, customerId);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert cart, no ID returned.");
    }

    public int getCartIdByCustomerId(int customerId) throws SQLException {
        String sql = "SELECT cart_id FROM cart WHERE customer_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cart_id");
                }
            }
        }
        return -1;
    }

    public void deleteByCustomerId(int customerId) throws SQLException {
        String sql = "DELETE FROM cart WHERE customer_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.executeUpdate();
        }
    }

    public int getOrCreateCart(int customerId) throws SQLException {
        int cartId = getCartIdByCustomerId(customerId);
        if (cartId == -1) {
            cartId = insertCart(customerId);
        }
        return cartId;
    }
}
