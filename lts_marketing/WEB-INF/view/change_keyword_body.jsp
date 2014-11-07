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
	function changeKeyword() {
        $.ajax({
            type : 'POST',
            url : 'changeKeyword',
        	data: $("#thisForm1").serialize(),
            success : function(result) {
        		alert(result);
     			location.reload();
     			$('#dialog1').dialog('close');
     		},
			error : function(e) {
				alert('error: ' + e);
			}                        
        });
	}
</script>

<body  id="office">	
   <form:form id="thisForm1" method="post" action="" commandName="ltUser">
	
      	<!-- box -->
        <div class="box box_grey_bg box_grey_title box_message" id="box">
        	<!-- title -->
        	<div class="box_title">
          		<h2>Change Keyword</h2>
          	</div>
          <!-- // title -->
          <!-- msg params -->
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
                	<td class="td_02"><form:input path="sendSearchKeywordString" /></td>
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

        </div>
        <!-- // box -->
    
   </form:form>
 </body>