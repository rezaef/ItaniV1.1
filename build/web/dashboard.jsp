<%-- 
    Document   : dashboard
    Created on : 24 Des 2025, 12.14.41
    Author     : rezaef
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="models.User"%>
<%@page import="dao.WateringLogDAO"%>
<%@page import="models.WateringLog"%>
<%@page import="dao.NotifikasiUserDAO"%>
<%@page import="models.NotifikasiUser"%>
<%@page import="java.util.List"%>
<%
  User u = (User) session.getAttribute("user");
  boolean isAdmin = (u != null && "Admin".equalsIgnoreCase(u.getRole()));
%>
<%
  String msg = request.getParameter("msg");
  String err = request.getParameter("err");

  List<WateringLog> wlogs = new WateringLogDAO().listLatest(10);
  NotifikasiUserDAO notifDAO = new NotifikasiUserDAO();
  int notifUnread = notifDAO.countUnread();
  List<NotifikasiUser> notifs = notifDAO.listLatest(8);
%>
<!doctype html>
<html>
<head>
  <meta charset="utf-8"/>
  <title>ITani - Dashboard</title>
  <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.1/dist/chart.umd.min.js"></script>
  <% if (msg != null) { %>
  <div style="background:#ecfdf5;border:1px solid #34d399;padding:10px;border-radius:10px;margin:10px 0;">
    <%=msg%>
  </div>
    <% } %>
    <% if (err != null) { %>
      <div style="background:#fef2f2;border:1px solid #f87171;padding:10px;border-radius:10px;margin:10px 0;">
        <%=err%>
      </div>
    <% } %>

  <style>
    body { font-family: Arial; padding: 24px; }
    .top { display:flex; justify-content:space-between; align-items:center; }
    .grid { display:grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap:12px; margin-top:14px; }
    .card { border:1px solid #ddd; border-radius:12px; padding:14px; }
    .val { font-size:22px; font-weight:bold; margin-top:8px; }
    .btn { padding:8px 12px; border:1px solid #ddd; border-radius:10px; text-decoration:none; background:#fff; cursor:pointer; }
    .btn.primary { border-color:#6b21a8; color:#6b21a8; }
    .btn:disabled { opacity:.5; cursor:not-allowed; }
    .badge { display:inline-block; padding:4px 8px; border-radius:999px; font-size:12px; border:1px solid #ddd; background:#fff; }
    .notif-item { border:1px solid #eee; border-radius:10px; padding:10px; margin-bottom:8px; }
    .lvl { font-size:11px; padding:2px 8px; border-radius:999px; border:1px solid #ddd; }
    .lvl.warn { border-color:#f59e0b; color:#b45309; background:#fffbeb; }
    .lvl.danger { border-color:#ef4444; color:#b91c1c; background:#fef2f2; }

    .chart-grid{ display:grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap:12px; margin-top:12px; }
    @media (min-width: 1100px){
      .chart-grid{ grid-template-columns: repeat(3, minmax(0, 1fr)); }
    }
    .chart-card{ padding:14px; }
    .chart-title{ font-weight:bold; margin-bottom:8px; }
    .chart-wrap{ position:relative; height:180px; }
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
<!--  <div style="margin:10px 0 18px; display:flex; gap:10px; flex-wrap:wrap;">
    <a class="btn" href="<%=request.getContextPath()%>/seed_stock.jsp">Seed Stock</a>-->
  <div style="margin-top:12px; display:flex; gap:10px; flex-wrap:wrap;">
    <a class="btn" href="<%=request.getContextPath()%>/seed_stock.jsp">Seed Stock</a>
    <a class="btn" href="<%=request.getContextPath()%>/fertilizer_stock.jsp">Fertilizer Stock</a>
    <a class="btn" href="<%=request.getContextPath()%>/periods.jsp">Periode & Panen</a>
    <a class="btn" href="<%=request.getContextPath()%>/rekap.jsp">Rekap & Laporan</a>
    <% if (isAdmin) { %>
      <a class="btn" href="<%=request.getContextPath()%>/users.jsp">Kelola User</a>
    <% } %>
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

  
  <h3 style="margin-top:18px;">Grafik Sensor (30 data terakhir)</h3>
  <div class="card chart-card" style="margin-top:12px;">
    <div class="chart-title">Semua Sensor (poin)</div>
    <div class="chart-wrap" style="height:320px;"><canvas id="chart_all"></canvas></div>
    <div style="margin-top:8px; font-size:12px; color:#666;">
      Tip: klik label di legend untuk sembunyikan/tampilkan dataset sensor.
    </div>
  </div>

<div style="display:flex; gap:16px; flex-wrap:wrap; margin-top:16px;">

  <!-- KIRI: Kontrol Pompa (ON/OFF saja) -->
  <div class="card" style="flex:1; min-width:320px;">
    <h3>Kontrol Pompa</h3>

    <div style="margin:8px 0;">
        Status: <b id="pumpStatus">-</b>
        <span id="pumpTime" style="margin-left:8px; font-size:12px; color:#666;"></span>
        <button type="button" class="btn" onclick="refreshPump()">Refresh</button>
    </div>

    <div style="margin:10px 0; display:flex; gap:10px; align-items:center; flex-wrap:wrap;">
      Mode: <b id="autoModeLabel">MANUAL</b>
      <button type="button" class="btn" id="btnAutoOn" onclick="setAutoMode(true)">Otomatis ON</button>
      <button type="button" class="btn" id="btnAutoOff" onclick="setAutoMode(false)">Otomatis OFF</button>
    </div>
    <div id="autoModeHint" style="margin-top:-6px; margin-bottom:8px; font-size:12px; color:#666;"></div>

    <div style="display:flex; gap:10px; flex-wrap:wrap;">
      <form method="POST" action="<%=request.getContextPath()%>/api/watering/log" onsubmit="return guardManual();">
        <input type="hidden" name="redirect" value="1"/>
        <input type="hidden" name="next" value="/dashboard.jsp"/>
        <input type="hidden" name="mode" value="MANUAL"/>
        <input type="hidden" name="source" value="WEB"/>
        <input type="hidden" name="action" value="ON"/>
        <input type="hidden" name="note" value="manual via dashboard"/>
        <button id="btnPumpOn" class="btn primary" type="submit">ON</button>
      </form>

      <form method="POST" action="<%=request.getContextPath()%>/api/watering/log" onsubmit="return guardManual();">
        <input type="hidden" name="redirect" value="1"/>
        <input type="hidden" name="next" value="/dashboard.jsp"/>
        <input type="hidden" name="mode" value="MANUAL"/>
        <input type="hidden" name="source" value="WEB"/>
        <input type="hidden" name="action" value="OFF"/>
        <input type="hidden" name="note" value="manual via dashboard"/>
        <button id="btnPumpOff" class="btn primary" type="submit">OFF</button>
      </form>
    </div>

    <div style="margin-top:10px; font-size:12px; color:#666;">
      Tombol ON/OFF akan: (1) simpan log ke DB, (2) publish MQTT ke <b>okra/pump/cmd</b>.
    </div>
  </div>

  <!-- TENGAH: Notifikasi -->
  <div class="card" style="flex:1; min-width:320px;">
    <div style="display:flex; justify-content:space-between; align-items:center; gap:12px;">
      <h3 style="margin:0;">Notifikasi</h3>
      <span id="notifBadge" class="badge"><%=notifUnread%> belum dibaca</span>
    </div>

    <div style="margin:10px 0; display:flex; gap:8px; flex-wrap:wrap;">
      <button type="button" class="btn" onclick="loadNotifs()">Refresh</button>
      <button type="button" class="btn" onclick="markAllNotifsRead()">Tandai semua dibaca</button>
    </div>

    <div id="notifList" style="max-height:300px; overflow:auto;">
      <%
        if (notifs == null || notifs.isEmpty()) {
      %>
        <div style="padding:10px; color:#666;">Belum ada notifikasi.</div>
      <%
        } else {
          for (NotifikasiUser n : notifs) {
            String waktu = "-";
            if (n.getCreatedAt() != null) {
              String s = n.getCreatedAt().toString();
              waktu = (s.length() >= 19) ? s.substring(0,19) : s;
            }
            String lvl = (n.getLevel() == null) ? "" : n.getLevel().toUpperCase();
            String lvlCls = "WARNING".equals(lvl) ? "warn" : ("DANGER".equals(lvl) ? "danger" : "");
            boolean unread = n.getStatus() != null && n.getStatus().equalsIgnoreCase("UNREAD");
      %>
        <div class="notif-item">
          <div style="display:flex; justify-content:space-between; align-items:center; gap:10px;">
            <div style="display:flex; gap:8px; align-items:center; flex-wrap:wrap;">
              <span class="lvl <%=lvlCls%>"><%=lvl%></span>
              <span style="font-size:12px; color:#666;">Sensor: <b><%=n.getSensorKey()%></b> • <%=waktu%></span>
            </div>
            <% if (unread) { %>
              <button type="button" class="btn" style="padding:6px 10px;" onclick="markNotifRead(<%=n.getId()%>)">Dibaca</button>
            <% } %>
          </div>
          <div style="margin-top:6px;"><%=n.getPesan()%></div>
          <div style="margin-top:6px; font-size:12px; color:#666;">Source: <b><%=n.getSource()%></b> • Status: <b><%=n.getStatus()%></b></div>
        </div>
      <%
          }
        }
      %>
    </div>

    <div style="margin-top:10px; font-size:12px; color:#666;">
      Notifikasi dibuat otomatis jika nilai sensor memasuki ambang batas (<b>WARNING</b>) atau melewati batas (<b>DANGER</b>).
    </div>
  </div>

  <!-- KANAN: Watering Logs (10 terakhir) -->
  <div class="card" style="flex:2; min-width:420px;">
    <h3>Watering Logs (10 terakhir)</h3>

    <table style="width:100%; border-collapse:collapse;">
      <thead>
        <tr>
          <th style="text-align:left; padding:8px; border-bottom:1px solid #eee;">ID</th>
          <th style="text-align:left; padding:8px; border-bottom:1px solid #eee;">Mode</th>
          <th style="text-align:left; padding:8px; border-bottom:1px solid #eee;">Action</th>
          <th style="text-align:left; padding:8px; border-bottom:1px solid #eee;">Source</th>
          <th style="text-align:left; padding:8px; border-bottom:1px solid #eee;">Note</th>
          <th style="text-align:left; padding:8px; border-bottom:1px solid #eee;">Tanggal</th>
        </tr>
      </thead>
      <tbody>
        <%
          if (wlogs == null || wlogs.isEmpty()) {
        %>
          <tr><td colspan="6" style="padding:10px;">Belum ada data.</td></tr>
        <%
          } else {
            for (WateringLog w : wlogs) {
              String waktu = "-";
                if (w.getCreatedAt() != null) {
                  String s = w.getCreatedAt().toString(); // contoh: 2026-01-02 12:34:56.0
                  waktu = (s.length() >= 19) ? s.substring(0,19) : s; // yyyy-MM-dd HH:mm:ss
               }

        %>
          <tr>
            <td style="padding:8px; border-bottom:1px solid #f3f3f3;"><%=w.getId()%></td>
            <td style="padding:8px; border-bottom:1px solid #f3f3f3;"><%=w.getMode()%></td>
            <td style="padding:8px; border-bottom:1px solid #f3f3f3;"><b><%=w.getAction()%></b></td>
            <td style="padding:8px; border-bottom:1px solid #f3f3f3;"><%=w.getSource()%></td>
            <td style="padding:8px; border-bottom:1px solid #f3f3f3;"><%=w.getNote()==null?"":w.getNote()%></td>
            <td style="padding:8px; border-bottom:1px solid #f3f3f3;"><%=waktu%></td>
          </tr>
        <%
            }
          }
        %>
      </tbody>
    </table>

    <div style="margin-top:10px;">
      <a class="btn" href="<%=request.getContextPath()%>/watering_logs.jsp">Lihat semua</a>
    </div>
  </div>

</div>

<script>
  // Jangan pakai const (biar gak duplicate error kalau kebaca ulang)
  var CTX = "<%=request.getContextPath()%>";

  // ===== SENSOR =====
  async function loadLatest(){
    try{
      const res = await fetch(CTX + "/api/sensors/latest", { cache:"no-store" });
      const json = await res.json().catch(()=>null);
      if(!json || !json.ok) return;

      const d = json.data;
      if(!d){
        const tEl = document.getElementById('t');
        if (tEl) tEl.textContent = 'Belum ada data sensor';
        return;
      }

      const set = (id, val) => {
        const el = document.getElementById(id);
        if (el) el.textContent = (val ?? '-');
      };

      set('ph', d.ph);
      set('sm', d.soil_moisture);
      set('st', d.soil_temp);
      set('ec', d.ec);
      set('n', d.n);
      set('p', d.p);
      set('k', d.k);
      set('t', d.reading_time);
    }catch(e){}
  }
  loadLatest();
  setInterval(loadLatest, 3000);

    // ===== SENSOR CHART (ALL IN ONE) =====
  var __allChart = null;

  function fmtLabel(ts){
    if (!ts) return '';
    const s = String(ts);
    // "YYYY-MM-DD HH:mm:ss" => ambil HH:mm:ss
    if (s.length >= 19) return s.substring(11, 19);
    return s;
  }

  function ensureAllChart(){
    const canvas = document.getElementById('chart_all');
    if (!canvas || typeof Chart === 'undefined') return null;
    if (__allChart) return __allChart;

    const ctx = canvas.getContext('2d');

    // Palet sederhana (biar tiap sensor beda)
    const C = [
      '#6b21a8', // pH
      '#0ea5e9', // moisture
      '#f97316', // temp
      '#22c55e', // EC
      '#ef4444', // N
      '#eab308', // P
      '#14b8a6'  // K
    ];

    __allChart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: [],
        datasets: [
          // garis + poin (lebih enak dibaca). legend bisa di-klik untuk hide/show.
          ...(function(){
            const mkDS = (label, color) => ({
              label,
              data: [],
              borderColor: color,
              backgroundColor: color,
              borderWidth: 2,
              tension: 0.35,                // smoothing
              cubicInterpolationMode: 'monotone',
              pointRadius: 2.5,
              pointHoverRadius: 5,
              pointHitRadius: 10,
              fill: false,
              spanGaps: true
            });
            return [
              mkDS('pH', C[0]),
              mkDS('Moisture', C[1]),
              mkDS('Temp', C[2]),
              mkDS('EC', C[3]),
              mkDS('N', C[4]),
              mkDS('P', C[5]),
              mkDS('K', C[6])
            ];
          })()
        ]
      },
      options: {
        responsive: true,
        elements: { line: { borderJoinStyle: 'round', borderCapStyle: 'round' } },
        maintainAspectRatio: false,
        animation: true,
        plugins: {
          legend: { display: true, position: 'bottom', labels: { usePointStyle: true, boxWidth: 10 } },
          tooltip: { mode: 'index', intersect: false }
        },
        interaction: { mode: 'index', intersect: false },
        scales: {
          x: { ticks: { maxTicksLimit: 10 } },
          y: { beginAtZero: false }
        }
      }
    });

    return __allChart;
  }

  function updateAllChart(labels, rows){
    const ch = ensureAllChart();
    if (!ch) return;

    ch.data.labels = labels;

    // urutan dataset harus sama dengan di ensureAllChart()
    ch.data.datasets[0].data = rows.map(r => r.ph);
    ch.data.datasets[1].data = rows.map(r => r.soil_moisture);
    ch.data.datasets[2].data = rows.map(r => r.soil_temp);
    ch.data.datasets[3].data = rows.map(r => r.ec);
    ch.data.datasets[4].data = rows.map(r => r.n);
    ch.data.datasets[5].data = rows.map(r => r.p);
    ch.data.datasets[6].data = rows.map(r => r.k);

    ch.update('none');
  }

  async function loadHistory(){
    try{
      const res = await fetch(CTX + "/api/sensors/history?limit=30", { cache:"no-store" });
      const json = await res.json().catch(()=>null);
      if(!json || !json.ok) return;

      const rows = json.data || [];
      if(!rows.length) return;

      const labels = rows.map(r => fmtLabel(r.reading_time));
      updateAllChart(labels, rows);
    }catch(e){}
  }

  // first load + refresh berkala
  loadHistory();
  setInterval(loadHistory, 5000);

// ===== PUMP STATUS =====
  async function refreshPump(){
    const elS = document.getElementById("pumpStatus");
    const elT = document.getElementById("pumpTime"); // kalau gak ada, aman

    try{
      const r = await fetch(CTX + "/pump/status", { cache:"no-store" });
      const ct = (r.headers.get("content-type") || "").toLowerCase();
      if(!r.ok || !ct.includes("application/json")) throw new Error("not json");

      const j = await r.json();
//      const elS = document.getElementById("pumpStatus");
        let st = (j.status || "-");

        if (st === "PENDING_ON") st = "ON (pending)";
        if (st === "PENDING_OFF") st = "OFF (pending)";

        if (elS) elS.innerText = st;


      const ms = Number(j.updatedAtMs || 0);
      if (elT){
        if (ms > 0){
          const d = new Date(ms);
          elT.innerText = d.toLocaleTimeString("id-ID", { hour:"2-digit", minute:"2-digit", second:"2-digit", hour12:false });
        } else {
          elT.innerText = "";
        }
      }
    }catch(e){
      if (elS) elS.innerText = "-";
      if (elT) elT.innerText = "";
    }
  }
  refreshPump();
  setInterval(refreshPump, 2000);

  // ===== AUTO MODE (UI + HIT BACKEND) =====
  const AUTO_KEY = "itani_autoMode"; // 1=auto, 0=manual

  function getAutoMode(){
    return localStorage.getItem(AUTO_KEY) === "1";
  }

  function applyAutoModeUI(on){
    window.__autoModeEnabled = !!on;

    const label = document.getElementById("autoModeLabel");
    const hint  = document.getElementById("autoModeHint");
    const btnOn = document.getElementById("btnPumpOn");
    const btnOff= document.getElementById("btnPumpOff");

    if (label) label.innerText = on ? "OTOMATIS" : "MANUAL";

    [btnOn, btnOff].forEach(b=>{
      if(!b) return;
      b.disabled = on;
    });

    if (hint) hint.innerText = on
      ? "Mode otomatis aktif → kontrol manual (ON/OFF) dinonaktifkan."
      : "";
  }

  // dipakai form ON/OFF
  function guardManual(){
    if (window.__autoModeEnabled) {
      alert("Mode otomatis sedang aktif. Matikan dulu untuk kontrol manual.");
      return false;
    }
    return true;
  }
  window.guardManual = guardManual;

  // dipakai tombol Otomatis ON/OFF
  async function setAutoMode(on){
    localStorage.setItem(AUTO_KEY, on ? "1" : "0");
    applyAutoModeUI(on);

    const hint = document.getElementById("autoModeHint");
    try{
      if (hint) hint.innerText = "Mengirim mode...";

      const url = CTX + (on ? "/pump/mode/auto/on" : "/pump/mode/auto/off");
      const r = await fetch(url, { method:"POST", cache:"no-store" });
      const j = await r.json().catch(()=>null);

      if(!r.ok || !j || j.ok !== true){
        if (hint) hint.innerText = "Gagal publish mode (cek server/MQTT).";
        console.log("AUTO MODE FAIL:", r.status, j);
        return;
      }
      if (hint) hint.innerText = "Mode terkirim ✅";
      console.log("AUTO MODE OK:", j);
    }catch(e){
      if (hint) hint.innerText = "Gagal kirim mode ke server.";
      console.log("AUTO MODE EX:", e);
    }
  }
  window.setAutoMode = setAutoMode;

  // init UI dari localStorage
  applyAutoModeUI(getAutoMode());

  // ===== NOTIFICATIONS =====
  function escHtml(s){
    if (s == null) return '';
    return String(s)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;');
  }

  function fmtTime(ts){
    if (!ts) return '-';
    // ts umumnya: "2026-01-03 18:05:00.0"
    const s = String(ts);
    return (s.length >= 19) ? s.substring(0,19) : s;
  }

  function renderNotifs(unread, data){
    const badge = document.getElementById('notifBadge');
    if (badge) badge.textContent = (unread || 0) + ' belum dibaca';

    const box = document.getElementById('notifList');
    if (!box) return;

    if (!data || !data.length){
      box.innerHTML = '<div style="padding:10px; color:#666;">Belum ada notifikasi.</div>';
      return;
    }

    let html = '';
    for (const n of data){
      const lvl = (n.level || '').toUpperCase();
      const lvlCls = (lvl === 'DANGER') ? 'danger' : 'warn';
      const unread = (String(n.status || '').toUpperCase() === 'UNREAD');

      html += '<div class="notif-item">'
        + '<div style="display:flex; justify-content:space-between; align-items:center; gap:10px;">'
        +   '<div style="display:flex; gap:8px; align-items:center; flex-wrap:wrap;">'
        +     '<span class="lvl ' + lvlCls + '">' + escHtml(lvl) + '</span>'
        +     '<span style="font-size:12px; color:#666;">Sensor: <b>' + escHtml(n.sensorKey) + '</b> • ' + escHtml(fmtTime(n.createdAt)) + '</span>'
        +   '</div>'
        +   (unread ? '<button type="button" class="btn" style="padding:6px 10px;" onclick="markNotifRead(' + Number(n.id) + ')">Dibaca</button>' : '')
        + '</div>'
        + '<div style="margin-top:6px;">' + escHtml(n.message) + '</div>'
        + '<div style="margin-top:6px; font-size:12px; color:#666;">Source: <b>' + escHtml(n.source) + '</b> • Status: <b>' + escHtml(n.status) + '</b></div>'
        + '</div>';
    }

    box.innerHTML = html;
  }

  async function loadNotifs(){
    try{
      const r = await fetch(CTX + '/api/notifications/latest?limit=8', { cache:'no-store' });
      const j = await r.json().catch(()=>null);
      if (!r.ok || !j || j.ok !== true) return;
      renderNotifs(j.unread, j.data);
    }catch(e){}
  }
  window.loadNotifs = loadNotifs;

  async function markAllNotifsRead(){
    try{
      const r = await fetch(CTX + '/api/notifications/markAllRead', { method:'POST' });
      const j = await r.json().catch(()=>null);
      if (!r.ok || !j || j.ok !== true) return;
      loadNotifs();
    }catch(e){}
  }
  window.markAllNotifsRead = markAllNotifsRead;

  async function markNotifRead(id){
    try{
      const r = await fetch(CTX + '/api/notifications/markRead?id=' + encodeURIComponent(id), { method:'POST' });
      const j = await r.json().catch(()=>null);
      if (!r.ok || !j || j.ok !== true) return;
      loadNotifs();
    }catch(e){}
  }
  window.markNotifRead = markNotifRead;

  // auto refresh notifikasi
  loadNotifs();
  setInterval(loadNotifs, 5000);
</script>

</body>
</html>
