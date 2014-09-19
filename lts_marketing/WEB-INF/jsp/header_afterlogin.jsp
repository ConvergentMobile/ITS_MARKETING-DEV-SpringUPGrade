<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd"><html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="user.User"%>

<script language="JavaScript">
	function set(form, action) {
		form.dispatch.value = action;		
		form.submit();
	}
	
	function setAction(action) {
		var form = document.getElementById("signin");

		form.action = action;		
		form.submit();
	}

	function openwindow(of) {
		window.open(of,"mywindow","menubar=1,resizable=1,width=950,height=750");
	}
	
	$(function() {
		 if (${error != null}) {
			$('#errwin').dialog();
		}
	});
</script>

<style>
.err_msg {font-size:large; color:white; font-weight:bold};
</style>

<!-- page wrapper -->
<div class="wrapper">
	<!-- header -->
  <div class="header">
  	<a href="dashboard-office.html" class="logo_lts"></a>
    <div class="logo_txt"><a href="dashboard-office.html">Liberty Tax Service</a></div>
    <div class="logo_app"><a href="dashboard-office.html"></a></div>
    <ul class="top_nav">
      <li class="help"><a href="#">Help</a></li>

<form:form id="signin" method="post" action="logout" commandName="ltUser"> 		 	
      
      <li class="signout"><a href="javascript:setAction('logout');">Signout</a></li>
     <!-- 
      <li class="welcome"><p>Entity: <c:out  value="${sessionScope.eId}"/></p></li>  
     -->
</form:form>
    </ul>
  </div>
   
<div  class="err_msg" align="center">
		<c:if test = "${error != null}">
			*** <c:out value="${error}"/> ***
		</c:if>
</div>

<div id="errwin">
		<c:if test = "${error != null}">
			*** <c:out value="${error}"/> ***
		</c:if>
</div>