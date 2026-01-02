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

import models.FertilizerStockTransaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FertilizerStockTransactionDAO {

    public List<FertilizerStockTransaction> listByStock(int stockId) {
        List<FertilizerStockTransaction> out = new ArrayList<>();
        String sql = "SELECT id, stock_id, type, qty, note, created_at " +
                     "FROM fertilizer_stock_transactions WHERE stock_id=? ORDER BY id DESC";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, stockId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FertilizerStockTransaction t = new FertilizerStockTransaction();
                    t.setId(rs.getInt("id"));
                    t.setStockId(rs.getInt("stock_id"));
                    t.setType(rs.getString("type"));
                    t.setQty(rs.getDouble("qty"));
                    t.setNote(rs.getString("note"));
                    t.setCreatedAt(rs.getTimestamp("created_at"));
                    out.add(t);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    // transaksi + update stok (anti minus) dalam 1 transaksi DB
    public void createTx(int stockId, String type, double qty, String note) {
        String lockSql = "SELECT stock FROM fertilizer_stock WHERE id=? FOR UPDATE";
        String insSql  = "INSERT INTO fertilizer_stock_transactions(stock_id, type, qty, note) VALUES(?,?,?,?)";
        String updSql  = "UPDATE fertilizer_stock SET stock=? WHERE id=?";

        try (Connection c = DB.getConnection()) {
            c.setAutoCommit(false);

            double current;
            try (PreparedStatement ps = c.prepareStatement(lockSql)) {
                ps.setInt(1, stockId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) throw new SQLException("Stock item tidak ditemukan");
                    current = rs.getDouble("stock");
                }
            }

            double next = current;
            if ("IN".equalsIgnoreCase(type)) next = current + qty;
            else if ("OUT".equalsIgnoreCase(type)) {
                next = current - qty;
                if (next < 0) throw new SQLException("Stok tidak cukup (akan menjadi minus)");
            } else {
                throw new SQLException("Type harus IN atau OUT");
            }

            try (PreparedStatement ps = c.prepareStatement(insSql)) {
                ps.setInt(1, stockId);
                ps.setString(2, type.toUpperCase());
                ps.setDouble(3, qty);
                ps.setString(4, note);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = c.prepareStatement(updSql)) {
                ps.setDouble(1, next);
                ps.setInt(2, stockId);
                ps.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
