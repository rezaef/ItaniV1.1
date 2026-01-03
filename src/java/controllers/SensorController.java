/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controllers;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author rezaef
 */
import dao.SensorDAO;
import utils.SensorNotifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name="SensorController", urlPatterns = {
        "/api/sensors/insert",
        "/api/sensors/latest",
        "/api/sensors/history"
})
public class SensorController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();
        resp.setContentType("application/json; charset=UTF-8");
        


        if ("/api/sensors/insert".equals(path)) {
            String body = readBody(req);

            // suport: JSON body ATAU form-data
            Double ph = pickDouble(req, body, "ph");
            Double ec = pickDouble(req, body, "ec");
            Integer n = pickInt(req, body, "n");
            Integer p = pickInt(req, body, "p");
            Integer k = pickInt(req, body, "k");

            // mapping temp/humi -> soil_temp/soil_moisture (biar kompatibel payload kamu)
            Double soilTemp = pickDouble(req, body, "soil_temp");
            if (soilTemp == null) soilTemp = pickDouble(req, body, "temp");

            Double soilMoist = pickDouble(req, body, "soil_moisture");
            if (soilMoist == null) soilMoist = pickDouble(req, body, "humi");

            boolean ok = new SensorDAO().insertReading(ph, soilMoist, soilTemp, ec, n, p, k);

            // Buat notifikasi WARNING/DANGER dari reading sensor
            if (ok) {
                try {
                    HttpSession s = req.getSession(false);
                    String src = (s != null && s.getAttribute("user") != null) ? "WEB" : "ESP";
                    SensorNotifier.onNewReading(ph, soilMoist, soilTemp, ec, n, p, k, src);
                } catch (Exception ex) {
                    // tidak ganggu response utama
                    System.out.println("[SENSOR] notify error: " + ex.getMessage());
                }
            }

            if (ok) resp.getWriter().write("{\"ok\":true,\"message\":\"inserted\"}");
            else {
                resp.setStatus(500);
                resp.getWriter().write("{\"ok\":false,\"message\":\"failed insert\"}");
            }
            return;
        }

        resp.setStatus(404);
        resp.getWriter().write("{\"ok\":false,\"message\":\"not found\"}");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();
        resp.setContentType("application/json; charset=UTF-8");

        if ("/api/sensors/latest".equals(path))  {
            Map<String, Object> m = new SensorDAO().latest();
            if (m == null) {
                resp.getWriter().write("{\"ok\":true,\"data\":null}");
                return;
            }

            String json = "{"
                    + "\"ok\":true,"
                    + "\"data\":{"
                    + "\"ph\":" + toJsonNumber(m.get("ph")) + ","
                    + "\"soil_moisture\":" + toJsonNumber(m.get("soil_moisture")) + ","
                    + "\"soil_temp\":" + toJsonNumber(m.get("soil_temp")) + ","
                    + "\"ec\":" + toJsonNumber(m.get("ec")) + ","
                    + "\"n\":" + toJsonNumber(m.get("n")) + ","
                    + "\"p\":" + toJsonNumber(m.get("p")) + ","
                    + "\"k\":" + toJsonNumber(m.get("k")) + ","
                    + "\"reading_time\":\"" + escapeJson(String.valueOf(m.get("reading_time"))) + "\""
                    + "}"
                    + "}";

            resp.getWriter().write(json);
            return;
        }

        if ("/api/sensors/history".equals(path)) {
            int limit = 30;
            try {
                String s = req.getParameter("limit");
                if (s != null && !s.isEmpty()) limit = Integer.parseInt(s);
            } catch (Exception ignored) {}

            java.util.List<java.util.Map<String, Object>> rows = new SensorDAO().history(limit);

            StringBuilder sb = new StringBuilder();
            sb.append("{\"ok\":true,\"limit\":").append(limit).append(",\"data\":[");
            for (int i = 0; i < rows.size(); i++) {
                java.util.Map<String, Object> r = rows.get(i);
                String ts = String.valueOf(r.get("reading_time"));
                // default Timestamp#toString: 2026-01-03 22:00:00.0 -> trim biar rapih
                if (ts != null && ts.length() >= 19) ts = ts.substring(0, 19);

                if (i > 0) sb.append(',');
                sb.append('{')
                  .append("\"ph\":").append(toJsonNumber(r.get("ph"))).append(',')
                  .append("\"soil_moisture\":").append(toJsonNumber(r.get("soil_moisture"))).append(',')
                  .append("\"soil_temp\":").append(toJsonNumber(r.get("soil_temp"))).append(',')
                  .append("\"ec\":").append(toJsonNumber(r.get("ec"))).append(',')
                  .append("\"n\":").append(toJsonNumber(r.get("n"))).append(',')
                  .append("\"p\":").append(toJsonNumber(r.get("p"))).append(',')
                  .append("\"k\":").append(toJsonNumber(r.get("k"))).append(',')
                  .append("\"reading_time\":\"").append(escapeJson(ts)).append("\"")
                  .append('}');
            }
            sb.append("]}");
            resp.getWriter().write(sb.toString());
            return;
        }

        resp.setStatus(404);
        resp.getWriter().write("{\"ok\":false,\"message\":\"not found\"}");
    }

    // ===== helpers =====
    private static String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = req.getReader()) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString().trim();
    }

    private static Double pickDouble(HttpServletRequest req, String body, String key) {
        String v = req.getParameter(key);
        if (v == null || v.isEmpty()) v = pickJsonValue(body, key);
        try { return (v == null || v.isEmpty()) ? null : Double.valueOf(v); }
        catch (Exception e) { return null; }
    }

    private static Integer pickInt(HttpServletRequest req, String body, String key) {
        String v = req.getParameter(key);
        if (v == null || v.isEmpty()) v = pickJsonValue(body, key);
        try { return (v == null || v.isEmpty()) ? null : Integer.valueOf(v); }
        catch (Exception e) { return null; }
    }

    // parser JSON super sederhana: cari "key":value
    private static String pickJsonValue(String body, String key) {
        if (body == null) return null;
        String pattern = "\"" + key + "\"";
        int i = body.indexOf(pattern);
        if (i < 0) return null;
        int colon = body.indexOf(":", i + pattern.length());
        if (colon < 0) return null;

        int start = colon + 1;
        while (start < body.length() && Character.isWhitespace(body.charAt(start))) start++;

        int end = start;
        boolean quoted = (start < body.length() && body.charAt(start) == '"');
        if (quoted) {
            start++;
            end = body.indexOf("\"", start);
            if (end < 0) return null;
            return body.substring(start, end);
        } else {
            while (end < body.length()) {
                char c = body.charAt(end);
                if (c == ',' || c == '}' || Character.isWhitespace(c)) break;
                end++;
            }
            return body.substring(start, end);
        }
    }

    private static String toJsonNumber(Object o) {
        return (o == null) ? "null" : String.valueOf(o);
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

