package dao;

import util.DBConnection;

import java.sql.*;

public class MenuItemDAO {

    public int insert(String itemName, double itemPrice, int categoryId) throws SQLException {
        String sql = "INSERT INTO menu_item (item_name, item_price, category_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, itemName);
            ps.setDouble(2, itemPrice);
            ps.setInt(3, categoryId);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert menu item, no ID returned.");
    }

    public ResultSet getAllWithCategory() throws SQLException {
        String sql = "SELECT mi.item_id, mi.item_name, mi.item_price, mi.category_id, mc.category_name " +
                "FROM menu_item mi JOIN menu_category mc ON mi.category_id = mc.category_id " +
                "ORDER BY mi.item_id";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        return ps.executeQuery();
    }

    public ResultSet getById(int itemId) throws SQLException {
        String sql = "SELECT * FROM menu_item WHERE item_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, itemId);
        return ps.executeQuery();
    }

    public boolean existsByName(String itemName) throws SQLException {
        String sql = "SELECT 1 FROM menu_item WHERE item_name = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, itemName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
