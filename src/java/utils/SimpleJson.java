/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package utils;

/**
 *
 * @author rezaef
 */

import java.util.HashMap;
import java.util.Map;

public class SimpleJson {

    // parser sangat sederhana untuk JSON flat: {"a":1,"b":2}
    public static Map<String, String> parseFlat(String body) {
        Map<String, String> m = new HashMap<>();
        if (body == null) return m;

        String s = body.trim();
        if (s.startsWith("{")) s = s.substring(1);
        if (s.endsWith("}")) s = s.substring(0, s.length() - 1);

        // split by comma (cukup untuk json flat yang tidak ada nested)
        String[] parts = s.split(",");
        for (String part : parts) {
            String[] kv = part.split(":", 2);
            if (kv.length != 2) continue;
            String k = strip(kv[0]);
            String v = strip(kv[1]);
            m.put(k, v);
        }
        return m;
    }

    private static String strip(String x) {
        String s = x.trim();
        if (s.startsWith("\"")) s = s.substring(1);
        if (s.endsWith("\"")) s = s.substring(0, s.length() - 1);
        return s.trim();
    }

    public static Double getDouble(Map<String, String> m, String key) {
        try {
            String v = m.get(key);
            return (v == null || v.isEmpty()) ? null : Double.valueOf(v);
        } catch (Exception e) { return null; }
    }

    public static Integer getInt(Map<String, String> m, String key) {
        try {
            String v = m.get(key);
            return (v == null || v.isEmpty()) ? null : Integer.valueOf(v);
        } catch (Exception e) { return null; }
    }
}
