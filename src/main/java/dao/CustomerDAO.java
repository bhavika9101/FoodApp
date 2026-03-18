package dao;

import util.DBConnection;

import java.sql.*;

public class CustomerDAO {

    public void insertCustomer(int customerId, String address) throws SQLException {
        String sql = "INSERT INTO customer (customer_id, address) VALUES (?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setString(2, address);
            ps.executeUpdate();
        }
    }

    public void updateAddress(int customerId, String address) throws SQLException {
        String sql = "UPDATE customer SET address = ? WHERE customer_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, address);
            ps.setInt(2, customerId);
            ps.executeUpdate();
        }
    }

    public String getAddress(int customerId) throws SQLException {
        String sql = "SELECT address FROM customer WHERE customer_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("address");
                }
            }
        }
        return null;
    }
}
