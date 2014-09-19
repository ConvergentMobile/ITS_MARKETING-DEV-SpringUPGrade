<?php
session_start();
if($_REQUEST['mode'] == "FT") {
    $_SESSION['imageFile'] = "";
    $_SESSION['keyword'] = $_COOKIE['keyword'];
    $_SESSION['acnum'] = $_REQUEST['acnum'];
}
?>
 <html>
 <head>
 <meta charset="utf-8">
 <meta name="viewport" content="width=device-width,initial-scale=1.0">
 <title></title>


</head>

<body>

<!-- page wrapper -->
<div>




<script language="JavaScript">
	function gotoPage() {
		window.close();
	}
</script>

        <script src="http://ajax.googleapis.com/ajax/libs/swfobject/2.1/swfobject.js"></script>

	<div align="center">

        &nbsp;&nbsp;&nbsp;<h3>Preview Decal</h3>
        <hr/>
<?php
        if ($_SESSION['imageFile'] == "") {
?>

<font size="2"><div align="center">
*** NOTE *** Please be patient after clicking "Get my Hotspot!" as it may take 20-25 seconds
<br/>for the high-resolution artwork to be created. If there is an issue with the hotspot creation, please contact support@us411.co
</div></font>
<br/><br/>

<?php
        }
?>
			<div  align="left" id="flashcontent" ></div>

                        <script type="text/javascript">
                                var flashvars = { Keyword: '<?= urlencode($_COOKIE['keyword']) ?>' };

                                var params = {};
                                params.wmode = "transparent";
                                var attributes = {};

                                swfobject.embedSWF("./logo_USP.swf?id="+Math.random(), "flashcontent", "200", "420", "10.0.0",null, flashvars, params, attributes);
                        </script>

			<form name="form1" id="form1" method="get">
                <input type="hidden" name="imageFile" value="../../outfiles/hotspot/liberty_tax/<?=$_SESSION['imageFile']?>">
			</form>

	</div>


	<div align="center">

<?php
        if ($_SESSION['imageFile'] != "") {
?>

        <font size="+1">Your decal image file has been generated.
        You can view it <a href='./view_hotspot.jsp?hotspot=../../outfiles/hotspot/liberty_tax/<?= $_SESSION['acnum'] . "_" . $_SESSION['keyword'] . "_" . $_SESSION['imageFile']?>'>here</a>
        </font>
		<br/>
		<input type="image" name="" src="../images/finish.png" onclick="gotoPage();" title="Finished" alt="Finished">


<?php
        }
?>

	</div>


    </div>

</div>

</body>
</html>
