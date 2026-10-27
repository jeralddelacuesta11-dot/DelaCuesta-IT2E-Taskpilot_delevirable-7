package org.example.dao;

import org.example.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    public boolean register(String username, String password, String role) throws SQLException {
        String sql = "INSERT INTO users(username,password,roles,status) VALUES(?,?,?,'PENDING')";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, password);
            ps.setString(3, role.toUpperCase());
            return ps.executeUpdate() == 1;
        }
    }

    public User login(String username, String password) throws SQLException {
        String sql = "SELECT id,username,password,roles,status FROM users WHERE username = ? AND password = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username.trim()); ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id,username,password,roles,status FROM users ORDER BY id";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) users.add(map(rs));
        }
        return users;
    }

    public List<User> getPendingUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id,username,password,roles,status FROM users WHERE status = 'PENDING' ORDER BY id";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) users.add(map(rs));
        }
        return users;
    }

    public boolean approveUser(int userId) throws SQLException {
        String sql = "UPDATE users SET status = ? WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "APPROVED"); ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean updateUserRoleAndStatus(int userId, String newRole, String newStatus) throws SQLException {
        String sql = "UPDATE users SET roles = ?, status = ? WHERE id = ?;";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newRole.trim().toUpperCase());
            ps.setString(2, newStatus.trim().toUpperCase());
            ps.setInt(3, userId);
            return ps.executeUpdate() == 1;
        }
    }

    public List<User> getEmployees() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id,username,password,roles,status FROM users WHERE roles = 'EMPLOYEE' AND status = 'APPROVED' ORDER BY username";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) users.add(map(rs));
        }
        return users;
    }

    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT id,username,password,roles,status FROM users WHERE id = ?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    private User map(ResultSet rs) throws SQLException {
        return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getString("roles"), rs.getString("status"));
    }
}
