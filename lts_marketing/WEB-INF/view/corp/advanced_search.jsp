<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
 <%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<script type="text/JavaScript" SRC="scripts/CalendarPopup.js"></script>
<script type="text/JavaScript">	
	var cal = new CalendarPopup();

	function runSort(sortBy) {
		var form = document.getElementById("thisForm");

		var ev = form.elements["eventId"].value;
		var oid = form.elements["officeId"].value;

		var oldCol = form.elements["sortColumn"].value;
		var oldOrd = form.elements["sortOrder"].value;
		
		if (sortBy == oldCol) {		
			if (oldOrd == "asc") {
				form.elements["sortOrder"].value = 'desc';
			} else {
				form.elements["sortOrder"].value = 'asc';
			}
		} else {
			form.elements["sortColumn"].value = sortBy;
			form.elements["sortOrder"].value = 'asc';
		}
		
		form.action = 'sortListData?sortBy=' + sortBy;
		form.submit();	
	}
	
	function searchIt(){
        $.ajax({
            type : 'POST',
            url : 'corpSearch',
            data: $("#thisForm").serialize(),
            success : function(result) {
        		$('#office').html(result);
			},
			error : function(e) {
				alert('error: ' + e.text());
			}                        
        });
	}
	
	function getProfile(userId) {
	        $.ajax({
	            type : 'GET',
	            url : 'getProfile',
	            data: 'userId=' + userId,
	            success : function(result) {
            		$('#office').html(result);
				},
				error : function(e) {
					alert('error: ' + e.text());
				}                        
	        });
	}	
</script>

  <!-- // header -->
  <!-- content wrapper -->
  <div class="content_wrapper" id="content_wrapper">
   <form:form id="thisForm" method="post" action="dashboard" commandName="ltUser">
		
  	<!-- left side navigation -->
  	<ul class="ul_left_nav">
			<c:if test = '${ltUser.user.roleActions[0].roleType == "Entity"}'>  	
				<li class="si_dashboard"><a href="dashboardEntity">Dashboard</a></li>
				<li class="si_custom_msg"><a href="customMessageEntity">Create Custom Message</a></li>
				<li class="si_confirmation"><a href="confirmationMessage">Confirmation Message</a></li>		
				<li class="si_send_msg"><a href="sendMessage">Send Message</a></li>
				<li class="si_reports"><a href="getReports">Reports</a></li>     		
				<li class="si_mobile_profile"><a href="getProfile">My Mobile Profile</a></li>	
			</c:if>
			<c:if test = '${ltUser.user.roleActions[0].roleType == "Office"}'>
				<li class="si_dashboard selected"><a href="dashboardOffice">Dashboard</a></li>	
				<li class="si_custom_msg"><a href="customMessage">Create Custom Message</a></li>
				<li class="si_confirmation"><a href="confirmationMessage">Confirmation Message</a></li>	
				<li class="si_reports"><a href="getReports">Reports</a></li>	      	
				<li class="si_mobile_profile"><a href="getProfile">My Mobile Profile</a></li>	
			</c:if>   
			<c:if test = '${ltUser.user.roleActions[0].roleType == "Corporate"}'>
				<li class="si_dashboard"><a href="dashboardCorp">Dashboard</a></li>	
				<li class="si_custom_msg_approve"><a href="customMessageCorp">Approve Custom Messages</a></li>  
				<li class="si_confirmation"><a href="confirmationMessage">Confirmation Message</a></li>						 
				<li class="si_send_msg"><a href="sendMessage">Send Message</a></li>	      	
				<li class="si_search selected"><a href="corpSearch">Search</a></li>	
				<li class="si_reports"><a href="getReports">Reports</a></li>	      	
			</c:if> 
			<c:if test = '${ltUser.user.roleActions[0].roleType == "AD"}'>  	
				<li class="si_dashboard"><a href="dashboardAD">Dashboard</a></li>
				<li class="si_custom_msg"><a href="customMessageEntity">Create Custom Message</a></li>
				<li class="si_reports"><a href="getReports">Reports</a></li>		
			</c:if>			
    		
    		<li class="si_toolbox"><a href="cmtoolbox">Convergent Toolbox</a></li>
    </ul>
    <!-- // left side navigation -->
    <!-- content area -->
    <div class="content" id="id_content_01" style="width: 100%;">
      <!-- subheader -->
      <div class="subheader clearfix">
      	<h1>SEARCH</h1>
        <p>Corporate</p>
      </div>
      <!-- // subheader -->
    	<div class="inner_box">
      	<!-- box -->
        <div class="box box_red_title box_quick_search">
        	<!-- title -->
        	<div class="box_title mb9">
          	<h2>Search</h2>
          </div>
          <!-- // title -->
          <div class="wide_column_wrapper search_container_01">
          	<table width="100%" class="grid grid_06">
            <colgroup>
            	<col width="82"/>
              <col width="208"/>
              <col width="60"/>
              <col width="210"/>
            </colgroup>
            <tbody>
              <tr>
              	<td class="td_01"><label for="keyword">Keyword</label></td>
                <td class="td_02"><form:input path="searchKeywordString"/></td>
                <td class="td_01"><label for="office">Office ID</label></td>
                <td class="td_02"><form:input path="searchOfficeIdString"/></td>
              	<td class="td_01"><label for="city">City</label></td>
                <td class="td_02"><form:input path="searchCityString"/></td>
              </tr>
              <tr>
                <td class="td_01"><label for="dma">DMA</label></td>
                <td class="td_02"><form:input path="searchDMAString"/></td>
                <td class="td_01"><label for="entity">Entity ID</label></td>
                <td class="td_02"><form:input path="searchEntityIdString"/></td>
              	<td class="td_01"><label for="state">State</label></td>
                <td class="td_02"><form:input path="searchStateString"/></td>
	             </tr>
            </tbody>
            </table>
            <div class="button_wrapper_05"><center>
            	<input type="button" value="Search" class="btn_dark_blue btn_03" onclick="return searchIt();">
            	</center>
            </div>
          </div>
        </div>
        <!-- // box -->            
      </div>
    </div>
    <!-- // content area -->

    <div class="floatfix"></div>
    
    <!-- search results wrapper -->
    <div class="search_results_wrapper srw_fix_01">
    	<div class="inner">
        <div class="box box_grey_title box_sresults">
        	<!-- title -->
        	<div class="box_title mb9">
          	<h2>Search Results</h2>
          </div>
          <!-- // title -->
          <div class="search_results">
          	<c:if test="{fn:length(ltUser.profiles) == 0}">
          		No keywords found
          	</c:if>
          	<div id="results_scroll">
		   <table>      	
			  <tr>          
			  <c:forEach var="kw" items="${ltUser.profiles}" varStatus="loopStatus">
					<td><a href='#' onclick="getProfile('${kw.userId}')">
					<c:out value="${kw.customField2}"/>-<c:out value="${kw.keyword}"/></a></td>
						<c:if test = "${loopStatus.count % 6 == 0}">
						</tr>
						<tr>
					</c:if>	          
				</c:forEach>
		   </table>          	
            </div>
          </div>
        </div>

      </div>
    </div>
    <!-- // search results wrapper -->

</div>

  <!-- // content wrapper -->
  
		</form:form>
	
<!-- // page wrapper -->  