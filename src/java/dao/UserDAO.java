/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package dao;


/**
 *
 * @author rezaef
 */

import models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO User.
 *
 * - login(): autentikasi username + password (password disimpan SHA2-256 di DB)
 * - CRUD: untuk fitur Kelola User (Admin only)
 */
public class UserDAO {

    /**
     * Login: cocokkan username + SHA2(password,256)
     */
    public User login(String username, String passwordPlain) {
        String sql = "SELECT id, name, username, role "
                + "FROM users "
                + "WHERE username=? AND password_hash = SHA2(?, 256) "
                + "LIMIT 1";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, passwordPlain);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("username"),
                            rs.getString("role")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> findAll() {
        List<User> out = new ArrayList<>();
        String sql = "SELECT id, name, username, role FROM users ORDER BY id DESC";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    public User findById(int id) {
        String sql = "SELECT id, name, username, role FROM users WHERE id=? LIMIT 1";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("role")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int insert(String name, String username, String role, String passwordPlain) {
        String sql = "INSERT INTO users(name, username, role, password_hash) VALUES(?,?,?, SHA2(?, 256))";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, username);
            ps.setString(3, role);
            ps.setString(4, passwordPlain);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateMeta(int id, String name, String username, String role) {
        String sql = "UPDATE users SET name=?, username=?, role=? WHERE id=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, username);
            ps.setString(3, role);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean resetPassword(int id, String passwordPlain) {
        String sql = "UPDATE users SET password_hash=SHA2(?,256) WHERE id=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, passwordPlain);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
