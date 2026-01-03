/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

/**
 *
 * @author rezaef
 */

/**
 * Threshold & klasifikasi status sensor yang dipakai bersama oleh:
 * - SensorNotifier (buat notifikasi)
 * - models.Sensor.perbaruiStatus() (buat status di rekap)
 */
public class SensorThreshold {

    public static String classify(String sensorKey, Double value) {
        if (sensorKey == null || value == null) return "NORMAL";
        String k = sensorKey.trim().toUpperCase();

        // sensorKey mengikuti yang dipakai SensorNotifier: PH, MOISTURE, TEMP, EC, N, P, K
        switch (k) {
            case "PH":
                return classifyRange(value, 5.0, 5.5, 7.5, 8.0);
            case "MOISTURE":
                return classifyRange(value, 30, 40, 80, 90);
            case "TEMP":
                return classifyRange(value, 15, 18, 32, 35);
            case "EC":
                return classifyRange(value, 100, 200, 2000, 3000);
            case "N":
            case "P":
            case "K":
                return classifyRange(value, 15, 30, 250, 350);
            default:
                return "NORMAL";
        }
    }

    private static String classifyRange(double value,
                                        double lowDangerMax, double lowWarnMax,
                                        double highWarnMin, double highDangerMin) {
        if (value < lowDangerMax) return "DANGER";
        if (value < lowWarnMax) return "WARNING";
        if (value > highDangerMin) return "DANGER";
        if (value > highWarnMin) return "WARNING";
        return "NORMAL";
    }
}
