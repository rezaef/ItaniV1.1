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

public class WateringLog {
    private int id;
    private String mode;     // MANUAL / AUTO
    private String action;   // ON / OFF
    private String source;   // WEB / DEVICE / RULE
    private String note;
    private Timestamp createdAt;

    public WateringLog() {}

    public WateringLog(int id, String mode, String action, String source, String note, Timestamp createdAt) {
        this.id = id;
        this.mode = mode;
        this.action = action;
        this.source = source;
        this.note = note;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public String getMode() { return mode; }
    public String getAction() { return action; }
    public String getSource() { return source; }
    public String getNote() { return note; }
    public Timestamp getCreatedAt() { return createdAt; }

    public void setId(int id) { this.id = id; }
    public void setMode(String mode) { this.mode = mode; }
    public void setAction(String action) { this.action = action; }
    public void setSource(String source) { this.source = source; }
    public void setNote(String note) { this.note = note; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
