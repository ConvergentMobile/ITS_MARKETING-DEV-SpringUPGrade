<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<script type="text/JavaScript">	
	function resetForm() {
		$('#thisForm')[0].reset();			
		$('input[type="radio"]').prop('checked', false);
		$('input:checkbox').removeAttr('checked');
				
	}
	
	 $(document).ready(function() {
		resetForm();
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
	
	function getProfile() {
		var userId = $( "#searchOfficeIdString option:selected" ).val();	
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
		$('#currentPage').val(msgType);			
	        $.ajax({
	            type : 'POST',
	            url : 'saveConfirmationMessage',
            	    data: $("#thisForm").serialize(),
	            success : function(result) {
            		alert(result);
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
	      	<li class="si_confirmation" selected><a href="confirmationMessage">Confirmation Message</a></li>		
		<li class="si_send_msg"><a href="sendMessage">Send Message</a></li>			
	</c:if>
	<c:if test = '${ltUser.user.roleActions[0].roleType == "Office"}'>
    		<li class="si_dashboard"><a href="dashboardOffice">Dashboard</a></li>	
		<li class="si_custom_msg"><a href="customMessage">Create Custom Message</a></li>
	      	<li class="si_confirmation" selected><a href="confirmationMessage">Confirmation Message</a></li>	
	</c:if>   
 	<c:if test = '${ltUser.user.roleActions[0].roleType == "Corporate"}'>
    		<li class="si_dashboard"><a href="dashboardCorp">Dashboard</a></li>	
      		<li class="si_custom_msg_approve"><a href="customMessageCorp">Approve Custom Messages</a></li>   
		<li class="si_send_msg"><a href="sendMessage">Send Message</a></li>			
	</c:if> 
	<li class="si_reports"><a href="getReports">Reports</a></li>
      	<li class="si_mobile_profile"><a href="getProfile">My Mobile Profile</a></li>
      	<li class="si_toolbox"><a href="toolbox-office.html">Convergent Toolbox</a></li>
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
          <!-- two columns -->
          <div class="two_cols_wrapper_01 clearfix">
          	<!-- left column -->
            <div class="left">
            	<h3 class="h3_sub ico_select">Select a Message:</h3>
              <!-- tabs ///////////////////////////////////////////////////////////////////  -->
              <div class="tabs_01 tabs_height_02" id="tabs_01">
                <ul class="ul_tabs_select">
                  <li class="tab_01"><a href="#tabs_01_1" class="selected">Corporate</a></li>
                  <li class="tab_02"><a href="#tabs_01_2">Custom</a></li>
                </ul>
                <!-- tab 01 -->
                <div class="tabs_02_content" id="tabs_01_1">
                  <ul class="ul_scroll_list scroll_list_002">                
                  	<form:radiobuttons element="li" path="sendSearchCityString" onchange="showSelectedMsg(this, 'Corp', 'ini')"
                  		items="${ltUser.approvedMsgs}" itemValue="messageId" itemLabel="messageText"/>  
                  </ul>
                </div>                  
                 <!-- // tab 01 -->
                 <!-- tab 02 -->
                 <div class="tabs_01_content" id="tabs_01_2">
                 <c:if test = "${fn:length(ltUser.customMsgs) > 0}" >
                   <ul class="ul_scroll_list scroll_list_001">          
                   	<form:radiobuttons element="li" path="sendSearchEntityIdString" onchange="showSelectedMsg(this, 'Cust', 'ini')" 
                   		items="${ltUser.customMsgs}" itemValue="messageId" itemLabel="messageText"/>  
                   </ul>
                 </c:if>
                 </div>         
                <!-- // tab 02 -->
              </div>
              <!-- // tabs ////////////////////////////////////////////////////////////////  -->
            </div>
            <!-- // left column -->

          <!-- right column -->
            <div class="right">
            	<div class="msg_content_box mcb_height_02">
              	<h4 class="mb30">Message to Send:</h4>
                <div class="msg_text_scroll mts_02">            
			<form:textarea path="category.initialMessage" rows="4" class="ta_text_msg" readonly="true"/>				                			
                </div>
                <div class="chk_wrapper mt35 clearfix">
                	<form:checkbox path="category.includePhoneIni" class="chk_light mr5" />                	
                	<label for="include_phone">Include default phone number</label>
                </div>			
                <center><input type="button" value="Save" onclick="saveMsg('ini')" class="btn_green btn_save"></center>
              </div>
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
          <!-- two columns -->
          <div class="two_cols_wrapper_01 clearfix">
          	<!-- left column -->
            <div class="left">
            	<h3 class="h3_sub ico_select">Select a Message:</h3>
              <!-- tabs ///////////////////////////////////////////////////////////////////  -->
              <div class="tabs_01 tabs_height_02" id="tabs_02">
                <ul class="ul_tabs_select">
                  <li class="tab_01"><a href="#tabs_02_1" class="selected">Corporate</a></li>
                  <li class="tab_02"><a href="#tabs_02_2">Custom</a></li>
                </ul>
                <!-- tab 01 -->
                <div class="tabs_02_content" id="tabs_02_1">
                  <ul class="ul_scroll_list scroll_list_002">
                   	<form:radiobuttons element="li" path="sendSearchStateString" onchange="showSelectedMsg(this, 'Corp', 'rpt')" 
                   		items="${ltUser.approvedMsgs}" itemValue="messageId" itemLabel="messageText"/>  
                   </ul>
                 </div>                    
                 <!-- // tab 01 -->
                <!-- tab 02 -->
                <div class="tabs_02_content" id="tabs_02_2">
                 <c:if test = "${fn:length(ltUser.customMsgs) > 0}" >                              
                  <ul class="ul_scroll_list scroll_list_002">  
                   	<form:radiobuttons element="li" path="sendSearchDMAString" onchange="showSelectedMsg(this, 'Cust', 'rpt')" 
                   		items="${ltUser.customMsgs}" itemValue="messageId" itemLabel="messageText"/>  
                   </ul>
                 </c:if>
                 </div>  
                <!-- // tab 02 -->
              </div>
              <!-- // tabs ////////////////////////////////////////////////////////////////  -->
            </div>
            <!-- // left column -->
            
          	<!-- right column -->
            <div class="right">
            	<div class="msg_content_box mcb_height_02">
              	<h4 class="mb30">Message to Send:</h4>
                <div class="msg_text_scroll mts_02">            
			<form:textarea path="category.autoResponse" rows="4" class="ta_text_msg" readonly="true"/>				                						
                </div>                
                <div class="chk_wrapper mt35 clearfix">
                	<form:checkbox path="category.includePhoneRpt" class="chk_light mr5" />
                	<label for="include_phone">Include default phone number</label>
                </div>
                <center><input type="button" value="Save" onclick="saveMsg('rpt')" class="btn_green btn_save"></center>
              </div>
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
              <h3>Information</h3>
            </div>
            <!-- slider -->
            <div class="infoslider" id="infoslider">
            	<!-- slide -->
              <div class="slide">
              	<p>
                	Now, you can instantly reach your customers with the latest deals, promos, discounts, and other general information about your business 
                  using the power of text messaging&hellip; <b>any time of the day</b>!
                </p>
                <p class="p_small">
                	Don't forget&hellip; it is important to <span class="sp_red">PROMOTE, PROMOTE, PROMOTE</span> your call to action. You can send the best offers to your subscribers, 
                  but customers will only know you are there if you promote.
                </p>
              </div>
              <!-- // slide -->
            	<!-- slide -->
              <div class="slide">
              	<p>
                	1 Now, you can instantly reach your customers with the latest deals, promos, discounts, and other general information about your business 
                  using the power of text messaging&hellip; <b>any time of the day</b>!
                </p>
              </div>
              <!-- // slide -->

            </div>
            <!-- // slider -->
            <div class="infonext_wrapper"><a href="#" class="lnk_infonext get_next_info">Next</a></div>
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
				<c:out value="${ltUser.category.website}"/>						
				<br/>
				<c:out value="${ltUser.category.address}"/>
				<br/>
				<c:out value="${ltUser.category.city}"/>, 
				<c:out value="${ltUser.category.state}"/> 
				&nbsp;<c:out value="${ltUser.category.zip}"/>
				<br/>
				<c:out value="${ltUser.category.phone}"/>
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
                <div class="hotspot_seal"><img src="images/seal_hotspot_001.png" width="168" height="168"></div>
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
              	<a href="javascript:createHotspot('${keyword}');" class="btn_dark_blue btn_hotspot">Get My Hotspot!</a>
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
   