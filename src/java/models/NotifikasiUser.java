/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author rezaef
 */
import dao.NotifikasiUserDAO;
import java.sql.Timestamp;

/**
 * NotifikasiUser (sesuai UML).
 *
 * Catatan implementasi project:
 * - Data notifikasi disimpan di tabel `notifications`.
 * - Kolom DB yang dipakai: rule_key, sensor_key, level, message, value, status, source, created_at.
 * - Atribut UML: idNotif, pesan, waktu, status, penerima(User).
 */
public class NotifikasiUser implements Notifikasi {

    // --- Field internal (DB) ---
    private int id;                 // id (AUTO_INCREMENT)
    private String ruleKey;         // contoh: PH_HIGH_WARNING
    private String sensorKey;       // PH / MOISTURE / TEMP / EC / N / P / K
    private String level;           // WARNING / DANGER
    private Double value;           // nilai sensor (opsional)
    private String source;          // ESP / WEB / SYSTEM
    private Timestamp createdAt;    // waktu dibuat

    // --- Atribut UML ---
    private String pesan;           // message
    private String status;          // UNREAD / READ
    private User penerima;          // tidak dipersist (opsional untuk tugas UML)

    public NotifikasiUser() {}

    public NotifikasiUser(int id, String ruleKey, String sensorKey, String level,
                          String pesan, Double value, String status, String source,
                          Timestamp createdAt) {
        this.id = id;
        this.ruleKey = ruleKey;
        this.sensorKey = sensorKey;
        this.level = level;
        this.pesan = pesan;
        this.value = value;
        this.status = status;
        this.source = source;
        this.createdAt = createdAt;
    }

    // =====================
    // Getter/Setter UML
    // =====================

    /** UML: getIdNotif(): String */
    public String getIdNotif() {
        return String.valueOf(id);
    }

    /** UML: setIdNotif(String) */
    public void setIdNotif(String idNotif) {
        try {
            this.id = Integer.parseInt(idNotif);
        } catch (Exception e) {
            this.id = 0;
        }
    }

    /** UML: getPesan() */
    public String getPesan() {
        return pesan;
    }

    /** UML: setPesan(String) */
    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    /** UML: getStatus(): String */
    public String getStatus() {
        return status;
    }

    /** UML: setStatus(String) */
    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getWaktu() {
        return createdAt;
    }

    public void setWaktu(Timestamp waktu) {
        this.createdAt = waktu;
    }

    public User getPenerima() {
        return penerima;
    }

    public void setPenerima(User penerima) {
        this.penerima = penerima;
    }

    // =====================
    // Kontrak interface Notifikasi
    // =====================

    /**
     * UML: kirimNotifikasi(String pesan)
     * Implementasi: simpan ke DB sebagai UNREAD.
     */
    @Override
    public void kirimNotifikasi(String pesan) {
        this.pesan = pesan;
        if (this.status == null || this.status.isEmpty()) this.status = "UNREAD";
        if (this.source == null || this.source.isEmpty()) this.source = "SYSTEM";

        // Insert ke DB
        new NotifikasiUserDAO().insert(
                this.ruleKey,
                this.sensorKey,
                this.level,
                this.pesan,
                this.value,
                this.source
        );
    }

    /**
     * UML: tampilkanNotifikasi()
     * Implementasi sederhana: output ke console (dashboard menampilkan via query DB).
     */
    @Override
    public void tampilkanNotifikasi() {
        System.out.println("[NOTIF " + (level == null ? "" : level) + "] " + pesan);
    }

    // =====================
    // Getter/Setter tambahan (untuk kebutuhan project)
    // =====================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRuleKey() { return ruleKey; }
    public void setRuleKey(String ruleKey) { this.ruleKey = ruleKey; }

    public String getSensorKey() { return sensorKey; }
    public void setSensorKey(String sensorKey) { this.sensorKey = sensorKey; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
