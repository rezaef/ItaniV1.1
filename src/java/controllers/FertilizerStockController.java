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

import dao.FertilizerStockDAO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet(name="FertilizerStockController", urlPatterns = {
        "/fertilizer-stock/save",
        "/fertilizer-stock/delete"
})
public class FertilizerStockController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String ctx = req.getContextPath();
        String path = req.getServletPath();

        try {
            FertilizerStockDAO dao = new FertilizerStockDAO();

            if ("/fertilizer-stock/save".equals(path)) {
                String idStr = trim(req.getParameter("id"));
                String name  = trim(req.getParameter("name"));
                String unit  = trim(req.getParameter("unit"));
                String stockStr = trim(req.getParameter("stock"));

                if (name.isEmpty()) {
                    resp.sendRedirect(ctx + "/fertilizer_stock.jsp?err=" + enc("Nama wajib diisi"));
                    return;
                }

                if (idStr.isEmpty()) {
                    double stock = stockStr.isEmpty() ? 0 : Double.parseDouble(stockStr);
                    dao.insert(name, unit, stock);
                    resp.sendRedirect(ctx + "/fertilizer_stock.jsp?msg=" + enc("Berhasil tambah pupuk"));
                } else {
                    dao.update(Integer.parseInt(idStr), name, unit);
                    resp.sendRedirect(ctx + "/fertilizer_stock.jsp?msg=" + enc("Berhasil update pupuk"));
                }
                return;
            }

            if ("/fertilizer-stock/delete".equals(path)) {
                int id = Integer.parseInt(req.getParameter("id"));
                dao.delete(id);
                resp.sendRedirect(ctx + "/fertilizer_stock.jsp?msg=" + enc("Berhasil hapus pupuk"));
                return;
            }

            resp.setStatus(404);
        } catch (Exception e) {
            resp.sendRedirect(ctx + "/fertilizer_stock.jsp?err=" + enc("Gagal: " + e.getMessage()));
        }
    }

    private String trim(String s){ return s==null?"":s.trim(); }

    private String enc(String s){
        try { return URLEncoder.encode(s, "UTF-8"); }
        catch(Exception e){ return s.replace(" ","%20"); }
    }
}
