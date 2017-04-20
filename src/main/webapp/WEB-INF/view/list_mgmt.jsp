<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script LANGUAGE="JavaScript">
	$(document).ready(function() {	
		$("#listIds").pickList(
			{sortItems: false}
		);
	});


	function createList() {
		var listname = $('#thisForm1 #sendSearchKeywordString').val();
		if (listname == '') {
			alert("Please specify the New list name");
			return;
		}
		
		$.ajax({
		    type : 'POST',
		    url : 'listMgmt',
		    data: $('#thisForm1').serialize(),
		    success : function(result) {
		    		closeIt();
		    		popup(result, 1);
				},
				error : function(e) {
					popup(result, 0);
				}                        
		});		
	}
	
	function closeIt() {
		$('#dialog1').dialog('close');
	}	

</script>

<style>
.pickList_sourceListContainer, .pickList_controlsContainer, .pickList_targetListContainer { float: left; margin: 0.25em; }
.pickList_controlsContainer { text-align: center; }
.pickList_controlsContainer button { display: block; width: 100%; text-align: center; }
.pickList_list { list-style-type: none; margin: 0; padding: 0; float: left; width: 300px; height: 375px; border: 1px inset #eee; overflow-y: auto; cursor: default; }
.pickList_selectedListItem { background-color: #a3c8f5; }
.pickList_listLabel { font-size: 0.9em; font-weight: bold; text-align: center; }
.pickList_clear { clear: both; }
</style>

    <div>

   <form:form id="thisForm1" method="post" action="" commandName="ltUser">
	<form:hidden path="searchDMAString"/>
	
	<div id="id_popup_01" class="my_popup_01 mpu_01" style="width: 1200px;">
	<div class="mpu_title" style="text-align: center;">
		<h3>Create a New List</h3>
			<button title="Close (Esc)" type="button" onclick="closeIt()" class="mfp-close">&times;</button>
	    </div>
	     <p style="padding: 20px 20px 0;">New List Name: <form:input path="sendSearchKeywordString" style="padding: 6px;"/></p>
	<div style="padding: 0 20px;">
	<br/>
		<form:select path="listIds" multiple="multiple">	
           		<c:forEach var="row" items="${ltUser.listData}" varStatus="loopStatus">	 
           			<fmt:formatDate type="date" pattern="MM/dd/yyyy" value="${row.lastUpdated}" var="ts"/>        		
				<option value="${row.mobilePhone} : ${row.firstName} : ${row.lastName}">
				${ts} -- ${row.mobilePhone} -- ${row.firstName} ${row.lastName}</option>
	   		</c:forEach>
		</form:select>	   		
	</div>

    	<div align="center">
		<a href="#" onclick="createList();" class="btn_dark_blue" style="display: inline-block; padding: 0 20px;margin-top:10px;">Create</a>	    	
	</div>
	
   </form:form>
</div>      
