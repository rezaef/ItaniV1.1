/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package dao;
/**
 *
 * @author rezaef
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    // Ganti sesuai MySQL kamu
    private static final String URL =
            "jdbc:mysql://localhost:3306/itaniJav_db?useSSL=false&serverTimezone=Asia/Jakarta";
    private static final String USER = "root";
    private static final String PASS = ""; // isi kalau root kamu ada password

    static {
        try {
            // mysql-connector-j 8+ pakai class ini
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL Driver tidak ditemukan. Pastikan mysql-connector sudah masuk Libraries.", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
