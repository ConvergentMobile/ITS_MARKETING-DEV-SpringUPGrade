<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/JavaScript">	
	function deleteJob(trigName, trigGroup){
		
		var ok = confirm("Are you sure you want to delete this job?");
		if (ok) {
			form.action = '/us411/SendMessage.do?dispatch=deleteJob&trigName='+trigName+'&trigGroup='+trigGroup;
			form.submit();
		} 
	}
</script>
	
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

<h4>Scheduled Messages</h4>

   <form:form id="thisForm" method="post" action="dashboard" commandName="ltUser">

 <table border="1"> 
   		<th>Campaign Name</th>
   		<th>Message</th>
   		<th>Delivery Date</th>    	
	<c:forEach var="reportDataRow" items="${ltUser.reportRows}" varStatus="loopStatus"> 
	  <tr>
	  	<td><c:out value="${reportDataRow.column1}" /></td>
	  	<td><c:out value="${reportDataRow.column2}" /></td>
	  	<td><c:out value="${reportDataRow.column3}" /></td> 
	  	<td>
			<a href="javascript:deleteJob('${reportDataRow.column4}', '${reportDataRow.column5}');"><img src="images/delete.png" title="Delete"/></a>	
	  	</td>
	  </tr>
	</c:forEach>
</table>

   </form:form>
</body>
</html>