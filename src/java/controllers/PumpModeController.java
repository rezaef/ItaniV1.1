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

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name="PumpModeController", urlPatterns = {
        "/pump/mode/auto/on",
        "/pump/mode/auto/off"
})
public class PumpModeController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handle(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // biar gampang test dari browser juga
        handle(req, resp);
    }

    private void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getServletPath();

        boolean auto;
        if (path.endsWith("/on")) auto = true;
        else if (path.endsWith("/off")) auto = false;
        else {
            resp.setStatus(404);
            return;
        }

        boolean ok = MqttSubscriber.sendPumpMode(auto);

        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write("{\"ok\":" + ok + ",\"mode\":\"" + (auto ? "AUTO" : "MANUAL") + "\"}");
    }
}
