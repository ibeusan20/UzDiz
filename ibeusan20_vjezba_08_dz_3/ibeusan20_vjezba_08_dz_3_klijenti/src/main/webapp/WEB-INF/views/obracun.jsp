<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.List, edu.unizg.foi.nwtis.podaci.Obracun"%>
<html>
<head>
<title>Obračuni</title>
</head>
<body>
	<h2>Pregled obračuna</h2>

	<form method="get">
		<label for="od">Od:</label> <input type="datetime-local" name="od"
			required /> <label for="do">Do:</label> <input type="datetime-local"
			name="do" required /> <label for="tip">Tip:</label> <select
			name="tip">
			<option value="sve">Sve</option>
			<option value="jelo">Jelo</option>
			<option value="pice">Piće</option>
		</select> <input type="submit" value="Filtriraj" />
	</form>

	<%
	List<Obracun> obracuni = (List<Obracun>) request.getAttribute("obracuni");
	if (obracuni != null && !obracuni.isEmpty()) {
	%>
	<table border="1">
		<tr>
			<th>Vrijeme</th>
			<th>Partner ID</th>
			<th>Stavka</th>
			<th>Cijena</th>
		</tr>
		<%
		for (Obracun o : obracuni) {
		%>
		<tr>
			<td><%=o.vrijeme()%></td>
			<td><%=o.partner()%></td>
			<td><%=o.id()%></td>
			<td><%=o.cijena()%></td>
		</tr>
		<%
		}
		%>
	</table>
	<%
	} else if (obracuni != null) {
	%>
	<p>Nema rezultata za odabrano razdoblje.</p>
	<%
	}
	%>

	<a href="${pageContext.servletContext.contextPath}/mvc/tvrtka/pocetak">Natrag</a>
</body>
</html>
