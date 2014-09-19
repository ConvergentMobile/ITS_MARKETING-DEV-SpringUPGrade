<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

<script language="JavaScript">
</script>

<div did="main">
			<div class="wrapper">
				<div id="content">
				
<div align="center">
	<br/><br/><br/><br/>
	There was an error:
	<br/><br/>
	<font size="3">
		${errorMsg}
		<br/>
		<c:if test = "${error != null}">
			*** <c:out value="${error}"/> ***
		</c:if>
		<br/>
	</font>
	<br/><br/><br/><br/><br/>
	
</div>

</div>
</div>
</div>

</div>