package dao;

import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeliveryAgentDAO {

    public void insertAgent(int agentId, String status, double grossEarning, double baseSalary, double commissionRate)
            throws SQLException {
        String sql = "INSERT INTO delivery_agent (delivery_agent_id, status, gross_earning, base_salary, commission_rate) VALUES (?, ?::delivery_agent_status, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, agentId);
            ps.setString(2, status);
            ps.setDouble(3, grossEarning);
            ps.setDouble(4, baseSalary);
            ps.setDouble(5, commissionRate);
            ps.executeUpdate();
        }
    }

    public ResultSet getAgentsByStatus(String status) throws SQLException {
        String sql = "SELECT u.*, da.status AS agent_status, da.gross_earning, da.base_salary, da.commission_rate " +
                "FROM delivery_agent da JOIN users u ON da.delivery_agent_id = u.user_id " +
                "WHERE da.status = ?::delivery_agent_status";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, status);
        return ps.executeQuery();
    }

    public void updateStatus(int agentId, String status) throws SQLException {
        String sql = "UPDATE delivery_agent SET status = ?::delivery_agent_status WHERE delivery_agent_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, agentId);
            ps.executeUpdate();
        }
    }

    public void updateGrossEarning(int agentId, double amount) throws SQLException {
        String sql = "UPDATE delivery_agent SET gross_earning = gross_earning + ? WHERE delivery_agent_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setInt(2, agentId);
            ps.executeUpdate();
        }
    }

    public double getGrossEarning(int agentId) throws SQLException {
        String sql = "SELECT gross_earning FROM delivery_agent WHERE delivery_agent_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, agentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("gross_earning");
                }
            }
        }
        return 0.0;
    }

    public void updateBaseSalary(int agentId, double salary) throws SQLException {
        String sql = "UPDATE delivery_agent SET base_salary = ? WHERE delivery_agent_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setDouble(1, salary);
            ps.setInt(2, agentId);
            ps.executeUpdate();
        }
    }

    public void updateCommissionRate(int agentId, double rate) throws SQLException {
        String sql = "UPDATE delivery_agent SET commission_rate = ? WHERE delivery_agent_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setDouble(1, rate);
            ps.setInt(2, agentId);
            ps.executeUpdate();
        }
    }

    public String getStatus(int agentId) throws SQLException {
        String sql = "SELECT status FROM delivery_agent WHERE delivery_agent_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, agentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        }
        return null;
    }

    public Integer getCurrentOrderId(int agentId) throws SQLException {
        String sql = "SELECT o.order_id FROM orders o WHERE o.assigned_agent_id = ? AND o.status IN ('READY_FOR_DELIVERY', 'OUT_FOR_DELIVERY') LIMIT 1";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, agentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("order_id");
                }
            }
        }
        return null;
    }

    public Map<String, Double> getBaseSalaryAndCommission(int agentId) throws SQLException {
        String sql = "SELECT base_salary, commission_rate FROM delivery_agent WHERE delivery_agent_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, agentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Double> info = new HashMap<>();
                    info.put("base_salary", rs.getDouble("base_salary"));
                    info.put("commission_rate", rs.getDouble("commission_rate"));
                    return info;
                }
            }
        }
        return new HashMap<>();
    }

    public ResultSet getAgentById(int agentId) throws SQLException {
        String sql = "SELECT u.*, da.status AS agent_status, da.gross_earning, da.base_salary, da.commission_rate " +
                "FROM delivery_agent da JOIN users u ON da.delivery_agent_id = u.user_id " +
                "WHERE da.delivery_agent_id = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setInt(1, agentId);
        return ps.executeQuery();
    }

    public int getAgentCount() throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM delivery_agent";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("cnt");
            }
        }
        return 0;
    }
}
