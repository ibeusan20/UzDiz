<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="edu.unizg.foi.nwtis.podaci.Partner" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Detalji partnera</title>
</head>
<body>
	<h1>Detalji partnera</h1>
	<ul>
		<%
		Integer status = (Integer) request.getAttribute("status");
		Partner partner = (Partner) request.getAttribute("partner");
		%>
		<li>Status zahtjeva: <%= status %></li>
		<%
		if (partner != null) {
		%>
		<li>ID partnera: <%= partner.id() %></li>
		<li>Naziv: <%= partner.naziv() %></li>
		<li>Adresa: <%= partner.adresa() %></li>
		<li>Lokacija dužina: <%= partner.gpsDuzina() %></li>
		<li>Lokacija širina: <%= partner.gpsSirina() %></li>
		<li>Vrsta kuhinje: <%= partner.vrstaKuhinje() %></li>
		<%
		}
		%>
	</ul>
	<a href="<%= request.getContextPath() %>/mvc/tvrtka/partner">Povratak na popis</a>
</body>
</html>
