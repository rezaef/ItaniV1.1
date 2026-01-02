<%-- 
    Document   : fertilizer_stock
    Created on : 2 Jan 2026, 10.25.36
    Author     : rezaef
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="models.User"%>
<%@page import="models.FertilizerStock"%>
<%@page import="dao.FertilizerStockDAO"%>

<%
  User u = (User) session.getAttribute("user");
  List<FertilizerStock> items = new FertilizerStockDAO().findAll();
  String msg = request.getParameter("msg");
  String err = request.getParameter("err");
%>

<!doctype html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>ITani - Fertilizer Stock</title>
  <style>
    body{font-family:Arial;padding:24px;}
    .top{display:flex;justify-content:space-between;align-items:center;}
    .card{border:1px solid #ddd;border-radius:12px;padding:14px;margin-top:14px;}
    a.btn,button.btn{padding:8px 12px;border:1px solid #ddd;border-radius:10px;text-decoration:none;background:#fff;cursor:pointer;}
    table{width:100%;border-collapse:collapse;margin-top:12px;}
    th,td{border-bottom:1px solid #eee;padding:10px;text-align:left;}
    input{padding:8px;border:1px solid #ddd;border-radius:10px;min-width:220px;}
    .row{display:flex;gap:10px;flex-wrap:wrap;}
    .msg{padding:10px;border-radius:10px;margin-top:12px;}
    .ok{background:#eaffea;border:1px solid #b8f2b8;}
    .er{background:#ffecec;border:1px solid #ffb7b7;}
  </style>
</head>
<body>

<div class="top">
  <div>
    <h2>Fertilizer Stock</h2>
    <div>Login: <b><%=u.getName()%></b> (<%=u.getRole()%>)</div>
    <div style="margin-top:10px;display:flex;gap:10px;flex-wrap:wrap;">
      <a class="btn" href="<%=request.getContextPath()%>/dashboard.jsp">← Dashboard</a>
      <a class="btn" href="<%=request.getContextPath()%>/seed_stock.jsp">Seed Stock</a>
      <a class="btn" href="<%=request.getContextPath()%>/periods.jsp">Periode</a>
    </div>
  </div>
  <a class="btn" href="<%=request.getContextPath()%>/api/auth/logout">Logout</a>
</div>

<% if (msg != null) { %><div class="msg ok"><%=msg%></div><% } %>
<% if (err != null) { %><div class="msg er"><%=err%></div><% } %>

<div class="card">
  <h3 id="formTitle">Tambah Pupuk</h3>
  <form method="post" action="<%=request.getContextPath()%>/fertilizer-stock/save">
    <input type="hidden" name="id" id="fid"/>
    <div class="row">
      <div>
        <label>Nama</label><br/>
        <input name="name" id="fname" placeholder="contoh: NPK 16-16-16" required/>
      </div>
      <div>
        <label>Unit</label><br/>
        <input name="unit" id="funit" placeholder="kg / liter / pcs"/>
      </div>
      <div>
        <label>Stok Awal (hanya saat tambah)</label><br/>
        <input name="stock" id="fstock" type="number" step="0.01" placeholder="0"/>
      </div>
    </div>
    <div style="margin-top:12px;display:flex;gap:10px;">
      <button class="btn" type="submit">Simpan</button>
      <button class="btn" type="button" onclick="resetForm()">Reset</button>
    </div>
  </form>
</div>

<div class="card">
  <h3>Daftar Pupuk</h3>
  <table>
    <thead>
      <tr>
        <th>ID</th><th>Nama</th><th>Unit</th><th>Stok</th><th>Aksi</th>
      </tr>
    </thead>
    <tbody>
      <% for (FertilizerStock s : items) { %>
      <tr data-id="<%=s.getId()%>" data-name="<%=s.getName()%>" data-unit="<%=s.getUnit()%>">
        <td><%=s.getId()%></td>
        <td><%=s.getName()%></td>
        <td><%=s.getUnit()%></td>
        <td><%=s.getStock()%></td>
        <td style="display:flex;gap:8px;flex-wrap:wrap;">
          <a class="btn" href="<%=request.getContextPath()%>/fertilizer_transactions.jsp?stock_id=<%=s.getId()%>">Transaksi</a>
          <button class="btn" type="button" onclick="editRow(this)">Edit</button>
          <form method="post" action="<%=request.getContextPath()%>/fertilizer-stock/delete" onsubmit="return confirm('Hapus item ini?')">
            <input type="hidden" name="id" value="<%=s.getId()%>"/>
            <button class="btn" type="submit">Hapus</button>
          </form>
        </td>
      </tr>
      <% } %>
    </tbody>
  </table>
</div>

<script>
function editRow(btn){
  const tr = btn.closest("tr");
  document.getElementById("formTitle").textContent = "Edit Pupuk";
  document.getElementById("fid").value = tr.dataset.id;
  document.getElementById("fname").value = tr.dataset.name || "";
  document.getElementById("funit").value = tr.dataset.unit || "";
  document.getElementById("fstock").value = ""; // stok awal tidak dipakai saat edit
  window.scrollTo({top:0, behavior:"smooth"});
}
function resetForm(){
  document.getElementById("formTitle").textContent = "Tambah Pupuk";
  document.getElementById("fid").value = "";
  document.getElementById("fname").value = "";
  document.getElementById("funit").value = "";
  document.getElementById("fstock").value = "";
}
</script>

</body>
</html>
