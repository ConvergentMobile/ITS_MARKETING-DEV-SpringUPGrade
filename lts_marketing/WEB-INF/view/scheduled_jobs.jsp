<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/JavaScript">	
	function deleteJob1(trigName, trigGroup){		
		var ok = confirm("Are you sure you want to delete this job?");
		if (ok) {
			form = document.getElementById('thisForm');
			form.action = '/lts_marketing/deleteJob?triggerName='+trigName+'&triggerGroup='+trigGroup;
			form.submit();
		} 
	}
	
	function deleteJob(trigName, trigGroup) {
		var ok = confirm("Are you sure you want to delete this job?");
		if (! ok) {
			return;
		}
		
        $.ajax({
            type : 'POST',
            url : 'deleteJob',
        	data: 'triggerName='+trigName+'&triggerGroup='+trigGroup,
            success : function(result) {
        		alert(result);
				//$('#errwin').html('<div align="center">' + result + '</div>');
				//$( "#errwin" ).dialog('open');
				//popup(result, 1);
        		location.reload();        		
			},
			error : function(e) {
				alert('error: ' + e);
			}                        
        });	
	}
	
	function closeIt() {
		$('#dialog1').dialog('close');
	}	
</script>


<div>
  <form:form id="thisForm" method="post" action="" commandName="ltUser">

<div class="my_popup_01 mpu_01" id="id_popup_01">
	<div class="mpu_wrapper">
  	<!-- title -->
    <div class="mpu_title">
    	<table cellpadding="0" cellspacing="0" border="0" width="100%">
      <tr>
      	<td style="text-align:center"><h3>Scheduled Messages</h3></td>
      	<td><button title="Close (Esc)" type="button" onclick="closeIt()" class="mfp-close">×</button></td>
      </tr>
      </table>
    </div>
    <!-- // title -->
    <!-- table header -->
    <table width="100%" class="grid grid_search">
    <colgroup>
        <col width="20%" />
        <col width="25%" />
        <col width="20%" />
        <col width="4%" />
    </colgroup>
    <thead>
      <tr>
        <th class="th_01"><div>Campaign Name</div></th>
        <th class="th_02"><div>Message</div></th>
        <th class="th_05"><div>Time & Date</div></th>
        <th class="th_03"></th>
      </tr>
    </thead>
    </table>
    <!-- // table header -->
    
    <div id="id_search_pu">
      <table width="100%" class="grid grid_search">
      <colgroup>
        <col width="20%" />
        <col width="25%" />
        <col width="20%" />
        <col width="4%" />
      </colgroup>
	      <tbody>
            	<c:forEach var="row" items="${ltUser.reportRows}" varStatus="loopStatus">	 
            	<c:choose>
            	<c:when test="${loopStatus.index % 2 == 0}">
            		<tr class="even">
            	</c:when>
            	<c:otherwise>
			<tr class="odd">
		</c:otherwise>
		</c:choose>
			
			  <td class="td_01"><div><b>${row.column1}</b></div></td>
			  <td class="td_02"><div>${row.column2}</div></td>
			  <td class="td_03"><div>${row.column3}</div></td>
			  <td class="td_05"><div><a href="javascript:deleteJob('${row.column4}', '${row.column5}');" class="lnk_del_01"></a></div></td>
			</tr>  
		</c:forEach>

      </tbody>
    </div>
  </div>
</div>

	<div align="center">
    	   <a href="#" onclick="closeIt();" class="btn_dark_blue">Close</a>	
	</div>
   </form:form>
</div>