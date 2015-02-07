<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Servlet 3.0 File Upload</title></head>
<body>
 
<form action="/upload" method="post" enctype="multipart/form-data">
	<input type="text" name="a" />
    <input type="file" name="file"/>
    <button type="submit">submit</button>
</form>
 
</body>
</html>
