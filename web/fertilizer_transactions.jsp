<%-- 
    Document   : fertilizer_transaction
    Created on : 2 Jan 2026, 10.26.09
    Author     : rezaef
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="models.User"%>
<%@page import="models.FertilizerStock"%>
<%@page import="models.FertilizerStockTransaction"%>
<%@page import="dao.FertilizerStockDAO"%>
<%@page import="dao.FertilizerStockTransactionDAO"%>

<%
  User u = (User) session.getAttribute("user");

  int stockId = 0;
  try { stockId = Integer.parseInt(request.getParameter("stock_id")); } catch(Exception e){}

  FertilizerStock item = (stockId > 0) ? new FertilizerStockDAO().findById(stockId) : null;
  List<FertilizerStockTransaction> txs = (stockId > 0) ? new FertilizerStockTransactionDAO().listByStock(stockId) : new ArrayList<>();

  String msg = request.getParameter("msg");
  String err = request.getParameter("err");
%>

<!doctype html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>ITani - Fertilizer Transactions</title>
  <style>
    body{font-family:Arial;padding:24px;}
    .top{display:flex;justify-content:space-between;align-items:center;}
    .card{border:1px solid #ddd;border-radius:12px;padding:14px;margin-top:14px;}
    a.btn,button.btn{padding:8px 12px;border:1px solid #ddd;border-radius:10px;text-decoration:none;background:#fff;cursor:pointer;}
    table{width:100%;border-collapse:collapse;margin-top:12px;}
    th,td{border-bottom:1px solid #eee;padding:10px;text-align:left;}
    input,select{padding:8px;border:1px solid #ddd;border-radius:10px;min-width:220px;}
    .row{display:flex;gap:10px;flex-wrap:wrap;}
    .msg{padding:10px;border-radius:10px;margin-top:12px;}
    .ok{background:#eaffea;border:1px solid #b8f2b8;}
    .er{background:#ffecec;border:1px solid #ffb7b7;}
  </style>
</head>
<body>

<div class="top">
  <div>
    <h2>Transaksi Pupuk</h2>
    <div>Login: <b><%=u.getName()%></b> (<%=u.getRole()%>)</div>
    <div style="margin-top:10px;">
      <a class="btn" href="<%=request.getContextPath()%>/fertilizer_stock.jsp">← Fertilizer Stock</a>
    </div>
  </div>
  <a class="btn" href="<%=request.getContextPath()%>/api/auth/logout">Logout</a>
</div>

<% if (msg != null) { %><div class="msg ok"><%=msg%></div><% } %>
<% if (err != null) { %><div class="msg er"><%=err%></div><% } %>

<div class="card">
  <% if (item == null) { %>
    <b>Item pupuk tidak ditemukan.</b>
  <% } else { %>
    <h3><%=item.getName()%> (ID <%=item.getId()%>)</h3>
    <div>Stok saat ini: <b><%=item.getStock()%></b> <%=item.getUnit()%></div>
  <% } %>
</div>

<% if (item != null) { %>
<div class="card">
  <h3>Tambah Transaksi</h3>
  <form method="post" action="<%=request.getContextPath()%>/fertilizer-stock/tx/create">
    <input type="hidden" name="stock_id" value="<%=stockId%>"/>
    <div class="row">
      <div>
        <label>Type</label><br/>
        <select name="type">
          <option value="IN">IN</option>
          <option value="OUT">OUT</option>
        </select>
      </div>
      <div>
        <label>Qty</label><br/>
        <input name="qty" type="number" step="0.01" required/>
      </div>
      <div>
        <label>Note</label><br/>
        <input name="note" placeholder="opsional"/>
      </div>
    </div>
    <div style="margin-top:12px;">
      <button class="btn" type="submit">Simpan</button>
    </div>
  </form>
</div>

<div class="card">
  <h3>Riwayat Transaksi</h3>
  <table>
    <thead>
      <tr><th>ID</th><th>Type</th><th>Qty</th><th>Note</th><th>Waktu</th></tr>
    </thead>
    <tbody>
      <% for (FertilizerStockTransaction t : txs) { %>
      <tr>
        <td><%=t.getId()%></td>
        <td><%=t.getType()%></td>
        <td><%=t.getQty()%></td>
        <td><%=t.getNote()==null?"":t.getNote()%></td>
        <td><%=t.getCreatedAt()==null?"-":t.getCreatedAt().toString()%></td>
      </tr>
      <% } %>
    </tbody>
  </table>
</div>
<% } %>

</body>
</html>
