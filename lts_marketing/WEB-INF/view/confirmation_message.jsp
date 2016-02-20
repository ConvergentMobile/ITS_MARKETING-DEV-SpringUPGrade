<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<style>
.no-close .ui-dialog-titlebar-close {
display: none;
</style>

<script type="text/JavaScript">	
	function resetForm() {
		$('#thisForm')[0].reset();			
		$('input[type="radio"]').prop('checked', false);
		$('input:checkbox').removeAttr('checked');
	}
	
	 $(document).ready(function() {
		resetForm();
		
		$( "#errwin" ).dialog({
			title: 'Alerts & Notifications',
			width: 400,
			height: 200,		
			dialogClass: 'no-close',
			autoOpen: false, 
			buttons: {
			  OK: function() {
				$(this).dialog("close");
				//location.reload();
			  }
		       },
		});			
	});
	
	function createHotspot(keyword){		
		window.open('createHotspot?keyword=' + keyword,"mywindow","scrollbars=yes,menubar=1,resizable=1,width=450,height=550");		
	}

	function showSelectedMsg(elem, msgType, inirpt) {
		elemId = $(elem).attr("id");

		if (inirpt == 'ini') {		
			var msgText = $("label[for='"+elemId+"']").text();					
			$('#category\\.initialMessage').val(msgText);			
		}
		if (inirpt == "rpt") {		
			var msgText = $("label[for='"+elemId+"']").text();					
			$('#category\\.autoResponse').val(msgText);			
		}	
					
	}
	
	function getProfile(userId) {
		//var userId = $( "#searchOfficeIdString option:selected" ).val();	
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
	
	function getCategory1() {
		var userId = $( "#searchOfficeIdString option:selected" ).val();
		var offId = $( "#searchOfficeIdString option:selected" ).text();
	        $.ajax({
	            type : 'GET',
	            url : 'confirmationMessage',
	            data: 'userId=' + userId + '&officeId=' + offId,
	            success : function(result) {
            		$('#office').html(result);	            
				},
				error : function(e) {
					alert('error: ' + e.text());
				}                        
	        });		
	}
	
	//Use this as we are retuning the entire page anyway
	function getCategory() {
		//var userId = $( "#searchOfficeIdString option:selected" ).val();
		var offId = $( "#searchOfficeIdString option:selected" ).text();
		var form = document.getElementById("thisForm");
		//form.action  = 'confirmationMessage?userId=' + userId + '&officeId=' + offId;
		form.action  = 'confirmationMessage?officeId=' + offId;
		
		form.submit();
		
	
	}	
	
	function saveMsg(msgType) {
		var offId = $('#searchOfficeIdString').val();
		if (offId == "") {
			alert("Must select an Office");
			return;
		}
		if (msgType == 'ini') {
			if ($('#category\\.initialMessage').val() == '') {
				alert("Must select a msg");
				return;
			}
		}
		$('#currentPage').val(msgType);			
	        $.ajax({
	            type : 'POST',
	            url : 'saveConfirmationMessage',
            	data: $("#thisForm").serialize(),
	            success : function(result) {
            		//alert(result);
        			//$('#errwin').html('<div align="center">' + result + '</div>');
        			//$( "#errwin" ).dialog('open');  
        			popup(result, 0);        			
				},
				error : function(e) {
					alert('error: ' + e.text());
				}                        
	        });
	}	
	
	function setMsgType(msgType) {
		if (msgType == 'Custom') {
			$('#msgsCust').show();
			$('#msgsCorp').hide();			
		}
		if (msgType == 'Corporate') {
			$('#msgsCorp').show();
			$('#msgsCust').hide();			
		}
		
		if (msgType == 'CustomRpt') {
			$('#msgsCustRpt').show();
			$('#msgsCorpRpt').hide();			
		}
		if (msgType == 'CorporateRpt') {
			$('#msgsCorpRpt').show();
			$('#msgsCustRpt').hide();			
		}		
	}
</script>

  <!-- // header -->
  <!-- content wrapper -->
   <form:form id="thisForm" method="post" action="dashboard" commandName="ltUser">
  
  <div class="content_wrapper">
  	<!-- left side navigation -->
  	<ul class="ul_left_nav">
			<c:if test = '${ltUser.user.roleActions[0].roleType == "Entity"}'>  	
				<li class="si_dashboard"><a href="dashboardEntity">Dashboard</a></li>
				<li class="si_custom_msg"><a href="customMessageEntity">Create Custom Message</a></li>
				<li class="si_confirmation selected"><a href="confirmationMessage">Confirmation Message</a></li>		
				<li class="si_send_msg"><a href="sendMessage">Send Message</a></li>
				<li class="si_sendafriend"><a href="sendAFriend">Send a Friend</a></li>
				<li class="si_reports"><a href="getReports">Reports</a></li>     		
				<li class="si_mobile_profile"><a href="getProfile">My Mobile Profile</a></li>	
			</c:if>
			<c:if test = '${ltUser.user.roleActions[0].roleType == "Office"}'>
				<li class="si_dashboard"><a href="dashboardOffice">Dashboard</a></li>	
				<li class="si_custom_msg"><a href="customMessage">Create Custom Message</a></li>
				<li class="si_confirmation selected"><a href="confirmationMessage">Confirmation Message</a></li>	
				<li class="si_reports"><a href="getReports">Reports</a></li>	      	
				<li class="si_mobile_profile"><a href="getProfile">My Mobile Profile</a></li>	
			</c:if>   
			<c:if test = '${ltUser.user.roleActions[0].roleType == "Corporate"}'>
				<li class="si_dashboard"><a href="dashboardCorp">Dashboard</a></li>	
				<li class="si_custom_msg_approve"><a href="customMessageCorp">Approve Custom Messages</a></li>   
				<li class="si_confirmation selected"><a href="confirmationMessage">Confirmation Message</a></li>						
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
      	<h1>Confirmation Message</h1>
        <p>Office Id: 
        <c:choose>
          <c:when test = '${ltUser.user.roleActions[0].roleType != "Office"}'>
          <div class="select_wrapper_01">
                <form:select path="searchOfficeIdString" class="select_send_notification" onchange="getCategory()" >
                	<form:option value="">Select an Office</form:option>
                	<form:options items="${sites}" itemValue="userId" itemLabel="customField2"/>            	
            	</form:select>
          </div>
          </c:when>
          <c:otherwise>
          	<c:out value="${sites[0].customField2}"/>
          	<form:hidden path="searchOfficeIdString" value="${sites[0].userId}"/>
          </c:otherwise>
        </c:choose>
        </p>
      </div>
      <!-- // subheader -->
      
      <form:hidden path="currentPage"/>
      
    	<div class="inner_box">
      	<!-- box -->
        <div class="box box_blue_title box_opt_in">
        	<!-- title -->
        	<div class="box_title mb9">
          	<h2>First Time Opt-In</h2>
          </div>
          <!-- // title -->
           <form:hidden path="sendSearchCityString"/>

          <!-- two columns -->
          <div class="two_cols_wrapper_01 clearfix">
          	<!-- left column -->
            	<h3 class="h3_sub ico_select">Select a Message:</h3>
              <!-- tabs ///////////////////////////////////////////////////////////////////  -->
              <div class="tabs_01 tabs_height_02" id="tabs_01">
                <ul class="ul_tabs_select">
                  <li class="tab_01"><a href="#tabs_01_1" class="selected">Corporate</a></li>
                  <li class="tab_02"><a href="#tabs_01_2">Spanish</a></li>
                  <li class="tab_02"><a href="#tabs_01_3">Custom</a></li>                  
                </ul>
                <!-- tab 01 -->
                <div class="tabs_02_content" id="tabs_01_1">
                  <ul class="ul_scroll_list scroll_list_002"> 
                  <c:if test="${fn:length(ltUser.approvedMsgs) > 0}">
                  	<form:radiobuttons element="li" path="sendSearchCityString" onchange="showSelectedMsg(this, 'Corp', 'ini')"
                  		items="${ltUser.approvedMsgs}" itemValue="messageId" itemLabel="messageText"/>  
                  </c:if>
                  </ul>
                </div>                  
                 <!-- // tab 01 -->
                <!-- tab 02 -->
                <div class="tabs_02_content" id="tabs_01_2">
                  <ul class="ul_scroll_list scroll_list_002">  
                  <c:if test="${fn:length(ltUser.approvedMsgsSP) > 0}">                  
                  	<form:radiobuttons element="li" path="sendSearchCityString" onchange="showSelectedMsg(this, 'Corp', 'ini')"
                  		items="${ltUser.approvedMsgsSP}" itemValue="messageId" itemLabel="messageText"/>  
                  </c:if>
                  </ul>
                </div>                  
                 <!-- // tab 02 -->                 
                 <!-- tab 03 -->
                 <div class="tabs_02_content" id="tabs_01_3">
                 <c:if test = "${fn:length(ltUser.customMsgs) > 0}" >
                   <ul class="ul_scroll_list scroll_list_001">   
                  <c:if test="${fn:length(ltUser.customMsgs) > 0}">                   
                   	<form:radiobuttons element="li" path="sendSearchEntityIdString" onchange="showSelectedMsg(this, 'Cust', 'ini')" 
                   		items="${ltUser.customMsgs}" itemValue="messageId" itemLabel="messageText"/>  
                   </c:if>
                   </ul>
                 </c:if>
                 </div>         
                <!-- // tab 03 -->
              </div>
              <!-- // tabs ////////////////////////////////////////////////////////////////  -->
            <!-- // left column -->

          <!-- right column -->
            <div class="btn_message_box">
                <div class="box_curr_message">
                  <label>Current message:</label>
			<form:textarea path="category.initialMessage" rows="4" readonly="true"/>				                						
                </div>            
                <div class="chk_wrapper clearfix">
                	<form:checkbox path="category.includePhoneIni" class="chk_light mr5" />
                	<label for="include_phone">Include default phone number</label>
                	<input type="button" value="Save" onclick="saveMsg('ini')" class="btn_green btn_save">               
               </div>
                <div class="clearfix"></div>    
            </div>
            <!-- // right column -->
          </div>
          <!-- // two columns -->
        </div>
        <!-- // box -->
        
      	<!-- box -->
        <div class="box box_red_title box_opt_in_repeat">
        	<!-- title -->
        	<div class="box_title mb9">
          	<h2>Repeat Opt-In</h2>
          </div>
          <!-- // title -->
          <form:hidden path="sendSearchStateString"/>

          <!-- two columns -->
          <div class="two_cols_wrapper_01 clearfix">
          	<!-- left column -->
            	<h3 class="h3_sub ico_select">Select a Message:</h3>
              <!-- tabs ///////////////////////////////////////////////////////////////////  -->
              <div class="tabs_01 tabs_height_02" id="tabs_01">
                <ul class="ul_tabs_select">
                  <li class="tab_01"><a href="#tabs_02_1" class="selected">Corporate</a></li>
                  <li class="tab_02"><a href="#tabs_02_2">Spanish</a></li>
                  <li class="tab_02"><a href="#tabs_02_3">Custom</a></li>                  
                </ul>
                <!-- tab 01 -->
                <div class="tabs_02_content" id="tabs_02_1">
                  <ul class="ul_scroll_list scroll_list_002">
                  <c:if test="${fn:length(ltUser.approvedMsgs) > 0}">                  
                   	<form:radiobuttons element="li" path="sendSearchStateString" onchange="showSelectedMsg(this, 'Corp', 'rpt')" 
                   		items="${ltUser.approvedMsgs}" itemValue="messageId" itemLabel="messageText"/>  
                   </c:if>
                   </ul>
                 </div>                    
                 <!-- // tab 01 -->
                <!-- tab 02 -->
                <div class="tabs_02_content" id="tabs_02_2">
                  <ul class="ul_scroll_list scroll_list_002">
                  <c:if test="${fn:length(ltUser.approvedMsgsSP) > 0}">                
                   	<form:radiobuttons element="li" path="sendSearchStateString" onchange="showSelectedMsg(this, 'Corp', 'rpt')" 
                   		items="${ltUser.approvedMsgsSP}" itemValue="messageId" itemLabel="messageText"/>  
                   </c:if>
                   </ul>
                 </div>                    
                 <!-- // tab 02 -->                 
                <!-- tab 03 -->
                <div class="tabs_02_content" id="tabs_02_3">
                 <c:if test = "${fn:length(ltUser.customMsgs) > 0}" >                              
                  <ul class="ul_scroll_list scroll_list_002">  
                   	<form:radiobuttons element="li" path="sendSearchDMAString" onchange="showSelectedMsg(this, 'Cust', 'rpt')" 
                   		items="${ltUser.customMsgs}" itemValue="messageId" itemLabel="messageText"/>  
                   </ul>
                 </c:if>
                 </div>  
                <!-- // tab 03 -->
              </div>
              <!-- // tabs ////////////////////////////////////////////////////////////////  -->
            <!-- // left column -->
            
          	<!-- right column -->
            <div class="btn_message_box">
                <div class="box_curr_message">
                  <label>Current message:</label>
			<form:textarea path="category.autoResponse" rows="4" readonly="true"/>				                						
                </div>            
                <div class="chk_wrapper clearfix">
                	<form:checkbox path="category.includePhoneRpt" class="chk_light mr5" />
                	<label for="include_phone">Include default phone number</label>
                	<input type="button" value="Save" onclick="saveMsg('rpt')" class="btn_green btn_save">               
               </div>
                <div class="clearfix"></div>               
            </div>
            <!-- // right column -->
          </div>
          <!-- // two columns -->
        </div>
        <!-- // box -->
      </div>
    </div>
    <!-- // content area -->
    <!-- sidebar -->
    
    <div class="sidebar" id="id_sidebar">
    	<div class="inner">
      	<!-- title -->
        <div class="sb_title sb_title_ico ico_sb_mobile">
        	<h2 class="Mobile Marketing">Mobile Marketing</h2>
        </div>
        <!-- // title -->
        <!-- sidebar box -->
        <div class="sb_box">
          <!-- information wrapper -->
          <div class="information_wrapper">
          	<div class="info_title">
            	<a href="#" class="prevnext info_prev" id="id_prev_info"></a>
              <a href="#" class="prevnext info_next get_next_info"></a>
              <h3>Key Points For This Page</h3>
            </div>
            <!-- slider -->
            <div class="infoslider" id="infoslider">
            	<!-- slide -->
              <div class="slide">
<ul>
<li>1. <b>Select Your Office</b> you want to create your message for.</li><br/>
<li>2. <b>First Time Opt-In</b> - Once you choose a message, any time a customer or potential 
customer texts your KEYWORD to US411 (87411) they will get this message.  Remember to 
pick a good offer so that it is worth somebody opting in.</li><br/>
<li>3. <b>Repeat Opt-In</b> - If a customer text your KEYWORD to US411 (87411) again, the system 
will recognize their phone number and send the appropriate message back to them.  We have 
a "Welcome back to Liberty Tax" message already in there as a default, so there is 
no need to select a message for this box unless you really want to.</li>
</ul>
              </div>
              <!-- // slide -->


            </div>
            <!-- // slider -->
          </div>
          <!-- // information wrapper -->
          <!-- biz info wrapper -->
          <div class="biz_info_wrapper">
            <!-- title -->
            <div class="sb_title sb_title_ico ico_sb_biz">
              <h2 class="Mobile Marketing">Business Information</h2>
            </div>
            <!-- // title -->
            <div class="biz_info_grid_wrapper">
              <table class="grid bizinfo_grid" width="100%">
              <colgroup>
              <col width="79%" />
              <col width="21%" />
              </colgroup>
              <tbody>
                <tr>
                  <td class="td_01">
                  	<div>
				<c:out value="${ltUser.category.businessName}"/>
				<br/>
				<c:choose>
				<c:when test = '${ltUser.user.roleActions[0].roleType == "Office"}'>	
					<c:out value="http://libertytax.com/${ltUser.sites[0].customField2}"/>									
				</c:when>
				<c:otherwise>		
					<c:out value="http://libertytax.com"/>														
				</c:otherwise>
				</c:choose>									
				<br/>
				<c:out value="${ltUser.category.address}"/>
				<br/>
				<c:out value="${ltUser.category.city}"/>, 
				<c:out value="${ltUser.category.state}"/> 
				&nbsp;<c:out value="${ltUser.category.zip}"/>
				<br/>
				<c:if test="${fn:length(ltUser.category.phone) > 0}">
					<c:set var="phone" value="${ltUser.category.phone}"/>
					<c:out value="(${fn:substring(phone, 0, 3)}) ${fn:substring(phone, 3, 6)}-${fn:substring(phone, 6, fn:length(phone))}"/>				
				</c:if>				
		    </div>
		  </td>
		  <td class="td_02"><a href="#" onclick="getProfile(${ltUser.category.userId})">Expand</a></td>		
		  
		  <td class="td_02"><a href="#" onclick="saveProfile()" title="edit" class="lnk_edit">Edit</a></td>
		</tr>
	      </tbody>
	      </table>
	    </div>
	  </div>				
          <!-- // biz info wrapper -->
          <div class="hotspot_wrapper">
            <!-- title -->
            <div class="sb_title sb_title_ico ico_sb_hotspot">
              <h2 class="Mobile Marketing">Get your hotspot</h2>
            </div>
            <!-- // title -->
            <div class="hotspot">
            	<!-- box -->
          		<div class="hotspot_box">
              	<p class="p_left">Customize <br>your <br>Hotspot</p>
                <p class="p_right">Select <br>your <br>design</p>
                <c:if test='${loc == "CA"}'>                
                	<div class="hotspot_seal"><img src="images/seal_hotspot_ca.png" width="168" height="168"></div>               
                </c:if>
                <c:if test='${loc == "US"}'>
                	<div class="hotspot_seal"><img src="images/seal_hotspot_001.png" width="168" height="168"></div>
              	</c:if>                
              </div>
              <!-- // box -->
              <ul class="ul_hotspot">
              	<li><img src="images/hotspot_thumb_001.png" width="44" height="44"></li>
                <li><img src="images/hotspot_thumb_002.png" width="44" height="44"></li>
              	<li><img src="images/hotspot_thumb_003.png" width="44" height="44"></li>
                <li><img src="images/hotspot_thumb_004.png" width="44" height="44"></li>
              	<li><img src="images/hotspot_thumb_005.png" width="44" height="44"></li>
                <li><img src="images/hotspot_thumb_006.png" width="44" height="44"></li>
              	<li><img src="images/hotspot_thumb_007.png" width="44" height="44"></li>
                <li><img src="images/hotspot_thumb_008.png" width="44" height="44"></li>
              </ul>
              <div class="btn_hotspot_wrapper">
                <c:choose>
              	<c:when test = '${ltUser.user.roleActions[0].roleType == "Corporate"}'>
              		<h3>Please go to the Send Message page to get your hotspot</h3>
              	</c:when>
              	<c:otherwise>
              		<h3>Please go to the Dashboard page to get your hotspot</h3>              	
              	</c:otherwise>
              	</c:choose>              	
              </div>
            </div>
          </div>
        </div>
        <!-- // sidebar box -->
      </div>
      
    </div>
    <!-- // sidebar -->    
    <div class="floatfix"></div>
    		
    	</form:form>
    
  <!-- // content wrapper -->
</div>
	
<!-- // page wrapper -->
   