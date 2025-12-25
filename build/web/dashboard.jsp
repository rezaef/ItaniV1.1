<%-- 
    Document   : dashboard
    Created on : 24 Des 2025, 12.14.41
    Author     : rezaef
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="models.User"%>
<%
  User u = (User) session.getAttribute("user");
%>
<!doctype html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>ITani - Dashboard</title>
  <style>
    body { font-family: Arial; padding: 24px; }
    .top { display:flex; justify-content:space-between; align-items:center; }
    .grid { display:grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap:12px; margin-top:14px; }
    .card { border:1px solid #ddd; border-radius:12px; padding:14px; }
    .val { font-size:22px; font-weight:bold; margin-top:8px; }
    a.btn { padding:8px 12px; border:1px solid #ddd; border-radius:10px; text-decoration:none; }
  </style>
</head>
<body>
  <div class="top">
    <div>
      <h2>Dashboard</h2>
      <div>Login sebagai: <b><%=u.getName()%></b> (<%=u.getRole()%>)</div>
    </div>
    <a class="btn" href="<%=request.getContextPath()%>/api/auth/logout">Logout</a>
  </div>

  <div class="grid">
    <div class="card"><div>pH</div><div class="val" id="ph">-</div></div>
    <div class="card"><div>Moisture</div><div class="val" id="sm">-</div></div>
    <div class="card"><div>Temp</div><div class="val" id="st">-</div></div>
    <div class="card"><div>EC</div><div class="val" id="ec">-</div></div>
    <div class="card"><div>N</div><div class="val" id="n">-</div></div>
    <div class="card"><div>P</div><div class="val" id="p">-</div></div>
    <div class="card"><div>K</div><div class="val" id="k">-</div></div>
    <div class="card"><div>Waktu</div><div class="val" style="font-size:14px" id="t">-</div></div>
  </div>

<script>
async function loadLatest(){
  const res = await fetch('<%=request.getContextPath()%>/api/sensors/latest');
  const json = await res.json().catch(()=>null);
  if(!json || !json.ok) return;

  const d = json.data;
  if(!d){
    document.getElementById('t').textContent = 'Belum ada data sensor';
    return;
  }
  document.getElementById('ph').textContent = d.ph ?? '-';
  document.getElementById('sm').textContent = d.soil_moisture ?? '-';
  document.getElementById('st').textContent = d.soil_temp ?? '-';
  document.getElementById('ec').textContent = d.ec ?? '-';
  document.getElementById('n').textContent = d.n ?? '-';
  document.getElementById('p').textContent = d.p ?? '-';
  document.getElementById('k').textContent = d.k ?? '-';
  document.getElementById('t').textContent = d.reading_time ?? '-';
}
loadLatest();
setInterval(loadLatest, 3000);
</script>
</body>
</html>
