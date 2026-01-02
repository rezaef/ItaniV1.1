/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controllers;

import dao.SeedStockTransactionDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name="SeedStockTransactionController", urlPatterns = {
    "/seed-stock/tx/create"
})
public class SeedStockTransactionController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        boolean wantRedirect = "1".equals(req.getParameter("redirect"));

        int stockId = 0;
        int qty = 0;
        try { stockId = Integer.parseInt(req.getParameter("stock_id")); } catch (Exception ignore) {}
        try { qty = Integer.parseInt(req.getParameter("qty")); } catch (Exception ignore) {}

        String type = req.getParameter("type");
        String note = req.getParameter("note");

        SeedStockTransactionDAO dao = new SeedStockTransactionDAO();
        boolean ok = (stockId > 0) && dao.recordTransaction(stockId, type, qty, note);

        if (wantRedirect) {
            resp.sendRedirect(req.getContextPath() + "/seed_transactions.jsp?stock_id=" + stockId + "&" + (ok ? "ok=1" : "err=1"));
        } else {
            resp.setStatus(ok ? 200 : 400);
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().write("{\"ok\":" + ok + ",\"message\":\"" + (ok ? "recorded" : "failed") + "\"}");
        }
    }
}
