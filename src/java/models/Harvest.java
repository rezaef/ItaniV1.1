/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package models;

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

import java.sql.Timestamp;

public class Harvest {
    private int id;
    private int periodeId;
    private Timestamp tanggalPanen;
    private String jenisTanaman;
    private double jumlahPanen;
    private String catatan;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPeriodeId() { return periodeId; }
    public void setPeriodeId(int periodeId) { this.periodeId = periodeId; }

    public Timestamp getTanggalPanen() { return tanggalPanen; }
    public void setTanggalPanen(Timestamp tanggalPanen) { this.tanggalPanen = tanggalPanen; }

    public String getJenisTanaman() { return jenisTanaman; }
    public void setJenisTanaman(String jenisTanaman) { this.jenisTanaman = jenisTanaman; }

    public double getJumlahPanen() { return jumlahPanen; }
    public void setJumlahPanen(double jumlahPanen) { this.jumlahPanen = jumlahPanen; }

    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
