/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package dao;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author rezaef
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import models.NotifikasiUser;

/**
 * DAO untuk NotifikasiUser (sesuai UML).
 */
public class NotifikasiUserDAO {

    public boolean insert(String ruleKey, String sensorKey, String level,
                          String message, Double value, String source) {
        String sql = "INSERT INTO notifications (rule_key, sensor_key, level, message, value, status, source, created_at) "
                   + "VALUES (?,?,?,?,?, 'UNREAD', ?, NOW())";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, ruleKey);
            ps.setString(2, sensorKey);
            ps.setString(3, level);
            ps.setString(4, message);
            if (value == null) ps.setObject(5, null);
            else ps.setDouble(5, value);
            ps.setString(6, source);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cek apakah notifikasi dengan ruleKey sudah dibuat baru-baru ini.
     * Dipakai untuk anti-spam.
     */
    public boolean hasRecent(String ruleKey, int withinMinutes) {
        String sql = "SELECT id FROM notifications WHERE rule_key=? "
                   + "AND created_at >= (NOW() - INTERVAL ? MINUTE) LIMIT 1";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, ruleKey);
            ps.setInt(2, withinMinutes);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int countUnread() {
        String sql = "SELECT COUNT(*) AS c FROM notifications WHERE status='UNREAD'";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt("c");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<NotifikasiUser> listLatest(int limit) {
        String sql = "SELECT id, rule_key, sensor_key, level, message, value, status, source, created_at "
                   + "FROM notifications ORDER BY id DESC LIMIT ?";
        List<NotifikasiUser> out = new ArrayList<>();
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, Math.max(1, limit));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out;
    }

    public boolean markAllRead() {
        String sql = "UPDATE notifications SET status='READ' WHERE status='UNREAD'";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean markRead(int id) {
        String sql = "UPDATE notifications SET status='READ' WHERE id=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private NotifikasiUser mapRow(ResultSet rs) throws Exception {
        int id = rs.getInt("id");
        String ruleKey = rs.getString("rule_key");
        String sensorKey = rs.getString("sensor_key");
        String level = rs.getString("level");
        String message = rs.getString("message");
        Double value = (rs.getObject("value") == null) ? null : rs.getDouble("value");
        String status = rs.getString("status");
        String source = rs.getString("source");
        Timestamp createdAt = rs.getTimestamp("created_at");
        return new NotifikasiUser(id, ruleKey, sensorKey, level, message, value, status, source, createdAt);
    }
}
