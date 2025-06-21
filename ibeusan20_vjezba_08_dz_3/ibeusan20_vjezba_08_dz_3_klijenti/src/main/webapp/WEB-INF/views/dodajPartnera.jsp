<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Vježba 8 - zadaća 3 - Dodavanje partnera</title>
    <style>
        label { display: inline-block; width: 150px; margin-top: 5px; }
        input, select { margin-top: 5px; }
    </style>
</head>
<body>
    <h1>Vježba 8 - zadaća 3 - Dodavanje novog partnera</h1>

    <form method="post" action="${pageContext.servletContext.contextPath}/mvc/tvrtka/admin/dodajPartnera">
        <label for="id">ID partnera:</label>
        <input type="number" name="id" required /><br/>
        <label for="naziv">Naziv:</label>
        <input type="text" name="naziv" required /><br/>
        <label for="vrstakuhinje">Vrsta kuhinje:</label>
        <input type="text" name="vrstakuhinje" required /><br/>
        <label for="adresa">Adresa:</label>
        <input type="text" name="adresa" required /><br/>
        <label for="mreznaVrata">Mrežna vrata:</label>
        <input type="number" name="mreznaVrata" required /><br/>
        <label for="mreznaVrataKraj">Mrežna vrata za kraj:</label>
        <input type="number" name="mreznaVrataKraj" required /><br/>
        <label for="gpssirina">GPS širina:</label>
        <input type="number" step="any" name="gpssirina" required /><br/>
        <label for="gpsduzina">GPS dužina:</label>
        <input type="number" step="any" name="gpsduzina" required /><br/>
        <label for="sigurnosnikod">Sigurnosni kod:</label>
        <input type="text" name="sigurnosnikod" required /><br/>
        <label for="adminkod">Admin kod:</label>
        <input type="text" name="adminkod" required /><br/><br/>
        <input type="submit" value="Dodaj partnera" />
    </form>
    <br/>
    <%
        Integer status = (Integer) request.getAttribute("statusDodavanja");
        if (status != null) {
    %>
        <p>Status dodavanja partnera: <strong><%= status %></strong></p>
    <%
        }
    %>
    <br/>
    <a href="${pageContext.servletContext.contextPath}/mvc/tvrtka/admin/panel">← Natrag na admin panel</a>
</body>
</html>
