/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controllers;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author rezaef
 */

import dao.UserDAO;
import models.User;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name="AuthController", urlPatterns = {
        "/api/auth/login",
        "/api/auth/logout"
})
public class AuthController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        // LOGIN
        if ("/api/auth/login".equals(path)) {
            String username = req.getParameter("username");
            String password = req.getParameter("password");

            User user = new UserDAO().login(username, password);

            boolean wantRedirect = "1".equals(req.getParameter("redirect"));

            if (user != null) {
                HttpSession s = req.getSession(true);
                s.setAttribute("user", user);

                if (wantRedirect) {
                    resp.sendRedirect(req.getContextPath() + "/dashboard.jsp");
                } else {
                    resp.setContentType("application/json; charset=UTF-8");
                    resp.getWriter().write("{\"ok\":true,\"message\":\"login success\"}");
                }
            } else {
                if (wantRedirect) {
                    resp.sendRedirect(req.getContextPath() + "/index.jsp?err=1");
                } else {
                    resp.setStatus(401);
                    resp.setContentType("application/json; charset=UTF-8");
                    resp.getWriter().write("{\"ok\":false,\"message\":\"invalid credentials\"}");
                }
            }
            return;
        }

        resp.setStatus(404);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        // LOGOUT
        if ("/api/auth/logout".equals(path)) {
            HttpSession s = req.getSession(false);
            if (s != null) s.invalidate();
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        resp.setStatus(404);
    }
}
