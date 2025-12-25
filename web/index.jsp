<%-- 
    Document   : index
    Created on : 24 Des 2025, 12.09.06
    Author     : rezaef
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
  String err = request.getParameter("err");
%>
<!doctype html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>ITani - Login</title>
  <style>
    body { font-family: Arial; padding: 24px; max-width: 420px; margin: auto; }
    .card { border:1px solid #ddd; border-radius:12px; padding:16px; }
    input { width:100%; padding:10px; margin:8px 0; }
    button { width:100%; padding:10px; cursor:pointer; }
    .msg { margin-top: 10px; color:#b00; }
  </style>
</head>
<body>
  <h2>ITani (Java)</h2>

  <div class="card">
    <form method="POST" action="<%=request.getContextPath()%>/api/auth/login">
      <label>Username</label>
      <input name="username" placeholder="admin" required />

      <label>Password</label>
      <input name="password" type="password" placeholder="admin123" required />

      <!-- supaya servlet tau ini login via web dan harus redirect -->
      <input type="hidden" name="redirect" value="1"/>

      <button type="submit">Login</button>
    </form>

    <div class="msg">
      <%= (err != null ? "invalid credentials" : "") %>
    </div>
  </div>
</body>
</html>
