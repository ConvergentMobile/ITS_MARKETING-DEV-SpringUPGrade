<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
 
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.2/jquery-ui.min.js"></script>
<title>Generate Url</title>
</head>
<body>

<script LANGUAGE="JavaScript">
	function genIt() {
		$.ajax({
		    type : 'POST',
		    url : 'generateUrl',
		    data: 'role=' + $('#role').val() + '&id=' + $('#id').val() + '&password=' + $('#password').val(),
		    success : function(result) {
				alert(result)
				},
				error : function(e) {
					alert('error: ' + e.text());
				}                        
		});
	}	
</script>

   <form:form id="thisForm" method="post" action="dashboard" commandName="ltUser">

	Role: <select name="role" id="role"> 
  			<option value="Office" >Office</option>
  			<option value="Entity" >Entity</option>
  			<option value="Corporate" >Corporate</option>
		</select>
	<p/>
	Id: <input type="text" name="id" id="id"/>
	<p/>
	Password: <input type="text" name="password" id="password"/>
	<p/>	
	<button type="button" onclick="genIt()">Do It!</button> 
	
	</form:form>
	
</body>
</html>