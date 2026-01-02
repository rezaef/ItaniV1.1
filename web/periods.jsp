<%-- 
    Document   : periods
    Created on : 2 Jan 2026, 10.06.50
    Author     : rezaef
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="java.lang.reflect.*"%>
<%@page import="models.User"%>
<%@page import="models.Period"%>
<%@page import="dao.PeriodDAO"%>

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

  List<Period> periods = new PeriodDAO().findAllWithStats(uid, isAdmin);

  String msg = request.getParameter("msg");
  String err = request.getParameter("err");
%>

<!doctype html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>ITani - Periode</title>
  <style>
    body{font-family:Arial;padding:24px;}
    .top{display:flex;justify-content:space-between;align-items:center;}
    .card{border:1px solid #ddd;border-radius:12px;padding:14px;margin-top:14px;}
    a.btn,button.btn{padding:8px 12px;border:1px solid #ddd;border-radius:10px;text-decoration:none;background:#fff;cursor:pointer;}
    table{width:100%;border-collapse:collapse;margin-top:12px;}
    th,td{border-bottom:1px solid #eee;padding:10px;text-align:left;}
    input,select,textarea{padding:8px;border:1px solid #ddd;border-radius:10px;min-width:220px;}
    textarea{min-width:320px;min-height:70px;}
    .row{display:flex;gap:10px;flex-wrap:wrap;}
    .msg{padding:10px;border-radius:10px;margin-top:12px;}
    .ok{background:#eaffea;border:1px solid #b8f2b8;}
    .er{background:#ffecec;border:1px solid #ffb7b7;}
  </style>
</head>
<body>

<div class="top">
  <div>
    <h2>Periode</h2>
    <div>Login: <b><%=u.getName()%></b> (<%=u.getRole()%>)</div>
    <div style="margin-top:10px;display:flex;gap:10px;flex-wrap:wrap;">
      <a class="btn" href="<%=request.getContextPath()%>/dashboard.jsp">← Dashboard</a>
      <a class="btn" href="<%=request.getContextPath()%>/seed_stock.jsp">Seed Stock</a>
      <a class="btn" href="<%=request.getContextPath()%>/fertilizer_stock.jsp">Fertilizer Stock</a>
    </div>
  </div>
  <a class="btn" href="<%=request.getContextPath()%>/api/auth/logout">Logout</a>
</div>

<% if (msg != null) { %><div class="msg ok"><%=msg%></div><% } %>
<% if (err != null) { %><div class="msg er"><%=err%></div><% } %>

<div class="card">
  <h3 id="formTitle">Tambah Periode</h3>
  <form method="post" action="<%=request.getContextPath()%>/period/save" id="periodForm">
    <input type="hidden" name="id" id="pid"/>

    <div class="row">
      <div>
        <label>Nama Periode</label><br/>
        <input name="nama_periode" id="pnama" placeholder="contoh: Periode Cabai Jan 2026" required/>
      </div>

      <div>
        <label>Tanggal Mulai</label><br/>
        <input type="datetime-local" name="tanggal_mulai" id="pmulai"/>
      </div>

      <div>
        <label>Tanggal Selesai</label><br/>
        <input type="datetime-local" name="tanggal_selesai" id="pselesai"/>
      </div>

      <div>
        <label>Status</label><br/>
        <select name="status" id="pstatus">
          <option value="aktif">aktif</option>
          <option value="selesai">selesai</option>
          <option value="batal">batal</option>
        </select>
      </div>
    </div>

    <div style="margin-top:10px;">
      <label>Deskripsi</label><br/>
      <textarea name="deskripsi" id="pdesc" placeholder="opsional"></textarea>
    </div>

    <div style="margin-top:12px;display:flex;gap:10px;">
      <button class="btn" type="submit">Simpan</button>
      <button class="btn" type="button" onclick="resetForm()">Reset</button>
    </div>
  </form>
</div>

<div class="card">
  <h3>Daftar Periode</h3>
  <table>
    <thead>
      <tr>
        <th>ID</th>
        <% if (isAdmin) { %><th>User ID</th><% } %>
        <th>Nama</th>
        <th>Mulai</th>
        <th>Selesai</th>
        <th>Status</th>
        <th>Jumlah Panen</th>
        <th>Total Panen</th>
        <th>Aksi</th>
      </tr>
    </thead>
    <tbody>
      <% for (Period p : periods) {
           String mulai = (p.getTanggalMulai()==null) ? "" : p.getTanggalMulai().toLocalDateTime().toString();
           String selesai = (p.getTanggalSelesai()==null) ? "" : p.getTanggalSelesai().toLocalDateTime().toString();
           String inMulai = mulai.length() >= 16 ? mulai.substring(0,16) : "";
           String inSelesai = selesai.length() >= 16 ? selesai.substring(0,16) : "";
      %>
      <tr
        data-id="<%=p.getId()%>"
        data-nama="<%= (p.getNamaPeriode()==null?"":p.getNamaPeriode().replace("\"","&quot;")) %>"
        data-mulai="<%=inMulai%>"
        data-selesai="<%=inSelesai%>"
        data-status="<%= (p.getStatus()==null?"aktif":p.getStatus()) %>"
        data-desc="<%= (p.getDeskripsi()==null?"":p.getDeskripsi().replace("\"","&quot;")) %>"
      >
        <td><%=p.getId()%></td>
        <% if (isAdmin) { %><td><%=p.getUserId()%></td><% } %>
        <td><%=p.getNamaPeriode()%></td>
        <td><%= mulai.isEmpty() ? "-" : mulai.replace("T"," ") %></td>
        <td><%= selesai.isEmpty() ? "-" : selesai.replace("T"," ") %></td>
        <td><%=p.getStatus()%></td>
        <td><%=p.getHarvestCount()%></td>
        <td><%=p.getHarvestTotal()%></td>
        <td style="display:flex;gap:8px;flex-wrap:wrap;">
          <a class="btn" href="<%=request.getContextPath()%>/harvests.jsp?periode_id=<%=p.getId()%>">Panen</a>
          <button class="btn" type="button" onclick="editRow(this)">Edit</button>
          <form method="post" action="<%=request.getContextPath()%>/period/delete" onsubmit="return confirm('Hapus periode ini?')">
            <input type="hidden" name="id" value="<%=p.getId()%>"/>
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
  document.getElementById("formTitle").textContent = "Edit Periode";
  document.getElementById("pid").value = tr.dataset.id;
  document.getElementById("pnama").value = tr.dataset.nama || "";
  document.getElementById("pmulai").value = tr.dataset.mulai || "";
  document.getElementById("pselesai").value = tr.dataset.selesai || "";
  document.getElementById("pstatus").value = tr.dataset.status || "aktif";
  document.getElementById("pdesc").value = tr.dataset.desc || "";
  window.scrollTo({top:0, behavior:"smooth"});
}
function resetForm(){
  document.getElementById("formTitle").textContent = "Tambah Periode";
  document.getElementById("pid").value = "";
  document.getElementById("pnama").value = "";
  document.getElementById("pmulai").value = "";
  document.getElementById("pselesai").value = "";
  document.getElementById("pstatus").value = "aktif";
  document.getElementById("pdesc").value = "";
}
</script>

</body>
</html>
