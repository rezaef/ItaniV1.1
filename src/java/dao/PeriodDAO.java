/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package dao;

/**
 *
 * @author rezaef
 */

import models.Period;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeriodDAO {

    public List<Period> findAllWithStats(Integer userId, boolean isAdmin) {
        List<Period> out = new ArrayList<>();

        // Catatan: Data periode bersifat GLOBAL (bukan per-user).
        // Kolom user_id tetap disimpan sebagai "pembuat" (audit), tetapi tidak dipakai untuk filter akses.
        String sql =
            "SELECT p.id, p.user_id, p.nama_periode, p.tanggal_mulai, p.tanggal_selesai, p.deskripsi, p.status, p.created_at, p.updated_at, " +
            "       COUNT(h.id) AS harvest_count, COALESCE(SUM(h.jumlah_panen),0) AS harvest_total " +
            "FROM periods p " +
            "LEFT JOIN harvests h ON h.periode_id = p.id " +
            "" +
            "GROUP BY p.id, p.user_id, p.nama_periode, p.tanggal_mulai, p.tanggal_selesai, p.deskripsi, p.status, p.created_at, p.updated_at " +
            "ORDER BY p.id DESC";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Period p = map(rs);
                    p.setHarvestCount(rs.getInt("harvest_count"));
                    p.setHarvestTotal(rs.getDouble("harvest_total"));
                    out.add(p);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return out;
    }

    public Period findByIdWithStats(int id, Integer userId, boolean isAdmin) {
        String sql =
            "SELECT p.id, p.user_id, p.nama_periode, p.tanggal_mulai, p.tanggal_selesai, p.deskripsi, p.status, p.created_at, p.updated_at, " +
            "       COUNT(h.id) AS harvest_count, COALESCE(SUM(h.jumlah_panen),0) AS harvest_total " +
            "FROM periods p " +
            "LEFT JOIN harvests h ON h.periode_id = p.id " +
            "WHERE p.id = ? " +
            "" +
            "GROUP BY p.id, p.user_id, p.nama_periode, p.tanggal_mulai, p.tanggal_selesai, p.deskripsi, p.status, p.created_at, p.updated_at " +
            "LIMIT 1";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Period p = map(rs);
                p.setHarvestCount(rs.getInt("harvest_count"));
                p.setHarvestTotal(rs.getDouble("harvest_total"));
                return p;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int insert(Period p) {
        String sql =
            "INSERT INTO periods(user_id, nama_periode, tanggal_mulai, tanggal_selesai, deskripsi, status) " +
            "VALUES(?,?,?,?,?,?)";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getUserId());
            ps.setString(2, p.getNamaPeriode());
            ps.setDate(3, p.getTanggalMulai());
            ps.setDate(4, p.getTanggalSelesai());
            ps.setString(5, p.getDeskripsi());
            ps.setString(6, (p.getStatus() == null || p.getStatus().isEmpty()) ? "aktif" : p.getStatus());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean update(Period p, Integer userId, boolean isAdmin) {
        String sql =
            "UPDATE periods SET nama_periode=?, tanggal_mulai=?, tanggal_selesai=?, deskripsi=?, status=? " +
            "WHERE id=?";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, p.getNamaPeriode());
            ps.setDate(2, p.getTanggalMulai());
            ps.setDate(3, p.getTanggalSelesai());
            ps.setString(4, p.getDeskripsi());
            ps.setString(5, p.getStatus());
            ps.setInt(6, p.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(int id, Integer userId, boolean isAdmin) {
        String sql = "DELETE FROM periods WHERE id=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Period map(ResultSet rs) throws SQLException {
        Period p = new Period();
        p.setId(rs.getInt("id"));
        p.setUserId(rs.getInt("user_id"));
        p.setNamaPeriode(rs.getString("nama_periode"));
        p.setTanggalMulai(rs.getDate("tanggal_mulai"));
        p.setTanggalSelesai(rs.getDate("tanggal_selesai"));
        p.setDeskripsi(rs.getString("deskripsi"));
        p.setStatus(rs.getString("status"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        p.setUpdatedAt(rs.getTimestamp("updated_at"));
        return p;
    }
}
