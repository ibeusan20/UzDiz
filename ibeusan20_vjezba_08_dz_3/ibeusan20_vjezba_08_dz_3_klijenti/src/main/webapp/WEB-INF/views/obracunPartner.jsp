<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page
	import="java.util.List, java.util.Date, edu.unizg.foi.nwtis.podaci.Obracun"%>
<html>
<head>
<title>Pregled obračuna po partneru</title>
</head>
<body>
	<h2>Obračuni po partneru u razdoblju</h2>

	<form method="get" action="">
		<label for="od">Datum od:</label> <input type="datetime-local"
			name="od" required /> <label for="do">Datum do:</label> <input
			type="datetime-local" name="do" required /> <label for="id">Partner
			ID:</label> <input type="number" name="id" required />

		<button type="submit">Dohvati obračune</button>
	</form>

	<hr />

	<%
	List<Obracun> lista = (List<Obracun>) request.getAttribute("obracuni");
	if (lista != null && lista instanceof java.util.List) {
	  List<Obracun> obracuni = (List<Obracun>) lista;
	%>
	<table border="1">
		<tr>
			<th>ID</th>
			<th>Vrijeme</th>
			<th>Vrsta</th>
			<th>Partner ID</th>
			<th>Količina</th>
			<th>Cijena</th>
			<th>Ukupno</th>
		</tr>
		<%
		for (Obracun o : obracuni) {
		%>
		<tr>
			<td><%=o.id()%></td>
			<td><%=new Date(o.vrijeme())%></td>
			<td><%=o.jelo() ? "Jelo" : "Piće"%></td>
			<td><%=o.partner()%></td>
			<td><%=o.kolicina()%></td>
			<td><%=o.cijena()%></td>
		</tr>
		<%
		}
		%>
	</table>
	<%
	} else {
	%>
	<p>Nema dostupnih obračuna za prikaz.</p>
	<%
	}
	%>
</body>
</html>
