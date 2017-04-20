<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
 <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
 
<script LANGUAGE="JavaScript">
	function approveMsg(msgId) {
		$.ajax({
		    type : 'POST',
		    url : 'approveMessage',
		    data: $('#thisForm1').serialize(),
		    success : function(result) {
			$('#office').html(result)
				},
				error : function(e) {
					alert('error: ' + e.text());
				}                        
		});
	}
	
	function rejectMsg(msgId) {
		var comment = $('#aMsg\\.comments').val();
		if (comment == '') {
			popup("Must provide a comment in case of rejection");
			return;
		}
		$('#aMsg\\.status').val('R');
		//alert('cmt: ' + comment);
		$.ajax({
		    type : 'POST',
		    url : 'approveMessage',
		    data: $('#thisForm1').serialize(),
		    success : function(result) {
			$('#office').html(result)
				},
				error : function(e) {
					alert('error: ' + e.text());
				}                        
		});
	}	
</script>

<script language="JavaScript">

</script>
	
   <form:form id="thisForm1" method="post" action="dashboard" commandName="ltUser">
	
      	<!-- box -->
        <div class="box box_grey_bg box_grey_title box_message" id="box">
        	<!-- title -->
        	<div class="box_title">
          	<h2>New Custom Messages For Approval</h2>
          </div>
          <!-- // title -->
          <!-- msg params -->
          <div class="msg_params">
          	<table class="grid grid_05" width="100%">
            <colgroup>
            </colgroup>
            <tbody>
            	<form:hidden path="aMsg.messageId"/>
            	<form:hidden path="aMsg.userId"/>
            	<form:hidden path="aMsg.siteId"/>
            	<form:hidden path="aMsg.officeId"/>
            	<form:hidden path="aMsg.location"/>
            	<form:hidden path="aMsg.entityId"/>
            	<form:hidden path="aMsg.created"/>
				<form:hidden path="aMsg.language"/>
          		<form:hidden path="aMsg.status"/>				
            	<tr>
		<c:if test = '${fn:length(ltUser.aMsg.entityId) > 0}' >
			<td class="td_01" align="left">ENTITY ID: <c:out value="${ltUser.aMsg.entityId}"/></td>
			<td class="td_02" align="right">&nbsp;</td>
		</c:if>            
              
		<c:if test = '${fn:length(ltUser.aMsg.officeId) > 0}' >
			<td class="td_01" align="left">OFFICE ID: <c:out value="${ltUser.aMsg.officeId}"/></td>
			<td class="td_02" align="right">&nbsp;</td>
		</c:if>               	
                <td class="td_02" align="right">DATE SUBMITTED: <fmt:formatDate type="date" pattern="MM/dd/yyyy" value="${ltUser.aMsg.lastUpdated}" /></td>
              </tr>
            </tbody>
            </table>
          </div>
          <!-- // msg params -->
          <!-- msg details -->
          <div class="msg_details">
            <p class="p_detail_name"><form:label path="aMsg.messageText"><spring:message code="label.message" /></form:label></p>
            <p class="p_detail"><form:textarea path="aMsg.messageText" rows="3" cols="80" /></p>
          </div>
          <!-- // msg details -->   
          <!-- comment wrapper -->
          <div class="comment_wrapper">
	          <label for="comments" class="lb_01">Comments:</label>
  	        <form:textarea path="aMsg.comments" class="input_comment" rows="3"></form:textarea>
          </div>   
          <!-- buttons wrapper -->
          <div class="button_wrapper_03 clearfix">
         	<input type="button" value="Approve" class="btn_approve" onclick="return approveMsg('${ltUser.aMsg.messageId}')">
                <input type="button" onclick="rejectMsg('${ltUser.aMsg.messageId}')" class="btn_reject"  value="Reject">
          </div>            
          <!-- // buttons wrapper -->

        </div>
        <!-- // box -->
    
   </form:form>