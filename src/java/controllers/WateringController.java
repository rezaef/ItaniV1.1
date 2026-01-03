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

import dao.WateringLogDAO;
import utils.MqttSubscriber;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name="WateringController", urlPatterns = {
        "/api/watering/log"
})
public class WateringController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");

        String action = upper(req.getParameter("action")); // ON/OFF
        String mode   = upper(req.getParameter("mode"));   // MANUAL/AUTO
        String source = upper(req.getParameter("source")); // WEB/DEVICE/RULE
        String note   = req.getParameter("note");

        // default
        if (mode == null) mode = "MANUAL";
        if (source == null) source = "WEB";
        if (note == null) note = "";

        if (!"ON".equals(action) && !"OFF".equals(action)) {
            respond(req, resp, false, "Action harus ON/OFF");
            return;
        }

        boolean dbOk = new WateringLogDAO().insert(mode, action, source, note);

        // publish MQTT (walau ESP mati, publish tetap bisa sukses ke broker)
        boolean mqttOk = false;
        try {
            mqttOk = MqttSubscriber.sendPumpCmd(action);
        } catch (Exception e) {
            mqttOk = false;
        }

        if (dbOk && mqttOk) {
            respond(req, resp, true, "Berhasil log & kirim cmd " + action);
        } else if (dbOk) {
            respond(req, resp, true, "Log tersimpan, tapi publish MQTT gagal");
        } else {
            respond(req, resp, false, "Gagal simpan log");
        }
    }

    private void respond(HttpServletRequest req, HttpServletResponse resp, boolean ok, String msg) throws IOException {
    String redirect = req.getParameter("redirect");
    if ("1".equals(redirect)) {
        String q = ok ? "msg=" : "err=";

        // kalau ada param next, pakai itu
        String next = req.getParameter("next");
        if (next != null && !next.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + next + "?" + q + urlEnc(msg));
            return;
        }

        // fallback: balik ke halaman yang submit
        String ref = req.getHeader("Referer");
        if (ref != null && !ref.isEmpty()) {
            String sep = ref.contains("?") ? "&" : "?";
            resp.sendRedirect(ref + sep + q + urlEnc(msg));
            return;
        }

        // fallback terakhir
        resp.sendRedirect(req.getContextPath() + "/dashboard.jsp?" + q + urlEnc(msg));
        return;
    }

        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write("{\"ok\":" + ok + ",\"message\":\"" + esc(msg) + "\"}");
    }
    
    private String upper(String s) {
        return (s == null) ? null : s.trim().toUpperCase();
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
    private String urlEnc(String s) {
        try { return java.net.URLEncoder.encode(s, "UTF-8"); }
        catch (Exception e) { return s; }
    }
}
