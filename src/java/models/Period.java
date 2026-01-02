/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package models;

/**
 *
 * @author rezaef
 */

import java.sql.Timestamp;

public class Period {
    private int id;
    private int userId;
    private String namaPeriode;
    private Timestamp tanggalMulai;
    private Timestamp tanggalSelesai;
    private String deskripsi;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Statistik (rekap)
    private int harvestCount;
    private double harvestTotal;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getNamaPeriode() { return namaPeriode; }
    public void setNamaPeriode(String namaPeriode) { this.namaPeriode = namaPeriode; }

    public Timestamp getTanggalMulai() { return tanggalMulai; }
    public void setTanggalMulai(Timestamp tanggalMulai) { this.tanggalMulai = tanggalMulai; }

    public Timestamp getTanggalSelesai() { return tanggalSelesai; }
    public void setTanggalSelesai(Timestamp tanggalSelesai) { this.tanggalSelesai = tanggalSelesai; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public int getHarvestCount() { return harvestCount; }
    public void setHarvestCount(int harvestCount) { this.harvestCount = harvestCount; }

    public double getHarvestTotal() { return harvestTotal; }
    public void setHarvestTotal(double harvestTotal) { this.harvestTotal = harvestTotal; }
}
