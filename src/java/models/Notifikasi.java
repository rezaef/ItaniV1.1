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
 * Interface Notifikasi (sesuai UML).
 * Digunakan sebagai kontrak untuk mengirim dan menampilkan notifikasi.
 */
public interface Notifikasi {
    void kirimNotifikasi(String pesan);
    void tampilkanNotifikasi();
}
