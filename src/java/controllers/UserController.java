/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controllers;

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

import dao.UserDAO;
import models.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Kelola User (Admin only)
 * - POST /user/save    : insert/update meta, optional reset password
 * - POST /user/delete  : delete user
 */
@WebServlet(name = "UserController", urlPatterns = {
        "/user/save",
        "/user/delete"
})
public class UserController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String ctx = req.getContextPath();
        String path = req.getServletPath();

        HttpSession s = req.getSession(false);
        if (s == null || s.getAttribute("user") == null) {
            resp.sendRedirect(ctx + "/index.jsp");
            return;
        }
        User me = (User) s.getAttribute("user");
        if (me == null || !"Admin".equalsIgnoreCase(me.getRole())) {
            resp.sendRedirect(ctx + "/dashboard.jsp?err=" + enc("forbidden"));
            return;
        }

        try {
            if ("/user/save".equals(path)) {
                handleSave(req, resp, me);
                return;
            }
            if ("/user/delete".equals(path)) {
                handleDelete(req, resp, me);
                return;
            }
            resp.setStatus(404);
        } catch (Exception e) {
            resp.sendRedirect(ctx + "/users.jsp?err=" + enc("Gagal: " + e.getMessage()));
        }
    }

    private void handleSave(HttpServletRequest req, HttpServletResponse resp, User me) throws IOException {
        String ctx = req.getContextPath();

        String idStr = trim(req.getParameter("id"));
        String name = trim(req.getParameter("name"));
        String username = trim(req.getParameter("username"));
        String role = trim(req.getParameter("role"));
        String password = trim(req.getParameter("password"));

        if (name.isEmpty() || username.isEmpty() || role.isEmpty()) {
            resp.sendRedirect(ctx + "/users.jsp?err=" + enc("Nama/Username/Role wajib diisi"));
            return;
        }
        if (!"Admin".equalsIgnoreCase(role) && !"Petani".equalsIgnoreCase(role)) {
            role = "Petani";
        }

        UserDAO dao = new UserDAO();

        // insert
        if (idStr.isEmpty()) {
            if (password.isEmpty()) {
                resp.sendRedirect(ctx + "/users.jsp?err=" + enc("Password wajib untuk user baru"));
                return;
            }
            int newId = dao.insert(name, username, role, password);
            if (newId > 0) {
                resp.sendRedirect(ctx + "/users.jsp?msg=" + enc("User dibuat (ID " + newId + ")"));
            } else {
                resp.sendRedirect(ctx + "/users.jsp?err=" + enc("Gagal membuat user"));
            }
            return;
        }

        // update
        int id = Integer.parseInt(idStr);
        boolean okMeta = dao.updateMeta(id, name, username, role);
        if (!password.isEmpty()) {
            dao.resetPassword(id, password);
        }
        if (okMeta) {
            resp.sendRedirect(ctx + "/users.jsp?msg=" + enc("User diperbarui"));
        } else {
            resp.sendRedirect(ctx + "/users.jsp?err=" + enc("User tidak ditemukan"));
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp, User me) throws IOException {
        String ctx = req.getContextPath();

        String idStr = trim(req.getParameter("id"));
        if (idStr.isEmpty()) {
            resp.sendRedirect(ctx + "/users.jsp?err=" + enc("ID wajib"));
            return;
        }
        int id = Integer.parseInt(idStr);
        if (me.getId() == id) {
            resp.sendRedirect(ctx + "/users.jsp?err=" + enc("Tidak bisa menghapus akun yang sedang login"));
            return;
        }
        boolean ok = new UserDAO().delete(id);
        if (ok) {
            resp.sendRedirect(ctx + "/users.jsp?msg=" + enc("User dihapus"));
        } else {
            resp.sendRedirect(ctx + "/users.jsp?err=" + enc("User tidak ditemukan"));
        }
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private static String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}
