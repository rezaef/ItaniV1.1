package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.SeedStock;

public class SeedStockDAO {

    public List<SeedStock> findAll() {
        List<SeedStock> list = new ArrayList<>();
        String sql = "SELECT id, name, unit, stock, updated_at FROM seed_stock ORDER BY id DESC";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SeedStock s = new SeedStock();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setUnit(rs.getString("unit"));
                s.setStock(rs.getInt("stock"));
                s.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public SeedStock findById(int id) {
        String sql = "SELECT id, name, unit, stock, updated_at FROM seed_stock WHERE id = ?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    SeedStock s = new SeedStock();
                    s.setId(rs.getInt("id"));
                    s.setName(rs.getString("name"));
                    s.setUnit(rs.getString("unit"));
                    s.setStock(rs.getInt("stock"));
                    s.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return s;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(String name, String unit, int stock) {
        String sql = "INSERT INTO seed_stock (name, unit, stock, updated_at) VALUES (?,?,?, NOW())";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, unit);
            ps.setInt(3, stock);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMeta(int id, String name, String unit) {
        String sql = "UPDATE seed_stock SET name=?, unit=?, updated_at=NOW() WHERE id=?";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, unit);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        // Hapus transaksi dulu (jika FK tidak cascade)
        String delTx = "DELETE FROM seed_stock_transactions WHERE stock_id=?";
        String del = "DELETE FROM seed_stock WHERE id=?";
        try (Connection c = DB.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement ps1 = c.prepareStatement(delTx);
                 PreparedStatement ps2 = c.prepareStatement(del)) {

                ps1.setInt(1, id);
                ps1.executeUpdate();

                ps2.setInt(1, id);
                int affected = ps2.executeUpdate();

                c.commit();
                return affected > 0;
            } catch (Exception ex) {
                c.rollback();
                ex.printStackTrace();
                return false;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
