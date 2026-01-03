/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package utils;

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
/*
 * AutoModeState
 * - Menyimpan status mode otomatis di sisi server.
 * - Di-update dari retained topic MQTT okra/pump/autoMode dan juga dari
 *   pemanggilan endpoint /pump/mode/auto/on|off.
 */

import java.util.Map;

public class AutoModeState {

    private static volatile boolean enabled = false;
    private static volatile long updatedAtMs = 0;

    public static void setEnabled(boolean on) {
        enabled = on;
        updatedAtMs = System.currentTimeMillis();
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static String getMode() {
        return enabled ? "AUTO" : "MANUAL";
    }

    public static long getUpdatedAtMs() {
        return updatedAtMs;
    }

    /**
     * Update state dari payload MQTT.
     * Payload umum: ON/OFF, 1/0, TRUE/FALSE, AUTO/MANUAL.
     * Atau JSON flat: {"mode":"AUTO"} / {"auto":true}.
     */
    public static void updateFromPayload(String payload) {
        Boolean b = parseBoolean(payload);
        if (b != null) setEnabled(b);
    }

    public static Boolean parseBoolean(String payload) {
        if (payload == null) return null;
        String s = payload.trim();
        if (s.isEmpty()) return null;

        // JSON flat
        if (s.startsWith("{") && s.endsWith("}")) {
            try {
                Map<String, String> j = SimpleJson.parseFlat(s);
                String v = j.get("auto");
                if (v == null) v = j.get("mode");
                if (v == null) v = j.get("value");
                if (v == null) v = j.get("state");
                if (v != null) return parseBoolean(v);
            } catch (Exception ignored) {
            }
        }

        String up = s.toUpperCase();
        if ("ON".equals(up) || "AUTO".equals(up) || "1".equals(up) || "TRUE".equals(up)) return true;
        if ("OFF".equals(up) || "MANUAL".equals(up) || "0".equals(up) || "FALSE".equals(up)) return false;

        return null;
    }
}
