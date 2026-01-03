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

import models.Harvest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HarvestDAO {

    /**
     * Ambil data panen terbaru lintas periode.
     * Untuk Petani: dibatasi oleh periods.user_id.
     */
    public List<Harvest> listLatest(int limit, Integer userId, boolean isAdmin) {
        List<Harvest> out = new ArrayList<>();
        String sql =
            "SELECT h.id, h.periode_id, h.tanggal_panen, h.jenis_tanaman, h.jumlah_panen, h.catatan, h.created_at, h.updated_at " +
            "FROM harvests h " +
            "JOIN periods p ON p.id = h.periode_id " +
            (isAdmin ? "" : "WHERE p.user_id = ? ") +
            "ORDER BY h.tanggal_panen DESC, h.id DESC LIMIT ?";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            int idx = 1;
            if (!isAdmin) ps.setInt(idx++, userId == null ? 0 : userId);
            ps.setInt(idx, Math.max(1, Math.min(limit, 200)));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    public List<Harvest> listByPeriode(int periodeId) {
        List<Harvest> out = new ArrayList<>();
        String sql =
            "SELECT id, periode_id, tanggal_panen, jenis_tanaman, jumlah_panen, catatan, created_at, updated_at " +
            "FROM harvests WHERE periode_id=? " +
            "ORDER BY tanggal_panen DESC, id DESC";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, periodeId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    public int insert(Harvest h) {
        String sql =
            "INSERT INTO harvests(periode_id, tanggal_panen, jenis_tanaman, jumlah_panen, catatan) " +
            "VALUES(?,?,?,?,?)";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, h.getPeriodeId());
            ps.setDate(2, h.getTanggalPanen());
            ps.setString(3, h.getJenisTanaman());
            ps.setDouble(4, h.getJumlahPanen());
            ps.setString(5, h.getCatatan());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM harvests WHERE id=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Harvest map(ResultSet rs) throws SQLException {
        Harvest h = new Harvest();
        h.setId(rs.getInt("id"));
        h.setPeriodeId(rs.getInt("periode_id"));
        h.setTanggalPanen(rs.getDate("tanggal_panen"));
        h.setJenisTanaman(rs.getString("jenis_tanaman"));
        h.setJumlahPanen(rs.getDouble("jumlah_panen"));
        h.setCatatan(rs.getString("catatan"));
        h.setCreatedAt(rs.getTimestamp("created_at"));
        h.setUpdatedAt(rs.getTimestamp("updated_at"));
        return h;
    }
}
