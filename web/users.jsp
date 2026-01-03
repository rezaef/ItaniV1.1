<%-- 
    Document   : users
    Created on : 4 Jan 2026, 04.19.32
    Author     : rezaef
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page import="models.User"%>
<%@page import="dao.UserDAO"%>

<%
  User me = (User) session.getAttribute("user");
  if (me == null) { response.sendRedirect(request.getContextPath()+"/index.jsp"); return; }
  boolean isAdmin = "Admin".equalsIgnoreCase(me.getRole());
  if (!isAdmin) { response.sendRedirect(request.getContextPath()+"/dashboard.jsp?err=forbidden"); return; }

  String msg = request.getParameter("msg");
  String err = request.getParameter("err");

  List<User> users = new UserDAO().findAll();
%>

<!doctype html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>ITani - Kelola User</title>
  <style>
    body { font-family: Arial; padding: 24px; }
    .top { display:flex; justify-content: space-between; align-items: center; margin-bottom: 18px; }
    a.btn, button.btn { padding:8px 12px; border:1px solid #ddd; border-radius:10px; text-decoration:none; background:#fff; cursor:pointer; }
    .card { border:1px solid #eee; border-radius:14px; padding:14px; margin-bottom: 18px; }
    table { width:100%; border-collapse: collapse; }
    th, td { border-bottom:1px solid #eee; padding:10px 8px; text-align:left; font-size:14px; vertical-align: top; }
    th { background:#fafafa; }
    input, select { padding:8px 10px; border:1px solid #ddd; border-radius:10px; width: 100%; box-sizing: border-box; }
    .grid { display:grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 10px; }
    .muted { color:#777; font-size: 13px; }
    .msg { margin: 10px 0; padding: 10px; border-radius: 10px; }
    .ok { background:#e9ffe9; border:1px solid #b7f5b7; }
    .err { background:#ffe9e9; border:1px solid #f5b7b7; }
    .actions { display:flex; gap:8px; align-items:center; flex-wrap: wrap; }
  </style>
</head>
<body>

  <div class="top">
    <div>
      <h2 style="margin:0">Kelola User</h2>
      <div class="muted">Login sebagai: <b><%=me.getName()%></b> (<%=me.getRole()%>)</div>
    </div>
    <div class="actions">
      <a class="btn" href="<%=request.getContextPath()%>/dashboard.jsp">Dashboard</a>
      <a class="btn" href="<%=request.getContextPath()%>/api/auth/logout">Logout</a>
    </div>
  </div>

  <% if (msg != null) { %><div class="msg ok"><%=msg%></div><% } %>
  <% if (err != null) { %><div class="msg err"><%=err%></div><% } %>

  <div class="card">
    <h3 style="margin-top:0">Tambah User</h3>
    <div class="muted" style="margin-bottom:12px;">Buat akun baru untuk Admin/Petani. Password disimpan ter-hash (SHA2-256).</div>
    <form method="POST" action="<%=request.getContextPath()%>/user/save">
      <div class="grid">
        <div>
          <div class="muted" style="margin-bottom:6px;">Nama</div>
          <input name="name" placeholder="Nama" required />
        </div>
        <div>
          <div class="muted" style="margin-bottom:6px;">Username</div>
          <input name="username" placeholder="username" required />
        </div>
        <div>
          <div class="muted" style="margin-bottom:6px;">Role</div>
          <select name="role" required>
            <option value="Petani">Petani</option>
            <option value="Admin">Admin</option>
          </select>
        </div>
        <div>
          <div class="muted" style="margin-bottom:6px;">Password</div>
          <input name="password" type="password" placeholder="password" required />
        </div>
      </div>
      <div style="margin-top:12px;">
        <button class="btn" type="submit">Simpan</button>
      </div>
    </form>
  </div>

  <div class="card">
    <h3 style="margin-top:0">Daftar User</h3>
    <table>
      <thead>
        <tr>
          <th style="width:60px;">ID</th>
          <th>Nama</th>
          <th>Username</th>
          <th style="width:120px;">Role</th>
          <th style="width:260px;">Aksi</th>
        </tr>
      </thead>
      <tbody>
        <% if (users == null || users.isEmpty()) { %>
          <tr><td colspan="5" class="muted">Belum ada user.</td></tr>
        <% } else {
             for (User u : users) {
        %>
          <tr>
            <td><b><%=u.getId()%></b></td>
            <td>
              <form method="POST" action="<%=request.getContextPath()%>/user/save" style="margin:0;">
                <input type="hidden" name="id" value="<%=u.getId()%>" />
                <input name="name" value="<%=u.getName()%>" required />
            </td>
            <td>
                <input name="username" value="<%=u.getUsername()%>" required />
            </td>
            <td>
                <select name="role" required>
                  <option value="Petani" <%= "Petani".equalsIgnoreCase(u.getRole()) ? "selected" : "" %>>Petani</option>
                  <option value="Admin" <%= "Admin".equalsIgnoreCase(u.getRole()) ? "selected" : "" %>>Admin</option>
                </select>
            </td>
            <td>
                <div class="muted" style="margin-bottom:6px;">Reset Password (opsional)</div>
                <div class="actions">
                  <input name="password" type="password" placeholder="kosongkan jika tidak diubah" style="flex:1; min-width:160px;" />
                  <button class="btn" type="submit">Update</button>
                </div>
              </form>

              <form method="POST" action="<%=request.getContextPath()%>/user/delete" style="margin-top:8px;" onsubmit="return confirm('Hapus user ini?');">
                <input type="hidden" name="id" value="<%=u.getId()%>" />
                <% if (u.getId() == me.getId()) { %>
                  <span class="muted">(akun login tidak bisa dihapus)</span>
                <% } else { %>
                  <button class="btn" type="submit">Hapus</button>
                <% } %>
              </form>
            </td>
          </tr>
        <%   }
           } %>
      </tbody>
    </table>
  </div>

</body>
</html>
