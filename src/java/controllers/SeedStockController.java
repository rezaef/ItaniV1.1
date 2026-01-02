package controllers;

import dao.SeedStockDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name="SeedStockController", urlPatterns = {
    "/seed-stock/save",
    "/seed-stock/delete"
})
public class SeedStockController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();
        boolean wantRedirect = "1".equals(req.getParameter("redirect"));

        SeedStockDAO dao = new SeedStockDAO();

        // SAVE (create/update meta)
        if ("/seed-stock/save".equals(path)) {
            String idStr = req.getParameter("id");
            String name = req.getParameter("name");
            String unit = req.getParameter("unit");
            String stockStr = req.getParameter("stock"); // hanya dipakai saat create

            if (name == null || name.trim().isEmpty() || unit == null || unit.trim().isEmpty()) {
                respond(resp, wantRedirect, false, "Nama & unit wajib diisi", req.getContextPath() + "/seed_stock.jsp?err=1");
                return;
            }

            boolean ok;
            if (idStr == null || idStr.trim().isEmpty()) {
                int stock = 0;
                try { stock = Integer.parseInt(stockStr == null ? "0" : stockStr); } catch (Exception ignore) {}
                ok = dao.insert(name.trim(), unit.trim(), Math.max(0, stock));
            } else {
                int id;
                try { id = Integer.parseInt(idStr); } catch (Exception e) { id = 0; }
                ok = (id > 0) && dao.updateMeta(id, name.trim(), unit.trim());
            }

            if (wantRedirect) {
                resp.sendRedirect(req.getContextPath() + "/seed_stock.jsp?" + (ok ? "ok=1" : "err=1"));
            } else {
                respondJson(resp, ok, ok ? "saved" : "failed");
            }
            return;
        }

        // DELETE
        if ("/seed-stock/delete".equals(path)) {
            int id = 0;
            try { id = Integer.parseInt(req.getParameter("id")); } catch (Exception ignore) {}
            boolean ok = (id > 0) && dao.delete(id);

            if (wantRedirect) {
                resp.sendRedirect(req.getContextPath() + "/seed_stock.jsp?" + (ok ? "ok=1" : "err=1"));
            } else {
                respondJson(resp, ok, ok ? "deleted" : "failed");
            }
            return;
        }

        resp.setStatus(404);
    }

    private void respond(HttpServletResponse resp, boolean wantRedirect, boolean ok, String msg, String redirectUrl) throws IOException {
        if (wantRedirect) {
            resp.sendRedirect(redirectUrl);
        } else {
            respondJson(resp, ok, msg);
        }
    }

    private void respondJson(HttpServletResponse resp, boolean ok, String msg) throws IOException {
        resp.setStatus(ok ? 200 : 400);
        resp.setContentType("application/json; charset=UTF-8");
        resp.getWriter().write("{\"ok\":" + ok + ",\"message\":\"" + esc(msg) + "\"}");
    }

    // Escape minimal untuk string JSON
    private String esc(String s) {
        if (s == null) return "";
        return s
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\r", "")
            .replace("\n", "\\n");
    }
}
