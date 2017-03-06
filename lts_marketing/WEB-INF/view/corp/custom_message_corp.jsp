<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/JavaScript">	
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

	function getMsg(msgId) {
        $.ajax({
            type : 'GET',
            url : 'messageForApproval',
            data: 'msgId=' + msgId,
            success : function(result) {
        		$('#box').html(result);
			},
			error : function(e) {
				alert('error: ' + e.text());
			}                        
        });
	}
	
	function submitMsg() {
		$('thisForm').attr('novalidate','novalidate');
		
		var msg = $('#sendSearchCityString').val();

		if (msg == "") {
			alert("Must specify message text");
			return false;
		}
		
		$.ajax({
		    type : 'POST',
		    url : 'createCustomMessage',
		    data: $("#thisForm").serialize(),
		    success : function(result) {
				//alert(result);
				$('#sendSearchCityString').val('');
				//$('#errwin').html('<div align="center">' + result + '</div>');
				//$( "#errwin" ).dialog('open');	
				popup(result, 1);
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

		<div id="dialog1" title="Alert" style="display:none">
		</div>
		
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
				<li class="si_dashboard"><a href="dashboardOffice">Dashboard</a></li>	
				<li class="si_custom_msg"><a href="customMessage">Create Custom Message</a></li>
				<li class="si_confirmation"><a href="confirmationMessage">Confirmation Message</a></li>	
				<li class="si_reports"><a href="getReports">Reports</a></li>	      	
				<li class="si_mobile_profile"><a href="getProfile">My Mobile Profile</a></li>	
			</c:if>   
			<c:if test = '${ltUser.user.roleActions[0].roleType == "Corporate"}'>
				<li class="si_dashboard"><a href="dashboardCorp">Dashboard</a></li>	
				<li class="si_custom_msg_approve selected"><a href="customMessageCorp">Approve Custom Messages</a></li>   
				<li class="si_confirmation"><a href="confirmationMessage">Confirmation Message</a></li>						
				<li class="si_send_msg"><a href="sendMessage">Send Message</a></li>	      	
				<li class="si_search"><a href="corpSearch">Search</a></li>	
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
    <div class="content" id="id_content">
    	<div class="nav_pointer pos_01"></div>
      <!-- subheader -->
      <div class="subheader clearfix">
      	<h1>Approve Custom Messages</h1>
        <p>Corporate</p>
      </div>
      <!-- // subheader -->
    	<div class="inner_box">
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
            	<tr>
              	<td class="td_01" align="left">ENTITY ID:</td>
                <td class="td_02" align="right">&nbsp;</td>
              </tr>
            	<tr>
              	<td class="td_01" align="left">OFFICE ID:</td>
                <td class="td_02" align="right">DATE SUBMITTED:</td>
              </tr>
            </tbody>
            </table>
          </div>
          <!-- // msg params -->
          <!-- msg details -->
          <div class="msg_details">
            <p class="p_detail_name">Message</p>
            <p class="p_detail"></p>
          </div>
        </div>
        <!-- // box -->
      </div>
    </div>
    <!-- // content area -->
    <!-- sidebar -->
    <div class="sidebar" id="id_sidebar">
    	<div class="inner">
      	<!-- title -->
        <div class="sb_title">
        	<h2 class="Mobile Marketing">Custom Messages Status</h2>
        </div>
        <!-- // title -->
        <!-- sidebar box type 1 -->
        <div class="sb_box_01">
          <h3>Messages Pending Approval</h3>
          <div class="grid_wrapper_01">
          	<table class="grid grid_01" width="100%">
            <colgroup>
            	<col width="77%" />
              <col width="23%" />
            </colgroup>
            <thead>
            	<tr>
              	<th class="th_01"></th>
                <th class="th_02"><div>Date</div></th>
              </tr>
            </thead>
            </table>
            <div id="id_pending">
              <table class="grid grid_01" width="100%">
              <colgroup>
                <col width="70%" />
                <col width="30%" />
              </colgroup>
              <tbody>						
		<c:forEach var="pMsg" items="${ltUser.pendingMsgs}" varStatus="loopStatus"> 
			<tr>
				<td class="col-1"><a href="#" onclick="getMsg('${pMsg.messageId}')"><c:out  value="${pMsg.messageText}"/></a></td>																	
				<td class="col-2"><fmt:formatDate type="date" pattern="MM/dd/yyyy" value="${pMsg.created}" /></td>
			</tr>
		</c:forEach>            
                </tbody>
                </table>
              </div>
            </div>
          </div>
        <!-- // sidebar box type 1 -->
            
        <!-- sidebar box type 1 -->
        <div class="sb_box_01">
          <h3>Approved Messages History</h3>
          <div class="grid_wrapper_01">
          	<table class="grid grid_01" width="100%">
            <colgroup>
            	<col width="77%" />
              <col width="23%" />
            </colgroup>
            <thead>
            	<tr>
              	<th class="th_01"></th>
                <th class="th_02"><div>Approved</div></th>
              </tr>
            </thead>
            </table>
            <div id="id_msg_history_001">
              <table class="grid grid_01" width="100%">
              <colgroup>
                <col width="77%" />
                <col width="23%" />
              </colgroup>
              <tbody>						
		<c:forEach var="pMsg" items="${ltUser.approvedMsgs}" varStatus="loopStatus"> 
			<tr>
				<td class="col-1"><a href="#" onclick="getMsg('${pMsg.messageId}')"><c:out  value="${pMsg.messageText}"/></a></td>																	
				<td class="col-2"><fmt:formatDate type="date" pattern="MM/dd/yyyy" value="${pMsg.updated}" /></td>
			</tr>
		</c:forEach>            
                </tbody>
              </table>
            </div>
          </div>
        </div>
        <!-- // sidebar box type 1 -->
        
        <!-- sidebar box type 1 -->
        <div class="sb_box_01">
          <h3>Rejected Messages History</h3>
          <div class="grid_wrapper_01">
          	<table class="grid grid_01" width="100%">
            <colgroup>
            	<col width="77%" />
              <col width="23%" />
            </colgroup>
            <thead>
            	<tr>
              	<th class="th_01"></th>
                <th class="th_02"><div>Rejected</div></th>
              </tr>
            </thead>
            </table>

          	<div id="id_rejected_messages_001">
              <table class="grid grid_01" width="100%">
              <colgroup>
                <col width="77%" />
                <col width="23%" />
              </colgroup>
              <tbody>						
		<c:forEach var="pMsg" items="${ltUser.customMsgs}" varStatus="loopStatus"> 
			<tr>
				<td class="col-1"><a href="#" onclick="getMsg('${pMsg.messageId}')"><c:out  value="${pMsg.messageText}"/></a></td>																	
				<td class="col-2"><fmt:formatDate type="date" pattern="MM/dd/yyyy" value="${pMsg.updated}" /></td>
			</tr>
		</c:forEach>            
                </tbody>
              </table>
            </div>  
          </div>
        </div>
        <!-- // sidebar box type 1 -->

      </div>
      
    </div>
    <!-- // sidebar --> 
    
    <div class="floatfix"></div>
  </div>
  <!-- // content wrapper -->
</div>

		</form:form>
	
<!-- // page wrapper -->    
        

