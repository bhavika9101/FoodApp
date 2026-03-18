package dao;

import util.DBConnection;

import java.sql.*;

public class PaymentDAO {

    public int insert(int orderId, double amount, String paymentMode, String paymentIdentifier) throws SQLException {
        String sql = "INSERT INTO payment (order_id, amount, payment_mode, payment_identifier) VALUES (?, ?, ?::payment_mode_enum, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, orderId);
            ps.setDouble(2, amount);
            ps.setString(3, paymentMode);
            if (paymentIdentifier != null) {
                ps.setString(4, paymentIdentifier);
            } else {
                ps.setNull(4, Types.VARCHAR);
            }
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert payment, no ID returned.");
    }
}
