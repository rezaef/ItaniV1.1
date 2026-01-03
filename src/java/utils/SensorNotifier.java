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

import dao.NotificationDAO;

/**
 * Mengecek pembacaan sensor dan membuat notifikasi WARNING/DANGER jika
 * nilai berada di ambang batas atau sudah melewati batas.
 *
 * Threshold di sini adalah default (umum hortikultura). Kalau kamu punya
 * standar khusus komoditas/lahan, tinggal ubah angka di bawah.
 */
public class SensorNotifier {

    // Anti-spam: interval minimal (menit) untuk notifikasi dengan ruleKey yang sama
    private static final int COOLDOWN_WARNING_MIN = 15;
    private static final int COOLDOWN_DANGER_MIN = 5;

    public static void onNewReading(Double ph, Double soilMoisture, Double soilTemp,
                                    Double ec, Integer n, Integer p, Integer k,
                                    String source) {

        // pH (ideal 5.5–7.5)
        if (ph != null) {
            // WARNING jika <5.5 atau >7.5 | DANGER jika <5.0 atau >8.0
            check("PH", "pH", ph, 5.0, 5.5, 7.5, 8.0, "", "5.5–7.5", source);
        }

        // Soil moisture (%) - umum
        if (soilMoisture != null) {
            check("MOISTURE", "Kelembaban Tanah", soilMoisture, 30, 40, 80, 90, "%", "40–80%", source);
        }

        // Soil temperature (°C)
        if (soilTemp != null) {
            check("TEMP", "Suhu Tanah", soilTemp, 15, 18, 32, 35, "°C", "18–32°C", source);
        }

        // EC (µS/cm)
        if (ec != null) {
            // baseline umum: WARNING <200 atau >2000 | DANGER <100 atau >3000
            check("EC", "EC", ec, 100, 200, 2000, 3000, "µS/cm", "200–2000 µS/cm", source);
        }

        // N, P, K (ppm / unit sensor) - baseline umum, sesuaikan jika sensor/komoditas berbeda
        if (n != null) {
            check("N", "Nitrogen (N)", n.doubleValue(), 15, 30, 250, 350, "", "30–250", source);
        }
        if (p != null) {
            check("P", "Fosfor (P)", p.doubleValue(), 15, 30, 250, 350, "", "30–250", source);
        }
        if (k != null) {
            check("K", "Kalium (K)", k.doubleValue(), 15, 30, 250, 350, "", "30–250", source);
        }
    }

    private static void check(String sensorKey, String label, double value,
                              double lowDangerMax, double lowWarnMax,
                              double highWarnMin, double highDangerMin,
                              String unit, String idealRange, String source) {

        String level = null;
        String ruleKey = null;
        String message = null;
        int cooldown = COOLDOWN_WARNING_MIN;

        if (value < lowDangerMax) {
            level = "DANGER";
            ruleKey = sensorKey + "_LOW_DANGER";
            cooldown = COOLDOWN_DANGER_MIN;
            message = label + " sangat rendah (" + fmt(value) + unit + "). Ideal " + idealRange + ".";
        } else if (value < lowWarnMax) {
            level = "WARNING";
            ruleKey = sensorKey + "_LOW_WARNING";
            message = label + " di ambang batas rendah (" + fmt(value) + unit + "). Ideal " + idealRange + ".";
        } else if (value > highDangerMin) {
            level = "DANGER";
            ruleKey = sensorKey + "_HIGH_DANGER";
            cooldown = COOLDOWN_DANGER_MIN;
            message = label + " sangat tinggi (" + fmt(value) + unit + "). Ideal " + idealRange + ".";
        } else if (value > highWarnMin) {
            level = "WARNING";
            ruleKey = sensorKey + "_HIGH_WARNING";
            message = label + " di ambang batas tinggi (" + fmt(value) + unit + "). Ideal " + idealRange + ".";
        }

        if (level == null) return;

        NotificationDAO dao = new NotificationDAO();

        // Anti-spam: jangan insert rule yang sama kalau baru dibuat dalam cooldown
        if (dao.hasRecent(ruleKey, cooldown)) return;

        dao.insert(
            ruleKey,
            sensorKey,
            level,
            message,
            value,
            (source == null || source.isEmpty()) ? "SYSTEM" : source
        );
    }

    private static String fmt(double v) {
        // rapikan angka: max 1 decimal
        double r = Math.round(v * 10.0) / 10.0;
        if (Math.abs(r - Math.rint(r)) < 1e-9) return String.valueOf((long) Math.rint(r));
        return String.valueOf(r);
    }
}
