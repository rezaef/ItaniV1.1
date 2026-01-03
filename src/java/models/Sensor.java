/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author rezaef
 */

import java.time.LocalDateTime;

/**
 * Model Sensor sesuai UML.
 * Di project ini, 1 "Sensor" merepresentasikan 1 parameter (pH, Moisture, Temp, EC, N, P, K)
 * yang dibentuk dari pembacaan terbaru pada tabel sensor_readings.
 */
public class Sensor {
    private String sensorId;
    private String namaSensor;
    private String tipeSensor;
    private String satuan;
    private String status; // NORMAL / WARNING / DANGER
    private LocalDateTime waktuUpdate;
    private Double nilaiSensor;

    public Sensor() {}

    public Sensor(String sensorId, String namaSensor, String tipeSensor, String satuan, Double nilaiSensor, LocalDateTime waktuUpdate) {
        this.sensorId = sensorId;
        this.namaSensor = namaSensor;
        this.tipeSensor = tipeSensor;
        this.satuan = satuan;
        this.nilaiSensor = nilaiSensor;
        this.waktuUpdate = waktuUpdate;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getNamaSensor() {
        return namaSensor;
    }

    public void setNamaSensor(String namaSensor) {
        this.namaSensor = namaSensor;
    }

    public String getTipeSensor() {
        return tipeSensor;
    }

    public void setTipeSensor(String tipeSensor) {
        this.tipeSensor = tipeSensor;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getWaktuUpdate() {
        return waktuUpdate;
    }

    public void setWaktuUpdate(LocalDateTime waktuUpdate) {
        this.waktuUpdate = waktuUpdate;
    }

    public Double getNilaiSensor() {
        return nilaiSensor;
    }

    public void setNilaiSensor(Double nilaiSensor) {
        this.nilaiSensor = nilaiSensor;
    }

    /**
     * Placeholder sesuai UML. Data real dibaca dari DB (SensorDAO.latest) / MQTT.
     */
    public void bacaData() {
        // no-op
    }

    /**
     * Update status berdasarkan threshold default yang sama dengan SensorNotifier.
     */
    public void perbaruiStatus() {
        this.status = utils.SensorThreshold.classify(sensorId, nilaiSensor);
    }
}
