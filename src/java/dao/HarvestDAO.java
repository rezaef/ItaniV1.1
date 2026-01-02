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
