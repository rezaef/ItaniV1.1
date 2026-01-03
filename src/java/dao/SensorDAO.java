/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package dao;

/**
 *
 * @author rezaef
 */

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SensorDAO {

    public boolean insertReading(Double ph, Double soilMoisture, Double soilTemp,
                                 Double ec, Integer n, Integer p, Integer k) {
        String sql = "INSERT INTO sensor_readings (ph, soil_moisture, soil_temp, ec, n, p, k, reading_time) "
                   + "VALUES (?,?,?,?,?,?,?, NOW())";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            setDoubleOrNull(ps, 1, ph);
            setDoubleOrNull(ps, 2, soilMoisture);
            setDoubleOrNull(ps, 3, soilTemp);
            setDoubleOrNull(ps, 4, ec);

            setIntOrNull(ps, 5, n);
            setIntOrNull(ps, 6, p);
            setIntOrNull(ps, 7, k);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, Object> latest() {
        String sql = "SELECT ph, soil_moisture, soil_temp, ec, n, p, k, reading_time "
                   + "FROM sensor_readings ORDER BY id DESC LIMIT 1";
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                Map<String, Object> m = new HashMap<>();
                m.put("ph", rs.getObject("ph"));
                m.put("soil_moisture", rs.getObject("soil_moisture"));
                m.put("soil_temp", rs.getObject("soil_temp"));
                m.put("ec", rs.getObject("ec"));
                m.put("n", rs.getObject("n"));
                m.put("p", rs.getObject("p"));
                m.put("k", rs.getObject("k"));
                m.put("reading_time", rs.getTimestamp("reading_time"));
                return m;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Ambil histori pembacaan sensor terbaru.
     * Data dikembalikan dalam urutan lama -> baru (ascending), agar enak dipakai chart.
     */
    public List<Map<String, Object>> history(int limit) {
        if (limit <= 0) limit = 30;
        if (limit > 500) limit = 500;

        String sql = "SELECT ph, soil_moisture, soil_temp, ec, n, p, k, reading_time "
                   + "FROM sensor_readings ORDER BY id DESC LIMIT ?";

        List<Map<String, Object>> rowsDesc = new ArrayList<>();
        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("ph", rs.getObject("ph"));
                    m.put("soil_moisture", rs.getObject("soil_moisture"));
                    m.put("soil_temp", rs.getObject("soil_temp"));
                    m.put("ec", rs.getObject("ec"));
                    m.put("n", rs.getObject("n"));
                    m.put("p", rs.getObject("p"));
                    m.put("k", rs.getObject("k"));
                    m.put("reading_time", rs.getTimestamp("reading_time"));
                    rowsDesc.add(m);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // balik urutan (ascending)
        List<Map<String, Object>> asc = new ArrayList<>();
        for (int i = rowsDesc.size() - 1; i >= 0; i--) asc.add(rowsDesc.get(i));
        return asc;
    }

    private void setDoubleOrNull(PreparedStatement ps, int idx, Double v) throws SQLException {
        if (v == null) ps.setNull(idx, Types.DOUBLE);
        else ps.setDouble(idx, v);
    }

    private void setIntOrNull(PreparedStatement ps, int idx, Integer v) throws SQLException {
        if (v == null) ps.setNull(idx, Types.INTEGER);
        else ps.setInt(idx, v);
    }
}
