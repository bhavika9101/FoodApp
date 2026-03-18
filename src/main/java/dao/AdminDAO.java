package dao;

import util.DBConnection;

import java.sql.*;

public class AdminDAO {

    public void insertAdmin(int adminId) throws SQLException {
        String sql = "INSERT INTO admin (admin_id) VALUES (?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, adminId);
            ps.executeUpdate();
        }
    }

    public double getRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(final_amount), 0) AS revenue FROM orders WHERE status IN ('APPROVED', 'READY_FOR_DELIVERY', 'OUT_FOR_DELIVERY', 'DELIVERED')";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("revenue");
            }
        }
        return 0.0;
    }
}
