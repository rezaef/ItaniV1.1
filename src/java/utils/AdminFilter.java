/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

/**
 *
 * @author rezaef
 */

import models.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter untuk membatasi akses fitur Kelola User (Admin only).
 * Sesuai kebutuhan: perbedaan Admin vs Petani hanya pada Manajemen User.
 */
@WebFilter(filterName = "AdminFilter", urlPatterns = {
        "/users.jsp",
        "/user/*"
})
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession s = req.getSession(false);
        if (s == null || s.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        User u = (User) s.getAttribute("user");
        boolean isAdmin = (u != null && "Admin".equalsIgnoreCase(u.getRole()));

        if (!isAdmin) {
            String path = req.getServletPath();
            boolean isPage = (path != null && path.endsWith(".jsp"));

            if (isPage) {
                resp.sendRedirect(req.getContextPath() + "/dashboard.jsp?err=forbidden");
            } else {
                resp.setStatus(403);
                resp.setContentType("application/json; charset=UTF-8");
                resp.getWriter().write("{\"ok\":false,\"message\":\"forbidden\"}");
            }
            return;
        }

        chain.doFilter(request, response);
    }
}
