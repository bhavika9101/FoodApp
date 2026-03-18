package dao;

import model.payment.Discount;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscountDAO {

    public boolean insert(double priceThreshold, double discountRate) throws SQLException {
        String sql = "INSERT INTO discount (price_threshold, discount_rate) VALUES (?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setDouble(1, priceThreshold);
            ps.setDouble(2, discountRate);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().equals("23505")) {
                return false;
            }
            throw e;
        }
    }

    public boolean delete(double priceThreshold) throws SQLException {
        String sql = "DELETE FROM discount WHERE price_threshold = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setDouble(1, priceThreshold);
            return ps.executeUpdate() > 0;
        }
    }

    public Discount findByAmount(double amount) throws SQLException {
        String sql = "SELECT * FROM discount WHERE price_threshold <= ? ORDER BY price_threshold DESC LIMIT 1";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setDouble(1, amount);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Discount(rs.getDouble("price_threshold"), rs.getDouble("discount_rate"));
                }
            }
        }
        return new Discount(0.0, 0.0);
    }

    public List<Discount> getAll() throws SQLException {
        String sql = "SELECT * FROM discount ORDER BY price_threshold";
        List<Discount> discounts = new ArrayList<>();
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                discounts.add(new Discount(rs.getDouble("price_threshold"), rs.getDouble("discount_rate")));
            }
        }
        return discounts;
    }
}
