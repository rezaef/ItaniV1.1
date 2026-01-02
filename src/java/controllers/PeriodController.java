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

import dao.PeriodDAO;
import models.Period;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.time.LocalDate;

@WebServlet(name="PeriodController", urlPatterns = {
        "/period/save",
        "/period/delete"
})
public class PeriodController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String ctx = req.getContextPath();
        String path = req.getServletPath();

        Object user = req.getSession().getAttribute("user");
        Integer userId = getUserId(user);
        boolean isAdmin = isAdmin(user);

        try {
            if ("/period/save".equals(path)) {
                String idStr = trim(req.getParameter("id"));
                String nama = trim(req.getParameter("nama_periode"));
                String mulai = trim(req.getParameter("tanggal_mulai"));
                String selesai = trim(req.getParameter("tanggal_selesai"));
                String deskripsi = trim(req.getParameter("deskripsi"));
                String status = trim(req.getParameter("status"));

                if (nama.isEmpty()) {
                    resp.sendRedirect(ctx + "/periods.jsp?err=Nama%20periode%20wajib%20diisi");
                    return;
                }
                if (!isAdmin && (userId == null || userId == 0)) {
                    resp.sendRedirect(ctx + "/periods.jsp?err=Session%20user%20tidak%20valid");
                    return;
                }

                Period p = new Period();
                p.setNamaPeriode(nama);
                p.setTanggalMulai(parseDate(mulai));
                p.setTanggalSelesai(parseDate(selesai));
                p.setDeskripsi(deskripsi);
                p.setStatus(status.isEmpty() ? "aktif" : status);

                PeriodDAO dao = new PeriodDAO();

                if (idStr.isEmpty()) {
                    p.setUserId(isAdmin ? (userId == null ? 1 : userId) : userId);
                    int newId = dao.insert(p);
                    resp.sendRedirect(ctx + "/periods.jsp?msg=Periode%20dibuat%20(ID%20" + newId + ")");
                } else {
                    p.setId(Integer.parseInt(idStr));
                    boolean ok = dao.update(p, userId, isAdmin);
                    resp.sendRedirect(ctx + "/periods.jsp?" + (ok ? "msg=Periode%20diupdate" : "err=Update%20ditolak/ID%20tidak%20ditemukan"));
                }
                return;
            }

            if ("/period/delete".equals(path)) {
                int id = Integer.parseInt(req.getParameter("id"));
                boolean ok = new PeriodDAO().delete(id, userId, isAdmin);
                resp.sendRedirect(ctx + "/periods.jsp?" + (ok ? "msg=Periode%20dihapus" : "err=Hapus%20ditolak/ID%20tidak%20ditemukan"));
                return;
            }

            resp.setStatus(404);
        } catch (Exception e) {
            resp.sendRedirect(ctx + "/periods.jsp?err=" + urlSafe("Gagal: " + e.getMessage()));
        }
    }

    private static String trim(String s){ return s==null?"":s.trim(); }

    // ✅ input type="date" -> yyyy-MM-dd
    private static Date parseDate(String s){
        if (s==null || s.trim().isEmpty()) return null;
        LocalDate d = LocalDate.parse(s.trim());
        return Date.valueOf(d);
    }

    private static String urlSafe(String s){
        if (s==null) return "";
        return s.replace(" ", "%20");
    }

    private static boolean isAdmin(Object user){
        String role = invokeString(user, "getRole");
        return role != null && role.equalsIgnoreCase("Admin");
    }

    private static Integer getUserId(Object user){
        if (user == null) return null;

        Integer v;
        v = invokeInt(user, "getId");        if (v != null) return v;
        v = invokeInt(user, "getUserId");    if (v != null) return v;
        v = invokeInt(user, "getIdUser");    if (v != null) return v;
        v = invokeInt(user, "getIduser");    if (v != null) return v;

        return null;
    }

    private static Integer invokeInt(Object obj, String method){
        try {
            Method m = obj.getClass().getMethod(method);
            Object r = m.invoke(obj);
            if (r instanceof Number) return ((Number) r).intValue();
        } catch(Exception ignored){}
        return null;
    }

    private static String invokeString(Object obj, String method){
        try {
            Method m = obj.getClass().getMethod(method);
            Object r = m.invoke(obj);
            return r == null ? null : r.toString();
        } catch(Exception ignored){}
        return null;
    }
}
