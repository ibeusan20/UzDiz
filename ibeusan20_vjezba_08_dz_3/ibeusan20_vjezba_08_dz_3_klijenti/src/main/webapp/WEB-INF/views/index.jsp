<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Naslov početne stranice</title>
    </head>
    <body>
        <h1>Naslov početne stranice</h1>
        <ul>
            <li>
                <a href="${pageContext.servletContext.contextPath}/mvc/pocetak">Početna stranica</a>
            </li>
            <li>
                <a href="${pageContext.servletContext.contextPath}/mvc/kraj">Šalji komandu za kraj</a>
            </li>        
        </ul>          
    </body>
</html>
