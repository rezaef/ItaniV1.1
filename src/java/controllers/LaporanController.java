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

import dao.LaporanDAO;
import models.Laporan;
import models.RekapManajemen;
import models.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name="LaporanController", urlPatterns = {
        "/laporan/generate"
})
public class LaporanController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleGenerate(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleGenerate(req, resp);
    }

    private void handleGenerate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession s = req.getSession(false);
        if (s == null || s.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        User u = (User) s.getAttribute("user");
        boolean isAdmin = u != null && "Admin".equalsIgnoreCase(u.getRole());

        // Kebijakan proyek: semua user (Admin/Petani) boleh generate laporan miliknya.
        // Akses lihat laporan: Admin boleh lihat semua; Petani hanya laporannya sendiri.
        RekapManajemen rm = new RekapManajemen();
        rm.load(u == null ? null : u.getId(), isAdmin);
        Laporan lap = rm.buatLaporan(u, isAdmin);

        int id = new LaporanDAO().insert(u == null ? null : u.getId(), lap.getIsiLaporan());

        String next = req.getParameter("next");
        String redirect = req.getParameter("redirect");
        if ("1".equals(redirect) && next != null && !next.isEmpty()) {
            // arahkan ke halaman laporan dan auto buka laporan yang baru dibuat
            resp.sendRedirect(req.getContextPath() + next + "?ok=1&view=" + id);
            return;
        }

        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write("{\"ok\":true,\"id\":" + id + "}");
    }
}
