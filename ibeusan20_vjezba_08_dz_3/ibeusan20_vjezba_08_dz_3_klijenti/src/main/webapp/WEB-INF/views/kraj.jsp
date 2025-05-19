<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Kraj rada poslužitelja tvrtka</title>
    </head>
    <body>
        <h1>Kraj rada poslužitelja tvrtka</h1>
		<%String status = (String) request.getAttribute("status"); %>        
        <p>Status poslužitelja: <%= "status" %></p>        
    </body>
</html>
