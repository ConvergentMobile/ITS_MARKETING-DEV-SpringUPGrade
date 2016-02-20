<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
 
 <!-- this is the one that is used -->
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

	$("#thisForm1").submit(function(e) {
	    e.preventDefault();
	});
	
	function deleteNumber(number) {
		var listId = $('#searchDMAString').val();
	        $.ajax({
	            type : 'POST',
	            url : 'deleteNumber',
	        	    data: 'listId=' + listId + '&number=' + number,
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
	
	function searchNumber() {
		var num = $('#searchStateString').val();
		if (num == '') {
			alert('Must enter a number or name for Search');
			return;
		}
	
		var outStr = '<table width="100%" class="grid grid_search">';
	      	outStr += '<colgroup>';
		outStr += '<col width="32%" />';
		outStr += '<col width="30%" />';
		outStr += '<col width="32%" />';
		outStr += '<col width="6%" />';
	      	outStr += '</colgroup>';
	      	outStr += '<tbody>';
	      
	        outStr += '<div class="clear_res_box"><a href="JavaScript:resetIt()" class="btn_clear_search"><span>Clear Search</span></a></div>';
	      	var foundIt = 0;
	      	var idx = 0;
	        <c:forEach var="row" items="${ltUser.listData}" varStatus="loopStatus">	 
    			if ('${row.mobilePhone}'.toLowerCase().search(num.toLowerCase()) >= 0 
    				|| '${row.lastName}'.toLowerCase().search(num.toLowerCase()) >= 0
            		|| '${row.firstName}'.toLowerCase().search(num.toLowerCase()) >= 0)	{	
	        	  	foundIt = 1;
	        	  	idx = idx + 1;
				<c:choose>
				<c:when test="${idx % 2 == 0}">
					outStr += '<tr class="even">';
				</c:when>
				<c:otherwise>
					outStr += '<tr class="odd">';
				</c:otherwise>
				</c:choose>	        	  
			  	outStr += '<td class="td_01"><div><b>${row.mobilePhone}</b></div></td>';
			  	outStr += '<td class="td_02"><div>${row.firstName} ${row.lastName}</div></td>';
			  	outStr += '<td class="td_03"><div>${row.lastUpdated}</div></td>';
			  	outStr += '<td class="td_05"><a href="JavaScript:void(0)" onclick="deleteNumber(${row.mobilePhone})" class="lnk_del_01"></a></td>';	        	
	        	  	outStr += '</tr>';
	        	}
	        </c:forEach>
	        
	        if (foundIt == 0) {
	        	outStr += '<tr><td colspan=3 align="center">No matching record found</td></tr>';
	        }
	        
	        outStr += '</tbody>';
	        outStr += '</table>';

		$('#id_search_pu').empty().append(outStr);
	}	
	
	function resetIt() {
		$('#searchStateString').val('');

		var outStr = '<table width="100%" class="grid grid_search">';
	      	outStr += '<colgroup>';
		outStr += '<col width="32%" />';
		outStr += '<col width="30%" />';
		outStr += '<col width="32%" />';
		outStr += '<col width="6%" />';
	      	outStr += '</colgroup>';
	      	outStr += '<tbody>';
	      
	        <c:forEach var="row" items="${ltUser.listData}" varStatus="loopStatus">	 
			<c:choose>
			<c:when test="${loopStatus.index % 2 == 0}">
				outStr += '<tr class="even">';
			</c:when>
			<c:otherwise>
				outStr += '<tr class="odd">';
			</c:otherwise>
			</c:choose> 

			outStr += '<td class="td_01"><div><b>${row.mobilePhone}</b></div></td>';
			outStr += '<td class="td_02"><div>${row.firstName} ${row.lastName}</div></td>';
			outStr += '<td class="td_03"><div>${row.lastUpdated}</div></td>';
			outStr += '<td class="td_05"><a href="JavaScript:void(0)" onclick="deleteNumber(${row.mobilePhone})" class="lnk_del_01"></a></td>';	        	
	        	outStr += '</tr>';
	        </c:forEach>
	        
	        outStr += '</tbody>';
	        outStr += '</table>';

		$('#id_search_pu').empty().append(outStr);
	}
	
	function closeIt() {
		$('#dialog1').dialog('close');
	}	

</script>

    <div id="dialog1">

   <form:form id="thisForm1" method="post" action="" commandName="ltUser">
	<form:hidden path="searchDMAString"/>

	<div id="id_popup_01" class="my_popup_01 mpu_01">
	
		<div class="mpu_wrapper">
		<!-- title -->
	    <div class="mpu_title">
		<table cellpadding="0" cellspacing="0" border="0" width="100%">
	      <tr>
		<td><h3>Your Mobile Opt-Ins: <span class="cred">${fn:length(ltUser.listData)}</span></h3></td>
		<td class="td_02"><form:input path="searchStateString" placeholder="Search by name"/>
			<a href="#" onclick="searchNumber()"><img src="./images/bg_sbn.png"></a></td>
	      </tr>
	      </table>
	    </div>
	    <!-- // title -->
	    <!-- table header -->
	    <table width="100%" class="grid grid_search">
	    <colgroup>
	      <col width="32%" />
	      <col width="30%" />
	      <col width="32%" />
	      <col width="6%" />
	    </colgroup>
	    <thead>
	      <tr>
		<th class="th_01"><div>Mobile#</div></th>
		<th class="th_02"><div>First / Last</div></th>
		<th class="th_03"><div>Time & Date</div></th>
		<th class="th_05"></th>
	      </tr>
	    </thead>
	    </table>
	    <!-- // table header -->

	    <div id="id_search_pu">
	      <table width="100%" class="grid grid_search">
	      <colgroup>
		<col width="32%" />
		<col width="30%" />
		<col width="32%" />
		<col width="6%" />
	      </colgroup>
	      <tbody>
            	<c:forEach var="row" items="${ltUser.listData}" varStatus="loopStatus">	 
            	<c:choose>
            	<c:when test="${loopStatus.index % 2 == 0}">
            		<tr class="even">
            	</c:when>
            	<c:otherwise>
			<tr class="odd">
		</c:otherwise>
		</c:choose>
			
			  <td class="td_01"><div><b>${row.mobilePhone}</b></div></td>
			  <td class="td_02"><div>${row.firstName} ${row.lastName}</div></td>
			  <td class="td_03"><div>${row.lastUpdated}</div></td>
			  <td class="td_05"><a href="JavaScript:void(0)" onclick="deleteNumber('${row.mobilePhone}')" class="lnk_del_01"></a></td>
			</tr>  
		</c:forEach>

      </tbody>
      </table>
    </div>
    
  </div>                      
    
    	<div align="center">
    	    <a href="#" onclick="closeIt();" class="btn_dark_blue">Close</a>	
	</div>
	
   </form:form>
</div>      
</div>