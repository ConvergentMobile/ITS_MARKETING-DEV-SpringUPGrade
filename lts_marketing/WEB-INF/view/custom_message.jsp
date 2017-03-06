<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/JavaScript">	
	$(document).ready(function() {
		$('#sendSearchCityString').val('');
		$('#sendSearchDMAString').val('');	
		
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
				//$('#errwin').html('<div align="center">Error:' + error + '</div>');
				//$( "#errwin" ).dialog();	
				popup(result, 1);
				//location.reload();				
			},
				error : function(e) {
					alert('error: ' + e.text());
			}                        
		});
				
	}
	
	function showMsg(id, msg, comments) {
		$('#sendSearchCityString').val(msg);
		if (comments != '') {
			$('#sendSearchDMAString').val(comments);
			$('#rejectReason').show();
		}
	}		

/*
	$(document).ready(function() { 	
			$("#thisForm").validate({
				rules: {
					sendSearchCityString: 'required'
				},
				messages: {
					sendSearchCityString: 'Must specify msg'
				}
    		});
    });
 */
</script>

  <!-- // header -->
  <!-- content wrapper -->
   <form:form id="thisForm" method="post" action="" commandName="ltUser">
  
  <div class="content_wrapper">
  	<!-- left side navigation -->
  	<ul class="ul_left_nav">
			<c:if test = '${ltUser.user.roleActions[0].roleType == "Entity"}'>  	
				<li class="si_dashboard"><a href="dashboardEntity">Dashboard</a></li>
				<li class="si_custom_msg selected"><a href="customMessageEntity">Create Custom Message</a></li>
				<li class="si_confirmation"><a href="confirmationMessage">Confirmation Message</a></li>		
				<li class="si_send_msg"><a href="sendMessage">Send Message</a></li>
				<li class="si_sendafriend"><a href="sendAFriend">Send a Friend</a></li>
				<li class="si_reports"><a href="getReports">Reports</a></li>     		
				<li class="si_mobile_profile"><a href="getProfile">My Mobile Profile</a></li>	
			</c:if>
			<c:if test = '${ltUser.user.roleActions[0].roleType == "Office"}'>
				<li class="si_dashboard"><a href="dashboardOffice">Dashboard</a></li>	
				<li class="si_custom_msg selected"><a href="customMessage">Create Custom Message</a></li>
				<li class="si_confirmation"><a href="confirmationMessage">Confirmation Message</a></li>	
				<li class="si_reports"><a href="getReports">Reports</a></li>	      	
				<li class="si_mobile_profile"><a href="getProfile">My Mobile Profile</a></li>	
			</c:if>   
			<c:if test = '${ltUser.user.roleActions[0].roleType == "Corporate"}'>
				<li class="si_dashboard"><a href="dashboardCorp">Dashboard</a></li>	
				<li class="si_custom_msg_approve"><a href="customMessageCorp">Approve Custom Messages</a></li>   
				<li class="si_confirmation"><a href="confirmationMessage">Confirmation Message</a></li>					
				<li class="si_send_msg"><a href="sendMessage">Send Message</a></li>	      	
				<li class="si_search"><a href="corpSearch">Search</a></li>	
				<li class="si_reports"><a href="getReports">Reports</a></li>	      	
			</c:if> 
			<c:if test = '${ltUser.user.roleActions[0].roleType == "AD"}'>  	
				<li class="si_dashboard"><a href="dashboardAD">Dashboard</a></li>
				<li class="si_custom_msg selected"><a href="customMessageEntity">Create Custom Message</a></li>
				<li class="si_reports"><a href="getReports">Reports</a></li>		
				<li class="si_mobile_profile"><a href="getProfile">My Mobile Profile</a></li>
			</c:if>	
      	
      		<li class="si_toolbox"><a href="cmtoolbox">Convergent Toolbox</a></li>
    </ul>
    <!-- // left side navigation -->
    <!-- content area -->
    <div class="content" id="id_content_05">
    
    	<div class="nav_pointer pos_01"></div>
      <!-- subheader -->
      <div class="subheader clearfix">
      	<h1>Custom Message</h1>
        <p>Office Id: <c:out value="${sites[0].customField2}"/></p>
      </div>
      <!-- // subheader -->
    	<div class="inner_box">
      	<!-- box -->
        <div class="box box_grey_title box_message">
        	<!-- title -->
        	<div class="box_title mb9">
          	<h2>Create New Custom Message</h2>
          </div>
          <!-- // title -->
          <!-- wide_column -->
          <div class="wide_column_wrapper custom_msg">
          	<div class="description">
              <p>
                All new messages will be reviewed for compliance with the corporate policies. Your message may be Accepted or Rejected. Accepted and Rejected messages will appear on the right hand-side panel. Messages are usually reviewed within 1 week after submission.
              </p>
              <p>
                Please type your new Message Content in the space below.            
              </p>
            </div>
            <label for="msg_content" class="lb_01">Message:</label>
            <div class="big_input_wrapper">
		<form:textarea path="sendSearchCityString" class="input_big" maxlength="140" rows="9"/>					            
            </div>
            <div id="rejectReason" class="big_input_wrapper" style="display: none">
            	<label for="msg_content" class="lb_01">Comments:</label>            
				<form:textarea path="sendSearchDMAString" class="input_big" maxlength="140" rows="3"/>	
            </div>                  
            <div class="button_wrapper_01 clearfix">
            	<input type="button" class="btn_green btn_01" onclick="javascript:submitMsg()" value="Create & Send for Approval">
            </div>
          </div>
          <!-- // wide_column -->
        </div>
        <!-- // box -->            
      </div>
    </div>
    <!-- // content area -->
    <!-- sidebar -->
    <div class="sidebar" id="id_sidebar_05">
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
              	<th class="th_01"><div>Message</div></th>
                <th class="th_02"><div>Submitted</div></th>
              </tr>
            </thead>          
            <tbody>
		<c:forEach var="pMsg" items="${ltUser.pendingMsgs}" varStatus="loopStatus"> 
			<c:set var="keyword" value="${site.keyword}" />
			<tr>
				<td class="td_01"><div><c:out  value="${pMsg.messageText}"/></div></td>								
				<td class="td_02"><div><fmt:formatDate type="date" pattern="MM/dd/yyyy" value="${pMsg.created}" /></div></td>
			</tr>
		</c:forEach>            
            </tbody>
            </table>
          </div>
        </div>
        <!-- // sidebar box type 1 -->
		
        <!-- sidebar box type 1 -->
        <div class="sb_box_01">
          <h3>Approved Messages</h3>
          <div class="grid_wrapper_01">
          	<table class="grid grid_01" width="100%">
            <colgroup>
            	<col width="77%" />
              <col width="23%" />
            </colgroup>
            <thead>
            	<tr>
              	<th class="th_01"><div>Message</div></th>
                <th class="th_02"><div>Approved</div></th>
              </tr>
            </thead>
            <tbody>
		<c:forEach var="pMsg" items="${ltUser.approvedMsgs}" varStatus="loopStatus"> 
			<c:set var="keyword" value="${site.keyword}" />
			<tr>
				<td class="td_01"><div><c:out value="${pMsg.messageText}"/></div></td>								
				<td class="td_02"><div><fmt:formatDate type="date" pattern="MM/dd/yyyy" value="${pMsg.updated}" /></div></td>
			</tr>
		</c:forEach>    
            </tbody>
            </table>
          </div>
        </div>
        <!-- // sidebar box type 1 -->

        <!-- sidebar box type 1 -->
        <div class="sb_box_01">
          <h3>Rejected Messages</h3>
          <div class="grid_wrapper_01">
          	<table class="grid grid_01" width="100%">
            <colgroup>
            	<col width="77%" />
              <col width="23%" />
            </colgroup>
            <thead>
            	<tr>
              	<th class="th_01"><div>Message</div></th>
                <th class="th_02"><div>Rejected</div></th>
              </tr>
            </thead>
            <tbody>
		<c:forEach var="pMsg" items="${ltUser.customMsgs}" varStatus="loopStatus"> 
			<c:set var="keyword" value="${site.keyword}" />
			<tr>
				<td class="td_01"><div><a href="#" onclick="showMsg('${pMsg.messageId}', '${pMsg.messageText}', '${pMsg.comments}')"><c:out  value="${pMsg.messageText}"/></a></div></td>								
				<td class="td_02"><div><fmt:formatDate type="date" pattern="MM/dd/yyyy" value="${pMsg.updated}" /></div></td>
			</tr>
		</c:forEach>  
            </tbody>
            </table>
          </div>
        </div>
        <!-- // sidebar box type 1 -->
      </div>

    	<div class="inner">
      	<!-- title -->
        <div class="sb_title">
        	<h2 class="Mobile Marketing">Mobile Marketing</h2>
        </div>
        <!-- // title -->
        <!-- sidebar box type 1 -->
        <div class="sb_box_02">
          <h3>Key Points For This Page</h3>
          <div class="grid_wrapper_02">
           <div class="infoslider">
           <p class="slide">          
<ul>
<li>1. <b>Select Your Office</b> you want to create your message for.</li><br/>
<li>2. <b>Create a Custom Message</b> and send it directly to corporate for approval.  This is a direct line between you and corporate.  
No other Entity will see the messages you create.</li><br/>
<li>3. <b>Check the Status</b> of where your custom message is in the approval process.  
This should only take a few days at the most.  </li>
</ul>
 	</p>
 	</div>
          </div>
        </div>
        <!-- // sidebar box type 1 -->
	</div>
          
    </div>
    <!-- // sidebar -->    
    <div class="floatfix"></div>
  </div>
  
  		</form:form>

  <!-- // content wrapper -->
  

