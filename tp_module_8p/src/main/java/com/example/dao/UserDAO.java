package com.example.dao;

import com.example.model.User;
import java.sql.*;
import java.util.*;

public class UserDAO {
    private String jdbcURL = "jdbc:mysql://localhost:3306/demo_db?useSSL=false&serverTimezone=UTC";
    private String jdbcUser = "root";
    private String jdbcPass = "your_password";

    private static final String INSERT_SQL = "INSERT INTO users (nom, email) VALUES (?, ?)";
    private static final String SELECT_BY_ID = "SELECT id, nom, email FROM users WHERE id = ?";
    private static final String SELECT_ALL = "SELECT id, nom, email FROM users ORDER BY id DESC";
    private static final String DELETE_SQL = "DELETE FROM users WHERE id = ?";
    private static final String UPDATE_SQL = "UPDATE users SET nom = ?, email = ? WHERE id = ?";
    private static final String SEARCH_BY_NAME = "SELECT id, nom, email FROM users WHERE nom LIKE ? ORDER BY id DESC";

    public UserDAO() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPass);
    }

    public void insertUser(User user) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {
            ps.setString(1, user.getNom());
            ps.setString(2, user.getEmail());
            ps.executeUpdate();
        }
    }

    public User selectUser(int id) throws SQLException {
        User user = null;
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) user = new User(rs.getInt("id"), rs.getString("nom"), rs.getString("email"));
            }
        }
        return user;
    }

    public List<User> selectAllUsers() throws SQLException {
        List<User> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new User(rs.getInt("id"), rs.getString("nom"), rs.getString("email")));
            }
        }
        return list;
    }

    public boolean deleteUser(int id) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateUser(User user) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
            ps.setString(1, user.getNom());
            ps.setString(2, user.getEmail());
            ps.setInt(3, user.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public List<User> searchByName(String namePattern) throws SQLException {
        List<User> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SEARCH_BY_NAME)) {
            ps.setString(1, "%" + namePattern + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new User(rs.getInt("id"), rs.getString("nom"), rs.getString("email")));
                }
            }
        }
        return list;
    }
}