package org.example.server;

import java.sql.*;
import java.util.ArrayList;

public class Database {
    private Connection connection;

    public Database() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat_project", "root", "root");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkConnection(String user, String pass) {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
            ps.setString(1, user);
            ps.setString(2, pass);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            return false;
        }
    }

    public void saveMessage(String sender, String receiver, String content) {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO messages (sender, receiver, content, timestamp) VALUES (?, ?, ?, NOW())");
            ps.setString(1, sender);
            ps.setString(2, receiver);
            ps.setString(3, content);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getHistory(String user1, String user2) {
        StringBuilder sb = new StringBuilder();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT sender, content, timestamp FROM messages WHERE (sender=? AND receiver=?) OR (sender=? AND receiver=?) ORDER BY timestamp ASC");
            ps.setString(1, user1);
            ps.setString(2, user2);
            ps.setString(3, user2);
            ps.setString(4, user1);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                sb.append(rs.getString("sender")).append(": ").append(rs.getString("content")).append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
