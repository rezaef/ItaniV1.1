/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controllers;

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

import utils.MqttSubscriber;
import utils.PumpState;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name="PumpController", urlPatterns = {
        "/pump/on",
        "/pump/off",
        "/pump/status"
})
public class PumpController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getServletPath();
        String cmd = null;

        if ("/pump/on".equals(path)) cmd = "ON";
        if ("/pump/off".equals(path)) cmd = "OFF";

        if (cmd == null) {
            resp.setStatus(404);
            return;
        }

        boolean ok = MqttSubscriber.sendPumpCmd(cmd);

        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write("{\"ok\":" + ok + ",\"cmd\":\"" + cmd + "\"}");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!"/pump/status".equals(req.getServletPath())) {
            resp.setStatus(404);
            return;
        }

        String st = PumpState.getStatus();
        long ts = PumpState.getUpdatedAtMs();

        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write("{\"ok\":true,\"status\":\"" + esc(st) + "\",\"updatedAtMs\":" + ts + "}");
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
