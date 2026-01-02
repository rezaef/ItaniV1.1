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

import dao.FertilizerStockTransactionDAO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet(name="FertilizerStockTransactionController", urlPatterns = {
        "/fertilizer-stock/tx/create"
})
public class FertilizerStockTransactionController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String ctx = req.getContextPath();

        int stockId = Integer.parseInt(req.getParameter("stock_id"));
        String type = trim(req.getParameter("type"));
        double qty  = Double.parseDouble(req.getParameter("qty"));
        String note = trim(req.getParameter("note"));

        try {
            new FertilizerStockTransactionDAO().createTx(stockId, type, qty, note);
            resp.sendRedirect(ctx + "/fertilizer_transactions.jsp?stock_id=" + stockId + "&msg=" + enc("Transaksi tersimpan"));
        } catch (Exception e) {
            resp.sendRedirect(ctx + "/fertilizer_transactions.jsp?stock_id=" + stockId + "&err=" + enc("Gagal: " + e.getMessage()));
        }
    }

    private String trim(String s){ return s==null?"":s.trim(); }

    private String enc(String s){
        try { return URLEncoder.encode(s, "UTF-8"); }
        catch(Exception e){ return s.replace(" ","%20"); }
    }
}
