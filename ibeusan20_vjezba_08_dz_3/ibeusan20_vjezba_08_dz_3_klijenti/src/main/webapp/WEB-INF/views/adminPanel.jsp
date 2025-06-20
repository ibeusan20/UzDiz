<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>Administracija</title>
</head>
<body>
	<h1>Administracijski panel</h1>

	<ul>
		<li><a
			href="${pageContext.servletContext.contextPath}/mvc/tvrtka/admin/dodajPartnera">Dodaj
				novog partnera</a></li>
		<!-- TODO ostalo -->
	</ul>

	<h2>Aktivacija spavanja poslužitelja</h2>
	<form
		action="${pageContext.servletContext.contextPath}/mvc/tvrtka/admin/aktivirajSpavanje"
		method="post">
		<label for="vrijeme">Unesi vrijeme spavanja (u sekundama):</label> <input
			type="number" id="vrijeme" name="vrijeme" required>
		<button type="submit">Aktiviraj spavanje</button>
	</form>

	<%
	Integer statusSpavanje = (Integer) request.getAttribute("statusSpavanje");
	if (statusSpavanje != null) {
	%>
	<p>
		Status aktivacije spavanja:
		<%=statusSpavanje%></p>
	<%
	}
	%>


	<p>
		<a href="${pageContext.servletContext.contextPath}/mvc/tvrtka/pocetak">Natrag</a>
	</p>
</body>
</html>
