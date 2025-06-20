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
  var statusDiv = document.getElementById("statusWS");
  var racuniDiv = document.getElementById("racuniWS");
  var porukaDiv = document.getElementById("porukaWS");
  
  var brojRacuna = "0";
  var statusRada = "NEPOZNATO";

  var ws = new WebSocket("ws://" + window.location.hostname + ":8080/ibeusan20_vjezba_08_dz_3_klijenti/ws/tvrtka");

  ws.onopen = function () {
    console.log("WebSocket veza otvorena.");
  };

  ws.onmessage = function (event) {
	  var podaci = event.data.split(";");
	  if (podaci.length > 1) {
	    var status = podaci[0].trim();
	    brojRacuna = podaci[1].trim();
	    statusRada = status;

	    var poruka = podaci[2].trim();

	    statusDiv.textContent = status;
	    statusDiv.style.color = (status === "RADI") ? "green" : "red";
	    racuniDiv.textContent = brojRacuna;
	    porukaDiv.textContent = poruka || "(nema poruke)";
	  }
	};


  ws.onerror = function (e) {
    console.error("WebSocket greška:", e);
    statusDiv.textContent = "Greška!";
    statusDiv.style.color = "red";
  };

  ws.onclose = function () {
    console.warn("WebSocket zatvoren.");
    statusDiv.textContent = "Veza zatvorena";
    statusDiv.style.color = "red";
  };

  document.getElementById("porukaForma").addEventListener("submit", function (e) {
	  e.preventDefault();
	  var poruka = document.getElementById("porukaInput").value.trim();
	  console.log("Unesena poruka:", poruka);
	  if (!poruka) {
	    alert("Molim unesite poruku.");
	    return;
	  }

	  var wsPoruka = `INTERNA;${brojRacuna};${poruka}`;
	  console.log("Šaljem WebSocket poruku:", wsPoruka);
	  ws.send(wsPoruka);
	  document.getElementById("porukaInput").value = "";
	});

</script>
  
</body>
</html>
