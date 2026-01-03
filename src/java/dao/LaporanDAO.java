/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author rezaef
 */

import models.Laporan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LaporanDAO {

    public int insert(Integer userId, String isiLaporan) {
        String sql = "INSERT INTO laporan(user_id, isi_laporan, tanggal_laporan) VALUES(?,?, NOW())";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (userId == null) ps.setNull(1, Types.INTEGER);
            else ps.setInt(1, userId);
            ps.setString(2, isiLaporan);

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Laporan findById(int id, Integer userId, boolean isAdmin) {
        String sql = "SELECT l.id, l.user_id, l.tanggal_laporan, l.isi_laporan, u.name AS dibuat_oleh "
                + "FROM laporan l LEFT JOIN users u ON u.id = l.user_id "
                + "WHERE l.id=? " + (isAdmin ? "" : "AND l.user_id=?");
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            if (!isAdmin) ps.setInt(2, userId == null ? 0 : userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Laporan> listLatest(int limit, Integer userId, boolean isAdmin) {
        List<Laporan> out = new ArrayList<>();
        String sql = "SELECT l.id, l.user_id, l.tanggal_laporan, l.isi_laporan, u.name AS dibuat_oleh "
                + "FROM laporan l LEFT JOIN users u ON u.id = l.user_id "
                + (isAdmin ? "" : "WHERE l.user_id=? ")
                + "ORDER BY l.id DESC LIMIT ?";

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

    private Laporan map(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        Timestamp t = rs.getTimestamp("tanggal_laporan");

        Laporan l = new Laporan();
        l.setIdLaporan(String.format("LAP-%04d", id));
        l.setTanggalLaporan(t == null ? "" : String.valueOf(t));
        l.setIsiLaporan(rs.getString("isi_laporan"));
        try {
            l.setUserId((Integer) rs.getObject("user_id"));
        } catch (Exception ignored) {}
        try {
            l.setDibuatOleh(rs.getString("dibuat_oleh"));
        } catch (Exception ignored) {}
        return l;
    }
}
