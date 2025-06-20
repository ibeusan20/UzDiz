<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
  <title>Administracija</title>
</head>
<body>
  <h1>Administracijski panel</h1>

  <ul>
    <li>
      <a href="${pageContext.servletContext.contextPath}/mvc/tvrtka/admin/dodajPartnera">
        Dodaj novog partnera
      </a>
    </li>
    <li><a
			href="${pageContext.servletContext.contextPath}/mvc/tvrtka/admin/nadzornaKonzolaTvrtka">Nadzorna
				konzola Tvrtka</a></li>
  </ul>

  <fieldset>
    <legend>Status poslužitelja (WebSocket)</legend>
    <p>Status: <span id="statusWS" style="font-weight:bold;">Učitavanje...</span></p>
    <p>Broj obračuna: <span id="racuniWS">Učitavanje...</span></p>
    <p>Interna poruka: <span id="porukaWS">(nema poruke)</span></p>

    <form id="porukaForma">
      <label for="porukaInput">Unesi internu poruku:</label><br>
      <input type="text" id="porukaInput" name="poruka" placeholder="Poruka..." />
      <button type="submit">Pošalji</button>
    </form>
  </fieldset>

  <fieldset>
    <legend>Aktivacija spavanja poslužitelja</legend>
    <form action="${pageContext.servletContext.contextPath}/mvc/tvrtka/admin/aktivirajSpavanje" method="post">
      <label for="vrijeme">Vrijeme spavanja (u sekundama):</label>
      <input type="number" id="vrijeme" name="vrijeme" required>
      <button type="submit">Aktiviraj spavanje</button>
    </form>

    <%
      Integer statusSpavanje = (Integer) request.getAttribute("statusSpavanje");
      if (statusSpavanje != null) {
    %>
      <p>Status aktivacije spavanja: <%= statusSpavanje %></p>
    <%
      }
    %>
  </fieldset>

  <fieldset>
    <legend>Konzola za upravljanje poslužiteljem Tvrtka</legend>

    <%
  String status1 = String.valueOf(request.getAttribute("statusT1"));
  String status2 = String.valueOf(request.getAttribute("statusT2"));
%>

<p>Dio 1: 
  <strong style="color:<%= "200".equals(status1) ? "green" : "red" %>;">
    <%= "200".equals(status1) ? "AKTIVAN" : "NEAKTIVAN" %>
  </strong>
</p>

<p>Dio 2: 
  <strong style="color:<%= "200".equals(status2) ? "green" : "red" %>;">
    <%= "200".equals(status2) ? "AKTIVAN" : "NEAKTIVAN" %>
  </strong>
</p>


    <form method="get" action="${pageContext.servletContext.contextPath}/mvc/tvrtka/start/1">
      <button type="submit">Start 1</button>
    </form>
    <form method="get" action="${pageContext.servletContext.contextPath}/mvc/tvrtka/pauza/1">
      <button type="submit">Pauza 1</button>
    </form>
    <form method="get" action="${pageContext.servletContext.contextPath}/mvc/tvrtka/start/2">
      <button type="submit">Start 2</button>
    </form>
    <form method="get" action="${pageContext.servletContext.contextPath}/mvc/tvrtka/pauza/2">
      <button type="submit">Pauza 2</button>
    </form>
    <form method="get" action="${pageContext.servletContext.contextPath}/mvc/tvrtka/kraj">
      <button type="submit">Kraj rada poslužitelja</button>
    </form>
  </fieldset>

  <p>
    <a href="${pageContext.servletContext.contextPath}/mvc/tvrtka/pocetak">Natrag</a>
  </p>

  <script>
    const statusDiv = document.getElementById("statusWS");
    const racuniDiv = document.getElementById("racuniWS");
    const porukaDiv = document.getElementById("porukaWS");
    const ws = new WebSocket("ws://" + window.location.hostname + ":8080/nwtis/ws/tvrtka");

    ws.onmessage = function(event) {
      const podaci = event.data.split(";");
      if (podaci.length === 3) {
        const status = podaci[0].trim();
        const racuni = podaci[1].trim();
        const poruka = podaci[2].trim();

        statusDiv.textContent = status;
        statusDiv.style.color = (status === "RADI") ? "green" : "red";
        racuniDiv.textContent = racuni;
        porukaDiv.textContent = poruka || "(nema poruke)";
      }
    };

    document.getElementById("porukaForma").addEventListener("submit", function(e) {
      e.preventDefault();
      const poruka = document.getElementById("porukaInput").value;
      const wsPoruka = "RADI;;" + poruka;
      ws.send(wsPoruka);
      document.getElementById("porukaInput").value = "";
    });
  </script>
</body>
</html>
