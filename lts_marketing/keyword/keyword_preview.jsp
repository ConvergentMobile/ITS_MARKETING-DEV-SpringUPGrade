<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script language="JavaScript">

</script>

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<link href="styles/master.css" rel="stylesheet" type="text/css" media="all" />
		<link href="styles/common.css" rel="stylesheet" type="text/css" media="all" />
		
        <script src="http://ajax.googleapis.com/ajax/libs/swfobject/2.1/swfobject.js"></script>
	</head>

<div id="header">
  <div id="header_top"> <a id="logo" href="index.php" title="Convergent Mobile"><img src="../images/logo.jpg" alt="Convergent Mobile" /></a></div>
<div id="header_main" class="clearfix"/><span id="topinfor">Mobile... the future is now.</span>

</div>
	
		<div id="main">
			<div class="wrapper">
				<div id="content">
     
   <form:form id="thisForm" method="post" action="" commandName="ltUser">

        <h1>Preview</h1>
      		
			<div id="flashcontent" >
</div>
<script type="text/javascript">
        var flashvars = { Keyword: '${ltUser.searchKeywordString}' };
        swfobject.embedSWF("./keyword/logo_USP.swf?id="+Math.random(), "flashcontent", "200", "420", "10.0.0",null, flashvars);
</script>

						
        <div align="right">
			<html:image src="images/btn_submit_3.jpg" onclick="return gotoPage(this.form, 'submit');" title="Submit" alt="Submit" />
        </div>
        <div class="heightfixer">&nbsp;</div>
        
      </form:form>									
	
				</div>		
								
			<div class="heightfixer">&nbsp;</div>
		</div>
	</div>	
