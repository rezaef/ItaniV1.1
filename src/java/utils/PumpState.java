/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package utils;

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


public class PumpState {
    private static volatile String status = "UNKNOWN";
    private static volatile long updatedAtMs = 0;

    public static void update(String s){
        status = (s == null || s.trim().isEmpty()) ? "UNKNOWN" : s.trim();
        updatedAtMs = System.currentTimeMillis();
    }
    public static String getStatus(){ return status; }
    public static long getUpdatedAtMs(){ return updatedAtMs; }
}
