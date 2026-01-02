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
import models.FertilizerStock;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FertilizerStockDAO {

    public List<FertilizerStock> findAll() {
        List<FertilizerStock> out = new ArrayList<>();
        String sql = "SELECT id, name, unit, stock, updated_at FROM fertilizer_stock ORDER BY id DESC";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                FertilizerStock s = new FertilizerStock();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setUnit(rs.getString("unit"));
                s.setStock(rs.getDouble("stock"));
                s.setUpdatedAt(rs.getTimestamp("updated_at"));
                out.add(s);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    public FertilizerStock findById(int id) {
        String sql = "SELECT id, name, unit, stock, updated_at FROM fertilizer_stock WHERE id=? LIMIT 1";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                FertilizerStock s = new FertilizerStock();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setUnit(rs.getString("unit"));
                s.setStock(rs.getDouble("stock"));
                s.setUpdatedAt(rs.getTimestamp("updated_at"));
                return s;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int insert(String name, String unit, double stock) {
        String sql = "INSERT INTO fertilizer_stock(name, unit, stock) VALUES(?,?,?)";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, unit);
            ps.setDouble(3, stock);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean update(int id, String name, String unit) {
        String sql = "UPDATE fertilizer_stock SET name=?, unit=? WHERE id=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, unit);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM fertilizer_stock WHERE id=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
