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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import models.User;

public class UserDAO {

    // login: cocokkan username + SHA2(password,256)
    public User login(String username, String passwordPlain) {
        String sql = "SELECT id, name, username, role "
                   + "FROM users "
                   + "WHERE username=? AND password_hash = SHA2(?, 256) "
                   + "LIMIT 1";

        try (Connection c = DB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, passwordPlain);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("username"),
                            rs.getString("role")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
