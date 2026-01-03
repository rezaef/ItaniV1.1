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

/**
 * NotifikasiUser (sesuai class diagram): data notifikasi yang disimpan ke DB
 * dan ditampilkan ke dashboard.
 */
public class Notification {
    private int id;
    private String ruleKey;   // contoh: PH_HIGH_WARNING
    private String sensorKey; // PH / MOISTURE / TEMP / EC / N / P / K
    private String level;     // WARNING / DANGER
    private String message;
    private Double value;
    private String status;    // UNREAD / READ
    private String source;    // ESP / WEB / SYSTEM
    private Timestamp createdAt;

    public Notification() {}

    public Notification(int id, String ruleKey, String sensorKey, String level,
                        String message, Double value, String status, String source,
                        Timestamp createdAt) {
        this.id = id;
        this.ruleKey = ruleKey;
        this.sensorKey = sensorKey;
        this.level = level;
        this.message = message;
        this.value = value;
        this.status = status;
        this.source = source;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRuleKey() { return ruleKey; }
    public void setRuleKey(String ruleKey) { this.ruleKey = ruleKey; }

    public String getSensorKey() { return sensorKey; }
    public void setSensorKey(String sensorKey) { this.sensorKey = sensorKey; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
