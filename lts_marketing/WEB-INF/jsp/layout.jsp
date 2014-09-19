<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd"><html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

<head>
		<meta charset="utf-8">
		<meta content="width=device-width,initial-scale=1.0" name="viewport">
		<link rel="stylesheet" type="text/css" href="css/common.css">
		<link rel="shortcut icon" href="/favicon.ico">
		<title>Liberty Tax Service</title>
		<link rel="stylesheet" href="css/global.css">
		<link rel="stylesheet" href="fonts/fonts.css">
		<link rel="stylesheet" href="css/navigation.css">
		<link rel="stylesheet" href="css/common.css">
		<link rel="stylesheet" href="css/grids.css">
		<link rel="stylesheet" href="css/sidebar.css">

 		<link rel="stylesheet" href="css/jquery-ui.min.css">
		<link href='./css/cupertino/jquery-ui.css' rel='stylesheet' />	

 		<link rel="stylesheet" type="text/css" href="css/jquery.timepicker.css" />
  
  <!-- 
		<script src="js/jquery-1.9.1.min.js"></script>		
		<script src="js/jquery-ui.js"></script>
  -->	

<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.10.2/jquery-ui.min.js"></script>
		

  <script src="js/jquery.timepicker.js"></script>


		<script src="js/jquery.idTabs.min.js"></script>
		<script src="js/jquery.slimscroll.min.js"></script>
		<script src="js/jquery.screwdefaultbuttonsV2.js"></script>
		<script src="js/jquery.cycle.all.js"></script>
		<script src="js/functions.js"></script>
 
		
		<script language="JavaScript">
		function logout(){
			window.location="logout.do";
		}
		</script>
	</head>
	<body id="office">
		<tiles:insertAttribute  name="header" />
		<tiles:insertAttribute  name="body" />
		<tiles:insertAttribute  name="footer" />
	</body>
</html>