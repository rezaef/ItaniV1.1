package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.SeedStockTransaction;

public class SeedStockTransactionDAO {

    public List<SeedStockTransaction> findByStockId(int stockId) {
        List<SeedStockTransaction> list = new ArrayList<>();
        String sql = "SELECT id, stock_id, type, qty, note, created_at " +
                     "FROM seed_stock_transactions WHERE stock_id=? ORDER BY id DESC";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, stockId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SeedStockTransaction t = new SeedStockTransaction();
                    t.setId(rs.getInt("id"));
                    t.setStockId(rs.getInt("stock_id"));
                    t.setType(rs.getString("type"));
                    t.setQty(rs.getInt("qty"));
                    t.setNote(rs.getString("note"));
                    t.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(t);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Rekam transaksi (IN/OUT) + update stock dalam 1 transaksi DB (atomic).
     * Return false kalau OUT melebihi stock saat ini.
     */
    public boolean recordTransaction(int stockId, String type, int qty, String note) {
        if (qty <= 0) return false;
        type = (type == null ? "" : type.trim().toUpperCase());
        if (!type.equals("IN") && !type.equals("OUT")) return false;

        String lockSql = "SELECT stock FROM seed_stock WHERE id=? FOR UPDATE";
        String insertTx = "INSERT INTO seed_stock_transactions (stock_id, type, qty, note, created_at) " +
                          "VALUES (?,?,?,?, NOW())";
        String updateStock = "UPDATE seed_stock SET stock=?, updated_at=NOW() WHERE id=?";

        try (Connection c = DB.getConnection()) {
            c.setAutoCommit(false);

            int currentStock = 0;
            try (PreparedStatement lock = c.prepareStatement(lockSql)) {
                lock.setInt(1, stockId);
                try (ResultSet rs = lock.executeQuery()) {
                    if (!rs.next()) {
                        c.rollback();
                        return false;
                    }
                    currentStock = rs.getInt("stock");
                }
            }

            int newStock = currentStock;
            if (type.equals("IN")) newStock = currentStock + qty;
            else {
                // OUT
                if (currentStock < qty) {
                    c.rollback();
                    return false;
                }
                newStock = currentStock - qty;
            }

            try (PreparedStatement psTx = c.prepareStatement(insertTx);
                 PreparedStatement psUpd = c.prepareStatement(updateStock)) {

                psTx.setInt(1, stockId);
                psTx.setString(2, type);
                psTx.setInt(3, qty);
                psTx.setString(4, note);
                psTx.executeUpdate();

                psUpd.setInt(1, newStock);
                psUpd.setInt(2, stockId);
                psUpd.executeUpdate();

                c.commit();
                return true;
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
