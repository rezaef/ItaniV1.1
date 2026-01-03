/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author rezaef
 */

/**
 * Model Laporan sesuai UML.
 */
public class Laporan {
    private String idLaporan;
    private String tanggalLaporan;
    private String isiLaporan;

    public Laporan() {}

    public Laporan(String idLaporan, String tanggalLaporan, String isiLaporan) {
        this.idLaporan = idLaporan;
        this.tanggalLaporan = tanggalLaporan;
        this.isiLaporan = isiLaporan;
    }

    public String getIdLaporan() {
        return idLaporan;
    }

    public void setIdLaporan(String idLaporan) {
        this.idLaporan = idLaporan;
    }

    public String getTanggalLaporan() {
        return tanggalLaporan;
    }

    public void setTanggalLaporan(String tanggalLaporan) {
        this.tanggalLaporan = tanggalLaporan;
    }

    public String getIsiLaporan() {
        return isiLaporan;
    }

    public void setIsiLaporan(String isiLaporan) {
        this.isiLaporan = isiLaporan;
    }

    /**
     * Placeholder sesuai UML. Di web, cetak dilakukan via halaman print (window.print).
     */
    public void cetakLaporan() {
        // no-op
    }
}
