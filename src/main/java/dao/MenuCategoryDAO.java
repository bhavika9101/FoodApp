package dao;

import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuCategoryDAO {

    public int insert(String categoryName, Integer parentCategoryId) throws SQLException {
        String sql = "INSERT INTO menu_category (category_name, parent_category_id) VALUES (?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categoryName);
            if (parentCategoryId != null) {
                ps.setInt(2, parentCategoryId);
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert menu category, no ID returned.");
    }

    public ResultSet getById(int categoryId) throws SQLException {
        String sql = "SELECT * FROM menu_category WHERE category_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, categoryId);
        return ps.executeQuery();
    }

    public ResultSet getAll() throws SQLException {
        String sql = "SELECT * FROM menu_category ORDER BY category_id";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        return ps.executeQuery();
    }

    public ResultSet getAllWithItems() throws SQLException {
        String sql = "SELECT mc.category_id, mc.category_name, mc.parent_category_id, " +
                "mi.item_id, mi.item_name, mi.item_price " +
                "FROM menu_category mc LEFT JOIN menu_item mi ON mc.category_id = mi.category_id " +
                "ORDER BY mc.category_id, mi.item_id";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        return ps.executeQuery();
    }

    public boolean existsByName(String categoryName) throws SQLException {
        String sql = "SELECT 1 FROM menu_category WHERE category_name = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, categoryName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
