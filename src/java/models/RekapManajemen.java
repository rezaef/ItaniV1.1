/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author rezaef
 */

import dao.*;
import utils.SensorThreshold;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Kelas RekapManajemen sesuai UML.
 * Menggabungkan data panen, stok bibit, dan sensor untuk ditampilkan / dibuat laporan.
 */
public class RekapManajemen {

    private ArrayList<Harvest> daftarPanen = new ArrayList<>();
    private ArrayList<SeedStock> daftarBibit = new ArrayList<>();
    private ArrayList<Sensor> daftarSensor = new ArrayList<>();

    public ArrayList<Harvest> getDaftarPanen() {
        return daftarPanen;
    }

    public ArrayList<SeedStock> getDaftarBibit() {
        return daftarBibit;
    }

    public void setDaftarBibit(ArrayList<SeedStock> daftarBibit) {
        this.daftarBibit = daftarBibit;
    }

    public ArrayList<Sensor> getDaftarSensor() {
        return daftarSensor;
    }

    public void setDaftarSensor(ArrayList<Sensor> daftarSensor) {
        this.daftarSensor = daftarSensor;
    }

    /**
     * Load data rekap (panen terbaru, stok bibit, sensor terbaru).
     */
    public void load(Integer userId, boolean isAdmin) {
        // panen (ambil latest)
        List<Harvest> panen = new HarvestDAO().listLatest(10, userId, isAdmin);
        this.daftarPanen = new ArrayList<>(panen);

        // bibit
        List<SeedStock> bibit = new SeedStockDAO().findAll();
        this.daftarBibit = new ArrayList<>(bibit);

        // sensor (dibentuk jadi list Sensor sesuai UML)
        Map<String, Object> latest = new SensorDAO().latest();
        this.daftarSensor = new ArrayList<>();
        if (latest != null) {
            LocalDateTime waktu = toLocalDateTime((Timestamp) latest.get("reading_time"));
            addSensor("PH", "pH Tanah", "Parameter", "", toDouble(latest.get("ph")), waktu);
            addSensor("MOISTURE", "Kelembaban Tanah", "Parameter", "%", toDouble(latest.get("soil_moisture")), waktu);
            addSensor("TEMP", "Suhu Tanah", "Parameter", "°C", toDouble(latest.get("soil_temp")), waktu);
            addSensor("EC", "EC", "Parameter", "µS/cm", toDouble(latest.get("ec")), waktu);
            addSensor("N", "Nitrogen (N)", "Parameter", "", toDouble(latest.get("n")), waktu);
            addSensor("P", "Fosfor (P)", "Parameter", "", toDouble(latest.get("p")), waktu);
            addSensor("K", "Kalium (K)", "Parameter", "", toDouble(latest.get("k")), waktu);
        }
    }

    public String tampilkanRekapPanen() {
        if (daftarPanen == null || daftarPanen.isEmpty()) return "Belum ada data panen.";
        double total = 0;
        for (Harvest h : daftarPanen) total += h.getJumlahPanen();
        return "Panen terakhir: " + daftarPanen.size() + " data, total jumlah panen (10 terakhir) = " + round1(total);
    }

    public String tampilkanRekapStok() {
        if (daftarBibit == null || daftarBibit.isEmpty()) return "Belum ada data stok bibit.";
        int totalItem = daftarBibit.size();
        int totalQty = 0;
        for (SeedStock s : daftarBibit) totalQty += s.getStock();
        return "Stok bibit: " + totalItem + " item, total qty = " + totalQty;
    }

    public String tampilkanRekapSensor() {
        if (daftarSensor == null || daftarSensor.isEmpty()) return "Belum ada data sensor.";
        int warn = 0, danger = 0;
        for (Sensor s : daftarSensor) {
            if ("WARNING".equalsIgnoreCase(s.getStatus())) warn++;
            if ("DANGER".equalsIgnoreCase(s.getStatus())) danger++;
        }
        return "Status sensor: " + danger + " danger, " + warn + " warning, sisanya normal.";
    }

    /**
     * Buat isi laporan ringkas (sesuai UML: buatLaporan())
     */
    public Laporan buatLaporan(User u, boolean isAdmin) {
        StringBuilder sb = new StringBuilder();
        sb.append("LAPORAN REKAP ITANI\n");
        sb.append("Dibuat oleh: ").append(u == null ? "-" : u.getName()).append(" (").append(u == null ? "-" : u.getRole()).append(")\n");
        sb.append("Tanggal: ").append(java.time.LocalDateTime.now()).append("\n\n");

        sb.append("1) ").append(tampilkanRekapPanen()).append("\n");
        sb.append("2) ").append(tampilkanRekapStok()).append("\n");
        sb.append("3) ").append(tampilkanRekapSensor()).append("\n\n");

        // detail sensor
        if (daftarSensor != null && !daftarSensor.isEmpty()) {
            sb.append("Detail Sensor (latest):\n");
            for (Sensor s : daftarSensor) {
                sb.append("- ").append(s.getNamaSensor()).append(": ")
                  .append(s.getNilaiSensor() == null ? "-" : round1(s.getNilaiSensor()))
                  .append(s.getSatuan() == null ? "" : s.getSatuan())
                  .append(" (" + s.getStatus() + ")\n");
            }
        }

        // detail stok bibit (top 10)
        if (daftarBibit != null && !daftarBibit.isEmpty()) {
            sb.append("\nDetail Stok Bibit (top 10):\n");
            int max = Math.min(10, daftarBibit.size());
            for (int i = 0; i < max; i++) {
                SeedStock s = daftarBibit.get(i);
                sb.append("- ").append(s.getName()).append(" = ").append(s.getStock()).append(" ").append(s.getUnit()).append("\n");
            }
        }

        return new Laporan("", "", sb.toString());
    }

    private void addSensor(String id, String nama, String tipe, String satuan, Double nilai, LocalDateTime waktu) {
        Sensor s = new Sensor(id, nama, tipe, satuan, nilai, waktu);
        s.setStatus(SensorThreshold.classify(id, nilai));
        this.daftarSensor.add(s);
    }

    private static LocalDateTime toLocalDateTime(Timestamp ts) {
        if (ts == null) return null;
        return ts.toInstant().atZone(ZoneId.of("Asia/Jakarta")).toLocalDateTime();
    }

    private static Double toDouble(Object o) {
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).doubleValue();
        try { return Double.parseDouble(String.valueOf(o)); } catch (Exception e) { return null; }
    }

    private static String round1(double v) {
        double r = Math.round(v * 10.0) / 10.0;
        if (Math.abs(r - Math.rint(r)) < 1e-9) return String.valueOf((long) Math.rint(r));
        return String.valueOf(r);
    }
}
