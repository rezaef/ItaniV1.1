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

import dao.NotifikasiUserDAO;
import models.NotifikasiUser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name="NotifikasiController", urlPatterns = {
        "/api/notifications/latest",
        "/api/notifications/markAllRead",
        "/api/notifications/markRead"
})
public class NotifikasiController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        resp.setContentType("application/json; charset=UTF-8");

        if ("/api/notifications/latest".equals(path)) {
            // Optional: require login (dashboard pakai session)
            if (!isLoggedIn(req)) {
                resp.setStatus(401);
                resp.getWriter().write("{\"ok\":false,\"message\":\"unauthorized\"}");
                return;
            }

            int limit = parseInt(req.getParameter("limit"), 8);
            limit = Math.max(1, Math.min(limit, 50));

            NotifikasiUserDAO dao = new NotifikasiUserDAO();
            int unread = dao.countUnread();
            List<NotifikasiUser> list = dao.listLatest(limit);

            StringBuilder sb = new StringBuilder();
            sb.append("{\"ok\":true,");
            sb.append("\"unread\":").append(unread).append(",");
            sb.append("\"data\":[");

            for (int i = 0; i < list.size(); i++) {
                NotifikasiUser n = list.get(i);
                if (i > 0) sb.append(",");
                sb.append("{");
                sb.append("\"id\":").append(n.getId()).append(",");
                sb.append("\"ruleKey\":\"").append(esc(n.getRuleKey())).append("\",");
                sb.append("\"sensorKey\":\"").append(esc(n.getSensorKey())).append("\",");
                sb.append("\"level\":\"").append(esc(n.getLevel())).append("\",");
                sb.append("\"message\":\"").append(esc(n.getPesan())).append("\",");
                sb.append("\"value\":").append(n.getValue() == null ? "null" : n.getValue()).append(",");
                sb.append("\"status\":\"").append(esc(n.getStatus())).append("\",");
                sb.append("\"source\":\"").append(esc(n.getSource())).append("\",");
                sb.append("\"createdAt\":\"").append(esc(String.valueOf(n.getCreatedAt()))).append("\"");
                sb.append("}");
            }

            sb.append("]}");
            resp.getWriter().write(sb.toString());
            return;
        }

        resp.setStatus(404);
        resp.getWriter().write("{\"ok\":false,\"message\":\"not found\"}");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        resp.setContentType("application/json; charset=UTF-8");

        if (!isLoggedIn(req)) {
            resp.setStatus(401);
            resp.getWriter().write("{\"ok\":false,\"message\":\"unauthorized\"}");
            return;
        }

        NotifikasiUserDAO dao = new NotifikasiUserDAO();

        if ("/api/notifications/markAllRead".equals(path)) {
            boolean ok = dao.markAllRead();
            resp.getWriter().write("{\"ok\":" + ok + ",\"message\":\"marked\"}");
            return;
        }

        if ("/api/notifications/markRead".equals(path)) {
            int id = parseInt(req.getParameter("id"), -1);
            if (id <= 0) {
                resp.setStatus(400);
                resp.getWriter().write("{\"ok\":false,\"message\":\"id required\"}");
                return;
            }
            boolean ok = dao.markRead(id);
            resp.getWriter().write("{\"ok\":" + ok + ",\"message\":\"marked\"}");
            return;
        }

        resp.setStatus(404);
        resp.getWriter().write("{\"ok\":false,\"message\":\"not found\"}");
    }

    private static boolean isLoggedIn(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s != null && s.getAttribute("user") != null;
    }

    private static int parseInt(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
    }
}
