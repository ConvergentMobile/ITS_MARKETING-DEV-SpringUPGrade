<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script language="JavaScript">
	function gotoPage() {
		window.location.href = "/lts_marketing/keyword/keyword_preview.php?keyword=<%= request.getParameter("keyword")%>";
	}
	
</script>

<html>
	
	<image src="<%= request.getParameter("hotspot")%>" width="500" height="500"/>
	<br/><br/>
         <input type="button" value="Back" class="btn_dark_blue btn_03" onclick="gotoPage()">
	
</html>