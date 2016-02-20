<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script LANGUAGE="JavaScript">
	$(document).ready(function() {	
		$("#listIds").pickList();
	});


	function createList() {
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

	<h3>Create a New List</h3>
	<div id="id_popup_01" class="my_popup_01 mpu_01">
	<br/><br/>	
	     List Name: <form:input path="sendSearchKeywordString"/>
	<div>
	<br/>
		<form:select path="listIds" multiple="multiple">	
           		<c:forEach var="row" items="${ltUser.listData}" varStatus="loopStatus">	 		
				<option value="${row.mobilePhone} : ${row.firstName} : ${row.lastName}">${row.mobilePhone} -- ${row.firstName} ${row.lastName}</option>
	   		</c:forEach>
		</form:select>	   		
	</div>

    	<div align="center">
		<a href="#" onclick="createList();" class="btn_dark_blue">Create</a>	    	
    	    <a href="#" onclick="closeIt();" class="btn_dark_blue">Close</a>	
	</div>
	
   </form:form>
</div>      
