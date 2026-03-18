package dao;

import util.DBConnection;

import java.sql.*;

public class UserDAO {

    public int insertUser(String username, String password, String phoneNumber, String userType) throws SQLException {
        String sql = "INSERT INTO users (username, password, phone_number, user_type) VALUES (?, ?, ?, ?::user_type_enum)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, phoneNumber);
            ps.setString(4, userType);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to insert user, no ID returned.");
    }

    public ResultSet validateUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, password);
        return ps.executeQuery();
    }

    public ResultSet getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, username);
        return ps.executeQuery();
    }

    public boolean isUsernameTaken(String username) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean isPhoneNumberTaken(String phoneNumber) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE phone_number = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, phoneNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void updateUser(int userId, String username, String password, String phoneNumber) throws SQLException {
        String sql = "UPDATE users SET username = ?, password = ?, phone_number = ? WHERE user_id = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, phoneNumber);
            ps.setInt(4, userId);
            ps.executeUpdate();
        }
    }

    public ResultSet getAllByType(String userType) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_type = ?::user_type_enum ORDER BY user_id";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql);
        ps.setString(1, userType);
        return ps.executeQuery();
    }
}
