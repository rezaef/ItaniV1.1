<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="models.User"%>
<%@page import="models.SeedStock"%>
<%@page import="dao.SeedStockDAO"%>

<%
  User u = (User) session.getAttribute("user");
  if (u == null) { response.sendRedirect(request.getContextPath()+"/index.jsp"); return; }

  boolean isAdmin = "Admin".equalsIgnoreCase(u.getRole());
  String ok = request.getParameter("ok");
  String err = request.getParameter("err");

  SeedStockDAO dao = new SeedStockDAO();
  List<SeedStock> items = dao.findAll();

  String editIdStr = request.getParameter("edit");
  SeedStock editing = null;
  if (editIdStr != null && !editIdStr.trim().isEmpty()) {
      try { editing = dao.findById(Integer.parseInt(editIdStr)); } catch(Exception ex) {}
  }
%>

<!doctype html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>ITani - Seed Stock</title>
  <style>
    body { font-family: Arial; padding: 24px; }
    .top { display:flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
    a.btn, button.btn { padding:8px 12px; border:1px solid #ddd; border-radius:10px; text-decoration:none; background:#fff; cursor:pointer; }
    .grid { display:grid; grid-template-columns: 1fr 1fr; gap: 18px; }
    .card { border:1px solid #eee; border-radius:14px; padding:14px; }
    input, select, textarea { width:100%; padding:10px; border:1px solid #ddd; border-radius:10px; margin:6px 0 12px; }
    table { width:100%; border-collapse: collapse; }
    th, td { border-bottom:1px solid #eee; padding:10px 8px; text-align:left; font-size:14px; }
    th { background:#fafafa; }
    .msg { margin: 10px 0; padding: 10px; border-radius: 10px; }
    .ok { background:#e9ffe9; border:1px solid #b7f5b7; }
    .err { background:#ffe9e9; border:1px solid #f5b7b7; }
    .muted { color:#777; font-size: 13px; }
    .rowbtn { display:flex; gap:8px; flex-wrap: wrap; }
  </style>
</head>
<body>

  <div class="top">
    <div>
      <h2 style="margin:0">Seed Stock</h2>
      <div class="muted">Login sebagai: <b><%=u.getName()%></b> (<%=u.getRole()%>)</div>
    </div>
    <div class="rowbtn">
      <a class="btn" href="<%=request.getContextPath()%>/dashboard.jsp">Dashboard</a>
      <a class="btn" href="<%=request.getContextPath()%>/api/auth/logout">Logout</a>
    </div>
  </div>

  <% if (!isAdmin) { %>
    <div class="msg err">Akses ditolak. Halaman ini khusus Admin.</div>
  <% } else { %>

    <% if (ok != null) { %><div class="msg ok">Berhasil.</div><% } %>
    <% if (err != null) { %><div class="msg err">Gagal. Cek input / relasi data.</div><% } %>

    <div class="grid">

      <div class="card">
        <h3 style="margin-top:0"><%= (editing == null ? "Tambah Bibit" : "Edit Bibit") %></h3>

        <form method="POST" action="<%=request.getContextPath()%>/seed-stock/save">
          <input type="hidden" name="redirect" value="1"/>
          <% if (editing != null) { %>
            <input type="hidden" name="id" value="<%=editing.getId()%>"/>
          <% } %>

          <label>Nama</label>
          <input name="name" required value="<%= (editing!=null? editing.getName() : "") %>" placeholder="Contoh: Cabai Rawit"/>

          <label>Unit</label>
          <input name="unit" required value="<%= (editing!=null? editing.getUnit() : "") %>" placeholder="Contoh: pcs / gram / pack"/>

          <% if (editing == null) { %>
            <label>Stok Awal</label>
            <input name="stock" type="number" min="0" value="0"/>
            <div class="muted">Stok berikutnya sebaiknya berubah lewat transaksi (IN/OUT).</div>
          <% } else { %>
            <label>Stok Saat Ini</label>
            <input value="<%=editing.getStock()%>" disabled />
            <div class="muted">Untuk menambah/mengurangi stok, gunakan menu Transaksi.</div>
          <% } %>

          <button class="btn" type="submit"><%= (editing == null ? "Simpan" : "Update") %></button>
          <% if (editing != null) { %>
            <a class="btn" href="<%=request.getContextPath()%>/seed_stock.jsp" style="margin-left:8px;">Batal</a>
          <% } %>
        </form>
      </div>

      <div class="card">
        <h3 style="margin-top:0">Daftar Bibit</h3>

        <table>
          <thead>
            <tr>
              <th>Nama</th>
              <th>Unit</th>
              <th>Stok</th>
              <th>Updated</th>
              <th>Aksi</th>
            </tr>
          </thead>
          <tbody>
            <% if (items == null || items.isEmpty()) { %>
              <tr><td colspan="5" class="muted">Belum ada data.</td></tr>
            <% } else { 
                 for (SeedStock s : items) { %>
              <tr>
                <td><b><%=s.getName()%></b></td>
                <td><%=s.getUnit()%></td>
                <td><%=s.getStock()%></td>
                <td class="muted"><%= (s.getUpdatedAt()!=null? s.getUpdatedAt().toString() : "-") %></td>
                <td>
                  <div class="rowbtn">
                    <a class="btn" href="<%=request.getContextPath()%>/seed_stock.jsp?edit=<%=s.getId()%>">Edit</a>
                    <a class="btn" href="<%=request.getContextPath()%>/seed_transactions.jsp?stock_id=<%=s.getId()%>">Transaksi</a>
                    <form method="POST" action="<%=request.getContextPath()%>/seed-stock/delete" style="display:inline" onsubmit="return confirm('Hapus bibit ini? (transaksi ikut terhapus)');">
                      <input type="hidden" name="redirect" value="1"/>
                      <input type="hidden" name="id" value="<%=s.getId()%>"/>
                      <button class="btn" type="submit">Hapus</button>
                    </form>
                  </div>
                </td>
              </tr>
            <% } } %>
          </tbody>
        </table>
      </div>

    </div>
  <% } %>

</body>
</html>
