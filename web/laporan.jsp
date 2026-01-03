<%-- 
    Document   : laporan
    Created on : 3 Jan 2026, 21.08.55
    Author     : rezaef
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="models.User"%>
<%@page import="models.Laporan"%>
<%@page import="dao.LaporanDAO"%>

<%
  User u = (User) session.getAttribute("user");
  if (u == null) { response.sendRedirect(request.getContextPath()+"/index.jsp"); return; }
  boolean isAdmin = "Admin".equalsIgnoreCase(u.getRole());

  String ok = request.getParameter("ok");
  String err = request.getParameter("err");

  int viewId = -1;
  try { viewId = Integer.parseInt(request.getParameter("view")); } catch(Exception ex) {}

  LaporanDAO dao = new LaporanDAO();
  List<Laporan> list = dao.listLatest(30, u.getId(), isAdmin);
  Laporan selected = null;
  if (viewId > 0) {
    selected = dao.findById(viewId, u.getId(), isAdmin);
  }
%>

<!doctype html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>ITani - Laporan</title>
  <style>
    body { font-family: Arial; padding: 24px; }
    .top { display:flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
    a.btn, button.btn { padding:8px 12px; border:1px solid #ddd; border-radius:10px; text-decoration:none; background:#fff; cursor:pointer; }
    .grid { display:grid; grid-template-columns: 1fr 1.2fr; gap: 18px; }
    .card { border:1px solid #eee; border-radius:14px; padding:14px; }
    table { width:100%; border-collapse: collapse; }
    th, td { border-bottom:1px solid #eee; padding:10px 8px; text-align:left; font-size:14px; }
    th { background:#fafafa; }
    .muted { color:#777; font-size: 13px; }
    pre { white-space: pre-wrap; word-break: break-word; background:#fafafa; border:1px solid #eee; border-radius:10px; padding:12px; }
    .msg { margin: 10px 0; padding: 10px; border-radius: 10px; }
    .ok { background:#e9ffe9; border:1px solid #b7f5b7; }
    .err { background:#ffe9e9; border:1px solid #f5b7b7; }
    .rowbtn { display:flex; gap:8px; flex-wrap: wrap; }
    .badge { display:inline-block; padding:4px 8px; border-radius:999px; font-size:12px; border:1px solid #ddd; background:#fff; }
  </style>
</head>
<body>

  <div class="top">
    <div>
      <h2 style="margin:0">Laporan</h2>
      <div class="muted">Login sebagai: <b><%=u.getName()%></b> (<%=u.getRole()%>)</div>
    </div>
    <div class="rowbtn">
      <a class="btn" href="<%=request.getContextPath()%>/dashboard.jsp">Dashboard</a>
      <a class="btn" href="<%=request.getContextPath()%>/rekap.jsp">Rekap</a>
      <a class="btn" href="<%=request.getContextPath()%>/api/auth/logout">Logout</a>
    </div>
  </div>

  <% if (ok != null) { %><div class="msg ok">Berhasil.</div><% } %>
  <% if (err != null) { %><div class="msg err">Gagal.</div><% } %>

  <div class="card" style="margin-bottom:18px;">
    <div style="display:flex; justify-content:space-between; align-items:center; gap:12px; flex-wrap:wrap;">
      <div>
        <b>Buat laporan rekap otomatis</b>
        <div class="muted" style="margin-top:6px;">Laporan di-generate dari data rekap (sensor, stok, panen) lalu disimpan ke DB.</div>
      </div>
      <div class="rowbtn">
        <a class="btn" href="<%=request.getContextPath()%>/laporan/generate?redirect=1&next=/laporan.jsp">Generate Laporan</a>
      </div>
    </div>
  </div>

  <div class="grid">

    <div class="card">
      <h3 style="margin-top:0">Daftar Laporan (30 terakhir)</h3>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Tanggal</th>
            <% if (isAdmin) { %><th>Dibuat oleh</th><% } %>
            <th>Aksi</th>
          </tr>
        </thead>
        <tbody>
        <% if (list == null || list.isEmpty()) { %>
          <tr><td colspan="<%= (isAdmin ? 4 : 3) %>" class="muted">Belum ada laporan.</td></tr>
        <% } else {
             for (Laporan l : list) {
               // idLaporan = LAP-XXXX
               String num = l.getIdLaporan().replace("LAP-", "");
        %>
          <tr>
            <td><b><%=l.getIdLaporan()%></b></td>
            <td><%=l.getTanggalLaporan()%></td>
            <% if (isAdmin) { %>
              <td><%= (l.getDibuatOleh()==null? "-" : l.getDibuatOleh()) %></td>
            <% } %>
            <td>
              <a class="btn" href="<%=request.getContextPath()%>/laporan.jsp?view=<%=Integer.parseInt(num)%>">Lihat</a>
            </td>
          </tr>
        <%   }
           } %>
        </tbody>
      </table>
    </div>

    <div class="card">
      <h3 style="margin-top:0">Detail Laporan</h3>
      <% if (selected == null) { %>
        <div class="muted">Pilih laporan di sebelah kiri untuk melihat isi.</div>
      <% } else { %>
        <div class="rowbtn" style="margin-bottom:10px;">
          <button class="btn" onclick="window.print()">Print</button>
        </div>
        <div class="muted" style="margin-bottom:8px;">
          <b><%=selected.getIdLaporan()%></b> • <%=selected.getTanggalLaporan()%>
          <% if (isAdmin) { %>
            • dibuat oleh: <b><%= (selected.getDibuatOleh()==null? "-" : selected.getDibuatOleh()) %></b>
          <% } %>
        </div>
        <pre><%=selected.getIsiLaporan()%></pre>
      <% } %>
    </div>

  </div>

</body>
</html>
