<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
 
 <html>
 <head>
 <meta charset="utf-8">
 <meta name="viewport" content="width=device-width,initial-scale=1.0">
 <title></title>
 
 <link rel="stylesheet" href="css/global.css">
 <link rel="stylesheet" href="fonts/fonts.css">
 <link rel="stylesheet" href="css/navigation.css">
 <link rel="stylesheet" href="css/common.css">
 <link rel="stylesheet" href="css/grids.css">
 <link rel="stylesheet" href="css/sidebar.css">
</head>


<script LANGUAGE="JavaScript">
	$(document).ready(function() {
		$( "#errwin" ).dialog({
			title: 'Alerts & Notifications',
			width: 400,
			height: 200,		
			dialogClass: 'no-close',
			autoOpen: false,
			buttons: {
			  OK: function() {
				$(this).dialog("close");
				location.reload();
			  }
		       },
		});		
	});
	
	function changeKeyword() {
		var kw = $('#sendSearchKeywordString').val();
		if (kw.indexOf(" ") > 0) {
			alert("Keyword must be a single word without any spaces");
			return;
		}
		
		if (kw.indexOf("&") > 0) {
			alert("Keyword cannot contain special characters");
			return;
		}	
		
		$.ajax({
		    type : 'POST',
		    url : 'changeKeyword',
			data: $("#thisForm1").serialize(),
		    success : function(result) {
				//alert(result);
				//$('#errwin').html('<div align="center">' + result + '</div>');
				//$( "#errwin" ).dialog('open');		
				popup(result, 1);
				//location.reload();
				$('#dialog1').dialog('close');
		    },
		    error : function(e) {
				//alert('error: ' + e);
				$('#errwin').html('<div align="center">Error:' + error + '</div>');
				$( "#errwin" ).dialog();					
		    }                        
		});
	}
</script>

<body  id="office">	
   <form:form id="thisForm1" method="post" action="" commandName="ltUser">
	
      	<div class="my_popup_02 mpu_02" id="id_popup_01">
	<div class="mpu_wrapper">
  	<!-- title -->
    <div class="mpu_title">
    	<table cellpadding="0" cellspacing="0" border="0" width="100%">
      <tr>
      	<td style="text-align:center"><h3>Change Keyword</h3></td>
      	<td><button title="Close (Esc)" type="button" onclick="closeIt()" class="mfp-close">&times;</button></td>
      </tr>
      </table>
    </div>
    <!-- // title -->
    <!-- table header -->
		<div class="wide_column_wrapper search_container_01">
          	<table class="grid grid_06" width="50%">
            <colgroup>
            </colgroup>
            <tbody>
            	<tr>
              		<td class="td_01"><form:label path="searchKeywordString">Current <spring:message code="label.keyword" /></form:label></strong></td>
                	<td class="td_02"><form:input path="searchKeywordString" readonly="true"/></td>
              </tr>            
            	<tr>
              		<td class="td_01"><form:label path="sendSearchKeywordString">New <spring:message code="label.keyword" /></form:label></strong></td>
                	<td class="td_02"><form:input path="sendSearchKeywordString" maxlength="15"/></td>
              </tr>
            </tbody>
            </table>
          <!-- buttons wrapper -->
          <div class="button_wrapper_05" align="center">
         	<input type="button" value="Submit" class="btn_dark_blue btn_03" onclick="changeKeyword()">
          </div>            
          <!-- // buttons wrapper -->            
          </div>
          <!-- // msg params -->

    <!-- // table header -->
    
  </div>
</div>
    
   </form:form>


<!-- box -->
        




 </body>