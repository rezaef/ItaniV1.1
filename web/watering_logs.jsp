<%-- 
    Document   : watering_logs
    Created on : 2 Jan 2026, 12.41.25
    Author     : rezaef
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="models.User"%>
<%@page import="dao.WateringLogDAO"%>
<%@page import="models.WateringLog"%>

<%
  User u = (User) session.getAttribute("user");
  List<WateringLog> logs = new WateringLogDAO().listLatest(100);

  String msg = request.getParameter("msg");
  String err = request.getParameter("err");
%>

<!doctype html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>ITani - Riwayat Penyiraman</title>
  <style>
    body { font-family: Arial; padding: 24px; }
    .row { display:flex; gap:12px; flex-wrap:wrap; }
    .card { border:1px solid #ddd; border-radius:12px; padding:16px; margin: 12px 0; }
    .btn { padding:10px 14px; border:1px solid #ccc; border-radius:10px; cursor:pointer; background:#fff; text-decoration:none; display:inline-block; }
    .btn.primary { border-color:#6b21a8; color:#6b21a8; }
    .msg { padding:10px 12px; border-radius:10px; margin: 10px 0; }
    .msg.ok { background:#ecfdf5; border:1px solid #34d399; }
    .msg.err { background:#fef2f2; border:1px solid #f87171; }
    table { width:100%; border-collapse: collapse; }
    th, td { padding:10px; border-bottom:1px solid #eee; text-align:left; }
    .topbar { display:flex; justify-content:space-between; align-items:center; gap:12px; }
  </style>
</head>
<body>

  <div class="topbar">
    <div>
      <h2>Riwayat Penyiraman</h2>
      <div>Login: <b><%= (u==null? "-" : u.getUsername()) %></b> (<%= (u==null? "-" : u.getRole()) %>)</div>
      <div class="row" style="margin-top:10px;">
        <a class="btn" href="<%=request.getContextPath()%>/dashboard.jsp">Dashboard</a>
        <a class="btn" href="<%=request.getContextPath()%>/periods.jsp">Periode</a>
        <a class="btn" href="<%=request.getContextPath()%>/harvests.jsp">Panen</a>
        <a class="btn" href="<%=request.getContextPath()%>/seed_stock.jsp">Seed Stock</a>
        <a class="btn" href="<%=request.getContextPath()%>/fertilizer_stock.jsp">Fertilizer Stock</a>
      </div>
    </div>

    <form method="POST" action="<%=request.getContextPath()%>/api/auth/logout">
      <input type="hidden" name="redirect" value="1"/>
      <button class="btn primary" type="submit">Logout</button>
    </form>
  </div>

  <% if (msg != null) { %><div class="msg ok"><%=msg%></div><% } %>
  <% if (err != null) { %><div class="msg err"><%=err%></div><% } %>

  <div class="card">
    <h3>Watering Logs</h3>
    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>Mode</th>
          <th>Action</th>
          <th>Source</th>
          <th>Note</th>
          <th>Tanggal</th>
        </tr>
      </thead>
      <tbody>
      <%
        if (logs == null || logs.isEmpty()) {
      %>
        <tr><td colspan="6">Belum ada data.</td></tr>
      <%
        } else {
          for (WateringLog w : logs) {
            String tgl = "-";
            if (w.getCreatedAt() != null) {
              String s = w.getCreatedAt().toString();
              tgl = (s.length() >= 10) ? s.substring(0,10) : s; // tanggal saja
            }
      %>
        <tr>
          <td><%=w.getId()%></td>
          <td><%=w.getMode()%></td>
          <td><b><%=w.getAction()%></b></td>
          <td><%=w.getSource()%></td>
          <td><%=w.getNote()==null?"":w.getNote()%></td>
          <td><%=tgl%></td>
        </tr>
      <%
          }
        }
      %>
      </tbody>
    </table>
  </div>

</body>
</html>
