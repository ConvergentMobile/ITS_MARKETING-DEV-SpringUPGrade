<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<script type="text/JavaScript">	
	function uploadFile() {	
		form = document.getElementById('thisForm');
		var lname = document.getElementById('searchDMAString').value;
		var offEntId = document.getElementById('searchOfficeIdString').value;

		if (offEntId == null || offEntId == "") {
			alert("Must select an office id");
			return false;
		}
		
		if (lname == null || lname == "") {
			alert("Must enter List Name");
			return false;
		}
		
		form.action = '/lts_marketing/uploadFile';
		form.submit();	
	}

</script>

<style>
.err_msg {font-size:large; color:red; font-weight:bold};
</style>

<form:form id="thisForm" method="post" action="" commandName="ltUser" enctype="multipart/form-data">
	<h3>Upload Your List</h3>
	<hr/>
 
 <div  class="err_msg" align="center">
 		<c:if test = "${error != null}">
 			*** <c:out value="${error}"/> ***
 		</c:if>
</div>

<br/>
	<table border="0" align="center">
		<tr>
			<td>
				<strong><label for="selectedOffice"><spring:message code="label.officeId" /></label></strong>
			</td>		
			<td>
                <form:select path="searchOfficeIdString" class="select_send_notification">
                	<form:option value="">Select an Office</form:option>
                	<form:options items="${sites}" itemValue="userId" itemLabel="customField2"/>            	
            	</form:select>   
			</td>
		</tr>	
		<tr>
			<td>
				<strong><label for="listFileName"><spring:message code="label.listFileName" /></label></strong>
			</td>		
			<td><form:input path="searchDMAString" size="35" /></td>
		</tr>
		<tr>
			<td>
				<strong><label for="listFile"><spring:message code="label.listFile" /></label></strong>
			</td>
			<td><input type="file" name="file" id="file"></td>
		</tr>
		<tr>
			<td colspan="2">
			<a href="#" onclick="uploadFile()" class="lnk_scheduled">Upload</a>
			</td>
		</tr>	
	</table>
</form:form>	
				

