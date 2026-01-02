<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="models.User"%>
<%@page import="models.SeedStock"%>
<%@page import="models.SeedStockTransaction"%>
<%@page import="dao.SeedStockDAO"%>
<%@page import="dao.SeedStockTransactionDAO"%>

<%
  User u = (User) session.getAttribute("user");
  if (u == null) { response.sendRedirect(request.getContextPath()+"/index.jsp"); return; }

  boolean isAdmin = "Admin".equalsIgnoreCase(u.getRole());
  String ok = request.getParameter("ok");
  String err = request.getParameter("err");

  int stockId = 0;
  try { stockId = Integer.parseInt(request.getParameter("stock_id")); } catch(Exception ex) {}

  SeedStockDAO stockDao = new SeedStockDAO();
  SeedStock stock = (stockId > 0 ? stockDao.findById(stockId) : null);

  SeedStockTransactionDAO txDao = new SeedStockTransactionDAO();
  List<SeedStockTransaction> txs = (stock != null ? txDao.findByStockId(stockId) : new ArrayList<>());
%>

<!doctype html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>ITani - Seed Transactions</title>
  <style>
    body { font-family: Arial; padding: 24px; }
    .top { display:flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
    a.btn, button.btn { padding:8px 12px; border:1px solid #ddd; border-radius:10px; text-decoration:none; background:#fff; cursor:pointer; }
    .card { border:1px solid #eee; border-radius:14px; padding:14px; }
    input, select, textarea { width:100%; padding:10px; border:1px solid #ddd; border-radius:10px; margin:6px 0 12px; }
    table { width:100%; border-collapse: collapse; }
    th, td { border-bottom:1px solid #eee; padding:10px 8px; text-align:left; font-size:14px; }
    th { background:#fafafa; }
    .msg { margin: 10px 0; padding: 10px; border-radius: 10px; }
    .ok { background:#e9ffe9; border:1px solid #b7f5b7; }
    .err { background:#ffe9e9; border:1px solid #f5b7b7; }
    .muted { color:#777; font-size: 13px; }
    .row { display:grid; grid-template-columns: 360px 1fr; gap: 18px; }
    .rowbtn { display:flex; gap:8px; flex-wrap: wrap; }
  </style>
</head>
<body>

  <div class="top">
    <div>
      <h2 style="margin:0">Transaksi Bibit</h2>
      <div class="muted">Login sebagai: <b><%=u.getName()%></b> (<%=u.getRole()%>)</div>
    </div>
    <div class="rowbtn">
      <a class="btn" href="<%=request.getContextPath()%>/seed_stock.jsp">Seed Stock</a>
      <a class="btn" href="<%=request.getContextPath()%>/dashboard.jsp">Dashboard</a>
      <a class="btn" href="<%=request.getContextPath()%>/api/auth/logout">Logout</a>
    </div>
  </div>

  <% if (!isAdmin) { %>
    <div class="msg err">Akses ditolak. Halaman ini khusus Admin.</div>
  <% } else if (stock == null) { %>
    <div class="msg err">Stock ID tidak valid. Kembali ke <a href="<%=request.getContextPath()%>/seed_stock.jsp">Seed Stock</a>.</div>
  <% } else { %>

    <% if (ok != null) { %><div class="msg ok">Transaksi berhasil direkam.</div><% } %>
    <% if (err != null) { %><div class="msg err">Gagal merekam transaksi. Pastikan qty valid dan OUT tidak melebihi stok.</div><% } %>

    <div class="row">
      <div class="card">
        <h3 style="margin-top:0"><%=stock.getName()%></h3>
        <div class="muted">Unit: <b><%=stock.getUnit()%></b></div>
        <div style="margin:10px 0; font-size:28px;"><b>Stok: <%=stock.getStock()%></b></div>

        <h4>Tambah Transaksi</h4>
        <form method="POST" action="<%=request.getContextPath()%>/seed-stock/tx/create">
          <input type="hidden" name="redirect" value="1"/>
          <input type="hidden" name="stock_id" value="<%=stock.getId()%>"/>

          <label>Tipe</label>
          <select name="type" required>
            <option value="IN">IN (Tambah)</option>
            <option value="OUT">OUT (Kurangi)</option>
          </select>

          <label>Qty</label>
          <input name="qty" type="number" min="1" required placeholder="Contoh: 10"/>

          <label>Catatan (opsional)</label>
          <textarea name="note" rows="3" placeholder="Contoh: Pembelian / Pemakaian bedeng 1"></textarea>

          <button class="btn" type="submit">Simpan Transaksi</button>
        </form>
      </div>

      <div class="card">
        <h3 style="margin-top:0">Riwayat Transaksi</h3>
        <table>
          <thead>
            <tr>
              <th>Waktu</th>
              <th>Tipe</th>
              <th>Qty</th>
              <th>Catatan</th>
            </tr>
          </thead>
          <tbody>
            <% if (txs == null || txs.isEmpty()) { %>
              <tr><td colspan="4" class="muted">Belum ada transaksi.</td></tr>
            <% } else { 
                 for (SeedStockTransaction t : txs) { %>
              <tr>
                <td class="muted"><%= (t.getCreatedAt()!=null? t.getCreatedAt().toString() : "-") %></td>
                <td><b><%=t.getType()%></b></td>
                <td><%=t.getQty()%></td>
                <td><%= (t.getNote()!=null? t.getNote() : "") %></td>
              </tr>
            <% } } %>
          </tbody>
        </table>
      </div>
    </div>

  <% } %>

</body>
</html>
