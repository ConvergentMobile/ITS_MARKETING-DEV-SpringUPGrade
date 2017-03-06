<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<script type="text/JavaScript">	
	$(document).ready(function() {
		//resetForm($('#thisForm'));
		if (${ltUser.searchOfficeIdString != ''}) {
			setMessageOff(${ltUser.searchOfficeIdString});
		}
	});

	function resetForm(form) {
	    form.find('input:text, input:password, input:file, select, textarea').val('');
	    form.find('input:radio, input:checkbox')
		 .removeAttr('checked').removeAttr('selected');

	
	    $('#lists li').remove();
	}

	function getProfile1(userId) {
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

	function createHotspot() {
		if ($('[name="officeIds"]:checked').length <= 0) {
			alert("Please select an Office");
			return;
		}
		
		if ($('[name="officeIds"]:checked').length > 1) {
			alert('Please select only one office');
			return;
		}	
		
		var kw = $('[name="officeIds"]:checked').val();
		window.open('createHotspotKW?kw=' + kw,"mywindow","scrollbars=yes,menubar=1,resizable=1,width=450,height=550");		
	}
	
	function showSelectedMsg(elem, msgType) {
		elemId = $(elem).attr("id");	
		var msgText = $("label[for='"+elemId+"']").text();					
		$('#sendSearchCityString').val(msgText);
	}
	
	function getProfile(userId) {
		var outstr = '';
		var outstr2 = '';
		$.ajax({
			type : 'GET',
			contentType : "application/html",
			url : 'getProfileInfo',
			data : 'userId=' + userId,
			success : function(category) {
				if (!category) {
					//alert("No Business Information found");
					$('#biz_info').html("No Business Information found");
					$('#biz_info_2').html(outstr2);						
					return;
				}
				outstr += category.businessName + '<br/>';
				//if (category.website != null) {
					//outstr += category.website + '<br/>';
				//}
				outStr += "http://libertytax.com";				
				outstr += category.address + '<br/>';
				outstr += category.city + ',' + category.state + '&nbsp;'
						+ category.zip + '<br/>';
				outstr += category.phone;
				outstr += '<c:set var="userId" value="' + userId + '"/>';
				outstr2 = '<td class="td_02"><a href="#" onclick="getProfile1('
						+ userId + ')">Expand</a></td>';
				//alert(outstr);
				$('#biz_info').html(outstr);
				$('#biz_info_2').html(outstr2);

			},
			error : function(e) {
				alert('error: ' + e.text());
			}
		});
	}
	
	function setMessage(elem) {
		var offId = $(elem).val();
		setMessageOff(offId);		
	}
	
	function setMessageOff(offId) {
		if (offId == 'All') {
			return;
		}
		
		<c:forEach var="site" items="${ltUser.sites}" varStatus="loopStatus">
			if ('${site.customField2}' == offId) {
				if ('${site.customField4}' == '1') {
					//attr does not seem to work
					$("input[name='includePhone']").prop('checked', true);
				} else {
					$("input[name='includePhone']").prop('checked', false);
				}
				
     				//$('input[name="listIds"][value="' + 2 + '"]').attr('checked', true);	
     				$("input[name='listIds']").val(['${site.customField3}']);
     			}
		</c:forEach>		
	}	
		
	function saveIt() {	

		
	        $.ajax({
	            type : 'POST',
	            url : 'sendAFriend',
            	    data: $("#thisForm").serialize(),
	            success : function(result) {
			popup(result, 1);					
            		//resetForm($('#thisForm'));	 
		    },
		    error : function(e) {
				alert('error: ' + e);					
		    }                        
	        });		
	}
	
	function checkAll() {	
		var cblist = $("input[name='officeIds']");	

		cblist.prop("checked", ! cblist.prop("checked"));
	}
		
</script>

<style>
           .chkbox {
                padding-left: 50px;
                width: 15%;
            }
.ui-dialog { z-index: 20000001 !important ;}
       
          
</style>

  <!-- // header -->
  <!-- content wrapper -->
  <div class="content_wrapper" id="content_wrapper">
   <form:form id="thisForm" method="post" action="" commandName="ltUser">

		<div id="dialog1" style="display:none">
		</div>
	
  	<!-- left side navigation -->
  	<ul class="ul_left_nav">
			<c:if test = '${ltUser.user.roleActions[0].roleType == "Entity"}'>  	
				<li class="si_dashboard"><a href="dashboardEntity">Dashboard</a></li>
				<li class="si_custom_msg"><a href="customMessageEntity">Create Custom Message</a></li>
				<li class="si_confirmation"><a href="confirmationMessage">Confirmation Message</a></li>		
				<li class="si_send_msg"><a href="sendMessage">Send Message</a></li>
				<li class="si_sendafriend selected"><a href="sendAFriend">Send a Friend</a></li>				
				<li class="si_reports"><a href="getReports">Reports</a></li>     		
				<li class="si_mobile_profile"><a href="getProfile">My Mobile Profile</a></li>	
			</c:if>
			<c:if test = '${ltUser.user.roleActions[0].roleType == "Office"}'>
				<li class="si_dashboard"><a href="dashboardOffice">Dashboard</a></li>	
				<li class="si_custom_msg"><a href="customMessage">Create Custom Message</a></li>
				<li class="si_confirmation"><a href="confirmationMessage">Confirmation Message</a></li>	
				<li class="si_reports"><a href="getReports">Reports</a></li>	      	
				<li class="si_mobile_profile selected"><a href="getProfile">My Mobile Profile</a></li>	
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
				<li class="si_custom_msg"><a href="customMessageEntity">Create Custom Message</a></li>
				<li class="si_reports"><a href="getReports">Reports</a></li>		
			</c:if>
				    	
			<li class="si_toolbox"><a href="cmtoolbox">Convergent Toolbox</a></li>
		</ul>			
    <!-- // left side navigation -->
    <!-- content area -->
    <div class="content" id="id_content_09"> 
    	<div class="nav_pointer pos_01"></div>
      <!-- subheader -->
      <div class="subheader clearfix">
      	<h1>SEND A FRIEND</h1>
      	<c:choose>
      		<c:when test = '${ltUser.user.roleActions[0].roleType == "Corporate"}'>
      			<p>Corporate</p>
      		</c:when>
      		<c:when test = '${ltUser.user.roleActions[0].roleType != "Corporate"}'>    		
        		<p>Entity Id: <c:out value="${sites[0].customField1}"/></p>
        	</c:when>
		</c:choose>        	
      </div>
      <!-- // subheader -->
    	<div class="inner_box">
      	<!-- box -->
        <div class="box box_red_title box_send_friend">        
        	<!-- title -->
        	<div class="box_title">
          	<h2>Send a Friend</h2>
          
            	<!-- <div class="campaign clearfix"></div> -->
	        </div>
	        	           
	 <table class="send-friend grid grid_01" width="100%">
	 	<colgroup>
	 		<col width="10%">
	 		<col width="70%">
	 		<col width="20%">
	 	</colgroup>
	    <tr>
	 	<th>Office</th>
	 	<th>Message</th>
	 	<th>Include Phone</th>
	    </tr>
	 </table>
	 <div class="list-send-friend">
	 <table class="send-friend grid grid_01" width="100%">
		<colgroup>
	 		<col width="10%">
	 		<col width="70%">
	 		<col width="20%">
	 	</colgroup>
	    <c:forEach var="site" items="${ltUser.sites}" varStatus="loopStatus">
	     <form:hidden path="sites[${loopStatus.index}].customField1"/>
	     <form:hidden path="sites[${loopStatus.index}].keyword"/>	    
	     <form:hidden path="sites[${loopStatus.index}].userId"/>	    

	     <tr>
	    	<td><form:hidden path="sites[${loopStatus.index}].customField2" value="${site.customField2}"/>${site.customField2}</td>
	    	<td>
		   <form:select path="sites[${loopStatus.index}].customField3">
		      <form:option value="">Select a Message</form:option>
		      <form:options items="${ltUser.customMsgs}" itemValue="messageText" itemLabel="messageText" />
		   </form:select>	    	
	    	</td>
		<td>
                   <form:select path="sites[${loopStatus.index}].customField4" class="w60">
                   	<form:option value="Yes">Yes</form:option>
                   	<form:option value="No">No</form:option>
                   </form:select>
		</td>
	    </tr>
	    </c:forEach>
	 </table>
	 </div>
	        	           
          <!-- // title -->
 
	 		<div class="btn_message_box" style="background: none;">             
 				<a href="#" onclick="saveIt()" class="btn_save_lnk_003 lnk_centered"> Save </a>
        	</div>
	</div>	
        </div>
      </div>
     <!-- // content area -->
    <!-- sidebar -->
    <div class="sidebar" id="id_sidebar_09">
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

              <h3>Key Points For This Page</h3>
            </div>
            <!-- slider -->
            <div class="infoslider" id="infoslider">
            	<!-- slide -->
              <div class="slide">
              	<p>
              	<ul>
		<li><b>1. Entity Opt-In</b> - You must opt-in for this program.  All messages are sent 
		out 24 hours after a customer leaves your desk (provided they have opted in for 
		text marketing / text operations messages).</li><p/>
		<li><b>2. Select Your Offices</b> - Select one or all offices for this program</li>
		<li>&nbsp;</li>
		<li>&nbsp;</li>
                </p>
              </div>
              <!-- // slide -->
 	<!-- slide -->
              <div class="slide">
              	<p>
              	<ul>
		<li><b>3. Select Your SAF Message</b> - Select 1 of the 4 SAF messages. These are not 
		messages that you can create at this time.</li><p/>
		<li><b>4. Save</b> - Save the settings and you are all set up for SAF messages to go out 
		automatically</li>
		<li>&nbsp;</li>
		<li>&nbsp;</li>
                </p>
              </div>
              <!-- // slide -->

            </div>
            <!-- // slider -->
             <div class="infonext_wrapper"><a href="#" class="lnk_infoprev get_prev_info" id="id_prev_info">Prev</a>
            <a href="#" class="lnk_infonext get_next_info">Next</a></div>
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
                  	<div  id="biz_info">
                  		<c:set var="userId" value="${ltUser.category.userId}"/>
				<c:out value="${ltUser.category.businessName}"/>
				<br/>
				<c:choose>
				<c:when test = '${fn:length(ltUser.sites[0].customField2) > 0}'>	
					<c:set var="website" value="http://libertytax.com/${ltUser.sites[0].customField2}"/>									
				</c:when>
				<c:otherwise>		
					<c:set var="website" value="http://libertytax.com"/>														
				</c:otherwise>
				</c:choose>				
				<c:out value="http://libertytax.com"/>							
				<br/>
				<c:out value="${ltUser.category.address}"/>
				<br/>
				<c:out value="${ltUser.category.city}"/>, 
				<c:out value="${ltUser.category.state}"/> 
				&nbsp;<c:out value="${ltUser.category.zip}"/>
				<br/>
				<c:set var="phone" value="${ltUser.category.phone}"/>
				<c:out value="(${fn:substring(phone, 0, 3)}) ${fn:substring(phone, 3, 6)}-${fn:substring(phone, 6, fn:length(phone))}"/>					<form:hidden path="category.phone"/>
		    </div>
		  </td>
		  <td class="td_02" id="biz_info_2"></td>	
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
                <c:if test='${ltUser.user.billingCountry == "CA"}'>                
                	<div class="hotspot_seal"><img src="images/seal_hotspot_ca.png" width="168" height="168"></div>               
                </c:if>
                <c:if test='${ltUser.user.billingCountry == "US"}'>
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
  </div>
  <!-- // content wrapper -->
</div>

		</form:form>
	
<!-- // page wrapper -->

