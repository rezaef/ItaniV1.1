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
import dao.HarvestDAO;
import dao.PeriodDAO;
import models.Harvest;
import models.Period;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@WebServlet(name="HarvestController", urlPatterns = {
        "/harvest/add",
        "/harvest/delete"
})
public class HarvestController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String ctx = req.getContextPath();
        String path = req.getServletPath();

        Object user = req.getSession().getAttribute("user");
        Integer userId = getUserId(user);
        boolean isAdmin = isAdmin(user);

        try {
            if ("/harvest/add".equals(path)) {
                int periodeId = Integer.parseInt(req.getParameter("periode_id"));

                // cek kepemilikan periode (petani hanya boleh akses periodenya sendiri)
                Period p = new PeriodDAO().findByIdWithStats(periodeId, userId, isAdmin);
                if (p == null) {
                    resp.sendRedirect(ctx + "/harvests.jsp?periode_id=" + periodeId + "&err=Akses%20ditolak/Periode%20tidak%20ditemukan");
                    return;
                }

                String tanggalPanen = trim(req.getParameter("tanggal_panen"));
                String jenisTanaman = trim(req.getParameter("jenis_tanaman"));
                String jumlahPanen = trim(req.getParameter("jumlah_panen"));
                String catatan = trim(req.getParameter("catatan"));

                if (jenisTanaman.isEmpty()) {
                    resp.sendRedirect(ctx + "/harvests.jsp?periode_id=" + periodeId + "&err=Jenis%20tanaman%20wajib%20diisi");
                    return;
                }

                Harvest h = new Harvest();
                h.setPeriodeId(periodeId);
                h.setTanggalPanen(parseDT(tanggalPanen));
                h.setJenisTanaman(jenisTanaman);
                h.setJumlahPanen(Double.parseDouble(jumlahPanen));
                h.setCatatan(catatan);

                new HarvestDAO().insert(h);
                resp.sendRedirect(ctx + "/harvests.jsp?periode_id=" + periodeId + "&msg=Panen%20ditambahkan");
                return;
            }

            if ("/harvest/delete".equals(path)) {
                int id = Integer.parseInt(req.getParameter("id"));
                int periodeId = Integer.parseInt(req.getParameter("periode_id"));

                // cek kepemilikan periode dulu
                Period p = new PeriodDAO().findByIdWithStats(periodeId, userId, isAdmin);
                if (p == null) {
                    resp.sendRedirect(ctx + "/harvests.jsp?periode_id=" + periodeId + "&err=Akses%20ditolak");
                    return;
                }

                new HarvestDAO().delete(id);
                resp.sendRedirect(ctx + "/harvests.jsp?periode_id=" + periodeId + "&msg=Panen%20dihapus");
                return;
            }

            resp.setStatus(404);
        } catch (Exception e) {
            String pid = req.getParameter("periode_id");
            if (pid == null) pid = "0";
            resp.sendRedirect(ctx + "/harvests.jsp?periode_id=" + pid + "&err=" + urlSafe("Gagal: " + e.getMessage()));
        }
    }

    private static String trim(String s){ return s==null?"":s.trim(); }

    private static Timestamp parseDT(String s){
        if (s==null || s.trim().isEmpty()) return null;
        LocalDateTime ldt = LocalDateTime.parse(s.trim());
        return Timestamp.valueOf(ldt);
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
