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
 


<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
 
</head>

<script LANGUAGE="JavaScript">
function deleteNumber(number) {
	var listId = $('#searchDMAString').val();
        $.ajax({
            type : 'POST',
            url : 'deleteNumber',
        	    data: 'listId=' + listId + '&number=' + number,
            success : function(result) {
        		alert(result);
        		location.reload();        		
			},
			error : function(e) {
				alert('error: ' + e);
			}                        
        });	
}

</script>

<body  id="office">	
<div id="content" class="inner">
   <form:form id="thisForm1" method="post" action="" commandName="ltUser">
	
      	<!-- box -->
        <div class="box box_grey_bg box_grey_title box_message" id="box">
        	<!-- title -->
        	<div class="box_title">
          		<h2>Mobile Numbers</h2>
          	</div>
          <!-- // title -->

			<form:hidden path="searchDMAString"/>
			
          	<table class="grid grid_06">
            <colgroup>
            </colgroup>
            <tbody>
            	<tr>
            	<c:forEach var="val" items="${ltUser.listIds}" varStatus="loopStatus">
              		<td class="td_01"><c:out value="${val}"/>
              		<a href="#" onclick="deleteNumber('${val}')"><img src="images/delete.png" title="Delete"/></a></td>
                          <c:if test="${loopStatus.count % 3 == 0}">
	                  			</tr>
	                  			<tr>
            		</c:if>
              </c:forEach>
            </tbody>
            </table>
                      

        </div>
        <!-- // box -->
    
   </form:form>
   </div>
 </body>