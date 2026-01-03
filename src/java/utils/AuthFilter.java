/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author rezaef
 */

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

@WebFilter(filterName="AuthFilter", urlPatterns={
    "/dashboard.jsp",

    "/watering_logs.jsp",

    "/rekap.jsp",
    "/laporan.jsp",
    "/laporan/*",

    "/api/watering/log",
    "/pump/*",

    "/seed_stock.jsp",
    "/seed_transactions.jsp",
    "/seed-stock/*",

    "/fertilizer_stock.jsp",
    "/fertilizer_transactions.jsp",
    "/fertilizer-stock/*",
    
    "/periods.jsp",
    "/harvests.jsp",
    "/period/*",
    "/harvest/*",

    "/users.jsp",
    "/user/*"

})
public class AuthFilter implements Filter {
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
        chain.doFilter(request, response);
    }
}
