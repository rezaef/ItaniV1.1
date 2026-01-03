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
import models.WateringLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WateringLogDAO {

    public boolean insert(String mode, String action, String source, String note) {
        String sql = "INSERT INTO watering_logs(mode, action, source, note, created_at) VALUES(?,?,?,?,NOW())";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, mode);
            ps.setString(2, action);
            ps.setString(3, source);
            ps.setString(4, note);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("[WateringLogDAO] insert error: " + e.getMessage());
            return false;
        }
    }

    public List<WateringLog> listLatest(int limit) {
        List<WateringLog> out = new ArrayList<>();
        String sql = "SELECT id, mode, action, source, note, created_at " +
                     "FROM watering_logs ORDER BY id DESC LIMIT ?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new WateringLog(
                            rs.getInt("id"),
                            rs.getString("mode"),
                            rs.getString("action"),
                            rs.getString("source"),
                            rs.getString("note"),
                            rs.getTimestamp("created_at")
                    ));
                }
            }
        } catch (Exception e) {
            System.out.println("[WateringLogDAO] list error: " + e.getMessage());
        }
        return out;
    }
}
