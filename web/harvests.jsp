<%-- 
    Document   : harvests
    Created on : 2 Jan 2026, 10.07.13
    Author     : rezaef
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="java.lang.reflect.*"%>
<%@page import="models.User"%>
<%@page import="models.Period"%>
<%@page import="models.Harvest"%>
<%@page import="dao.PeriodDAO"%>
<%@page import="dao.HarvestDAO"%>

<%!
  private Integer getUserId(Object user){
    if (user == null) return null;
    try {
      for (String m : new String[]{"getId","getUserId","getIdUser","getIduser"}) {
        try {
          Method mm = user.getClass().getMethod(m);
          Object r = mm.invoke(user);
          if (r instanceof Number) return ((Number)r).intValue();
        } catch(Exception ignore){}
      }
    } catch(Exception ignore){}
    return null;
  }
  private String getRole(Object user){
    if (user == null) return "";
    try {
      Method m = user.getClass().getMethod("getRole");
      Object r = m.invoke(user);
      return r == null ? "" : r.toString();
    } catch(Exception e){
      return "";
    }
  }
%>

<%
  User u = (User) session.getAttribute("user");
  Integer uid = getUserId(u);
  boolean isAdmin = "Admin".equalsIgnoreCase(getRole(u));

  int periodeId = 0;
  try { periodeId = Integer.parseInt(request.getParameter("periode_id")); } catch(Exception e){}

  Period period = (periodeId > 0) ? new PeriodDAO().findByIdWithStats(periodeId, uid, isAdmin) : null;
  List<Harvest> harvests = (period != null) ? new HarvestDAO().listByPeriode(periodeId) : new ArrayList<>();

  String msg = request.getParameter("msg");
  String err = request.getParameter("err");
%>

<!doctype html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>ITani - Panen</title>
  <style>
    body{font-family:Arial;padding:24px;}
    .top{display:flex;justify-content:space-between;align-items:center;}
    .card{border:1px solid #ddd;border-radius:12px;padding:14px;margin-top:14px;}
    a.btn,button.btn{padding:8px 12px;border:1px solid #ddd;border-radius:10px;text-decoration:none;background:#fff;cursor:pointer;}
    table{width:100%;border-collapse:collapse;margin-top:12px;}
    th,td{border-bottom:1px solid #eee;padding:10px;text-align:left;}
    input,textarea{padding:8px;border:1px solid #ddd;border-radius:10px;min-width:220px;}
    textarea{min-width:320px;min-height:60px;}
    .row{display:flex;gap:10px;flex-wrap:wrap;}
    .msg{padding:10px;border-radius:10px;margin-top:12px;}
    .ok{background:#eaffea;border:1px solid #b8f2b8;}
    .er{background:#ffecec;border:1px solid #ffb7b7;}
  </style>
</head>
<body>

<div class="top">
  <div>
    <h2>Panen</h2>
    <div>Login: <b><%=u.getName()%></b> (<%=u.getRole()%>)</div>
    <div style="margin-top:10px;">
      <a class="btn" href="<%=request.getContextPath()%>/periods.jsp">← Periode</a>
    </div>
  </div>
  <a class="btn" href="<%=request.getContextPath()%>/api/auth/logout">Logout</a>
</div>

<% if (msg != null) { %><div class="msg ok"><%=msg%></div><% } %>
<% if (err != null) { %><div class="msg er"><%=err%></div><% } %>

<div class="card">
  <% if (period == null) { %>
    <b>Periode tidak ditemukan / akses ditolak.</b>
  <% } else {
      String mulai = "-";
      if (period.getTanggalMulai() != null) {
          String s = period.getTanggalMulai().toString();
          mulai = (s.length() >= 10) ? s.substring(0, 10) : s; // yyyy-MM-dd
      }

      String selesai = "-";
      if (period.getTanggalSelesai() != null) {
          String s2 = period.getTanggalSelesai().toString();
          selesai = (s2.length() >= 10) ? s2.substring(0, 10) : s2; // yyyy-MM-dd
      }
  %>
    <h3><%=period.getNamaPeriode()%> (ID <%=period.getId()%>)</h3>
    <div>Mulai: <%=mulai%></div>
    <div>Selesai: <%=selesai%></div>
    <div>Status: <b><%=period.getStatus()%></b></div>
    <div style="margin-top:8px;">Total panen: <b><%=period.getHarvestTotal()%></b> | Record: <b><%=period.getHarvestCount()%></b></div>
  <% } %>
</div>

<% if (period != null) { %>
<div class="card">
  <h3>Tambah Panen</h3>
  <form method="post" action="<%=request.getContextPath()%>/harvest/add">
    <input type="hidden" name="periode_id" value="<%=periodeId%>"/>

    <div class="row">
      <div>
        <label>Tanggal Panen</label><br/>
        <input type="date" name="tanggal_panen"/>
      </div>
      <div>
        <label>Jenis Tanaman</label><br/>
        <input name="jenis_tanaman" placeholder="contoh: Cabai Rawit" required/>
      </div>
      <div>
        <label>Jumlah Panen</label><br/>
        <input name="jumlah_panen" type="number" step="0.01" placeholder="contoh: 12.5" required/>
      </div>
    </div>

    <div style="margin-top:10px;">
      <label>Catatan</label><br/>
      <textarea name="catatan" placeholder="opsional"></textarea>
    </div>

    <div style="margin-top:12px;">
      <button class="btn" type="submit">Simpan</button>
    </div>
  </form>
</div>

<div class="card">
  <h3>Riwayat Panen</h3>
  <table>
    <thead>
      <tr>
        <th>ID</th>
        <th>Tanggal</th>
        <th>Jenis</th>
        <th>Jumlah</th>
        <th>Catatan</th>
        <th>Aksi</th>
      </tr>
    </thead>
    <tbody>
      <%
        for (Harvest h : harvests) {
          String t = (h.getTanggalPanen()==null) ? "-" : h.getTanggalPanen().toString();
      %>
      <tr>
        <td><%=h.getId()%></td>
        <td><%=t%></td>
        <td><%=h.getJenisTanaman()%></td>
        <td><%=h.getJumlahPanen()%></td>
        <td><%=h.getCatatan()==null?"":h.getCatatan()%></td>
              <td>
          <form method="post" action="<%=request.getContextPath()%>/harvest/delete" onsubmit="return confirm('Hapus record panen ini?')">
            <input type="hidden" name="id" value="<%=h.getId()%>"/>
            <input type="hidden" name="periode_id" value="<%=periodeId%>"/>
            <button class="btn" type="submit">Hapus</button>
          </form>
        </td>
      </tr>
      <% } %>
    </tbody>
  </table>
</div>
<% } %>

</body>
</html>
