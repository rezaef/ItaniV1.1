<%-- 
    Document   : rekap
    Created on : 3 Jan 2026, 21.08.50
    Author     : rezaef
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="models.User"%>
<%@page import="models.RekapManajemen"%>
<%@page import="models.Harvest"%>
<%@page import="models.SeedStock"%>
<%@page import="models.Sensor"%>

<%
  User u = (User) session.getAttribute("user");
  if (u == null) { response.sendRedirect(request.getContextPath()+"/index.jsp"); return; }
  boolean isAdmin = "Admin".equalsIgnoreCase(u.getRole());

  RekapManajemen rm = new RekapManajemen();
  rm.load(u.getId(), isAdmin);

  List<Harvest> panen = rm.getDaftarPanen();
  List<SeedStock> bibit = rm.getDaftarBibit();
  List<Sensor> sensors = rm.getDaftarSensor();

  String ok = request.getParameter("ok");
%>

<!doctype html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>ITani - Rekap</title>
  <style>
    body { font-family: Arial; padding: 24px; }
    .top { display:flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
    a.btn, button.btn { padding:8px 12px; border:1px solid #ddd; border-radius:10px; text-decoration:none; background:#fff; cursor:pointer; }
    .grid { display:grid; grid-template-columns: 1fr 1fr; gap: 18px; }
    .card { border:1px solid #eee; border-radius:14px; padding:14px; }
    table { width:100%; border-collapse: collapse; }
    th, td { border-bottom:1px solid #eee; padding:10px 8px; text-align:left; font-size:14px; }
    th { background:#fafafa; }
    .muted { color:#777; font-size: 13px; }
    .badge { display:inline-block; padding:4px 8px; border-radius:999px; font-size:12px; border:1px solid #ddd; background:#fff; }
    .lvl { font-size:11px; padding:2px 8px; border-radius:999px; border:1px solid #ddd; }
    .lvl.warn { border-color:#f59e0b; color:#b45309; background:#fffbeb; }
    .lvl.danger { border-color:#ef4444; color:#b91c1c; background:#fef2f2; }
    .lvl.normal { border-color:#10b981; color:#047857; background:#ecfdf5; }
    .rowbtn { display:flex; gap:8px; flex-wrap: wrap; }
  </style>
</head>
<body>

  <div class="top">
    <div>
      <h2 style="margin:0">Rekap Manajemen</h2>
      <div class="muted">Login sebagai: <b><%=u.getName()%></b> (<%=u.getRole()%>)</div>
    </div>
    <div class="rowbtn">
      <a class="btn" href="<%=request.getContextPath()%>/dashboard.jsp">Dashboard</a>
      <a class="btn" href="<%=request.getContextPath()%>/laporan.jsp">Laporan</a>
      <a class="btn" href="<%=request.getContextPath()%>/api/auth/logout">Logout</a>
    </div>
  </div>

  <% if (ok != null) { %>
    <div style="background:#ecfdf5;border:1px solid #34d399;padding:10px;border-radius:10px;margin:10px 0;">
      Berhasil.
    </div>
  <% } %>

  <div class="card" style="margin-bottom:18px;">
    <div style="display:flex; justify-content:space-between; align-items:center; gap:12px; flex-wrap:wrap;">
      <div>
        <b>Ringkasan</b>
        <div class="muted" style="margin-top:6px;">
          <div>• <%=rm.tampilkanRekapPanen()%></div>
          <div>• <%=rm.tampilkanRekapStok()%></div>
          <div>• <%=rm.tampilkanRekapSensor()%></div>
        </div>
      </div>
      <div class="rowbtn">
        <a class="btn" href="<%=request.getContextPath()%>/laporan/generate?redirect=1&next=/laporan.jsp">Generate Laporan</a>
      </div>
    </div>
  </div>

  <div class="grid">

    <div class="card">
      <h3 style="margin-top:0">Sensor (latest)</h3>
      <table>
        <thead>
          <tr>
            <th>Sensor</th>
            <th>Nilai</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
        <% if (sensors == null || sensors.isEmpty()) { %>
          <tr><td colspan="3" class="muted">Belum ada data sensor.</td></tr>
        <% } else {
             for (Sensor s : sensors) {
                String st = (s.getStatus() == null ? "NORMAL" : s.getStatus().toUpperCase());
                String cls = "normal";
                if ("WARNING".equals(st)) cls = "warn";
                if ("DANGER".equals(st)) cls = "danger";
        %>
          <tr>
            <td><b><%=s.getNamaSensor()%></b></td>
            <td><%= (s.getNilaiSensor()==null? "-" : s.getNilaiSensor()) %> <%= (s.getSatuan()==null?"":s.getSatuan()) %></td>
            <td><span class="lvl <%=cls%>"><%=st%></span></td>
          </tr>
        <%   }
           } %>
        </tbody>
      </table>
      <div class="muted" style="margin-top:10px;">Catatan: status dihitung dari threshold WARNING/DANGER yang sama dengan notifikasi.</div>
    </div>

    <div class="card">
      <h3 style="margin-top:0">Stok Bibit</h3>
      <table>
        <thead>
          <tr>
            <th>Nama</th>
            <th>Stok</th>
            <th>Unit</th>
          </tr>
        </thead>
        <tbody>
        <% if (bibit == null || bibit.isEmpty()) { %>
          <tr><td colspan="3" class="muted">Belum ada data stok bibit.</td></tr>
        <% } else {
             int max = Math.min(10, bibit.size());
             for (int i=0; i<max; i++) {
               SeedStock b = bibit.get(i);
        %>
          <tr>
            <td><b><%=b.getName()%></b></td>
            <td><%=b.getStock()%></td>
            <td><%=b.getUnit()%></td>
          </tr>
        <%   }
           } %>
        </tbody>
      </table>
      <div class="muted" style="margin-top:10px;">Menampilkan 10 item terbaru.</div>
    </div>

    <div class="card" style="grid-column: 1 / span 2;">
      <h3 style="margin-top:0">Panen (10 terakhir)</h3>
      <table>
        <thead>
          <tr>
            <th>Tanggal</th>
            <th>Tanaman</th>
            <th>Jumlah</th>
            <th>Catatan</th>
          </tr>
        </thead>
        <tbody>
        <% if (panen == null || panen.isEmpty()) { %>
          <tr><td colspan="4" class="muted">Belum ada data panen.</td></tr>
        <% } else {
             for (Harvest h : panen) { %>
          <tr>
            <td><%=h.getTanggalPanen()%></td>
            <td><b><%=h.getJenisTanaman()%></b></td>
            <td><%=h.getJumlahPanen()%></td>
            <td><%= (h.getCatatan()==null?"":h.getCatatan()) %></td>
          </tr>
        <%   }
           } %>
        </tbody>
      </table>
    </div>

  </div>

</body>
</html>
