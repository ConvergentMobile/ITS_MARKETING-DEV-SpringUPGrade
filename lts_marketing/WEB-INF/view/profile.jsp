<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<script type="text/JavaScript">	
	function createHotspot(keyword){		
		window.open('createHotspot?keyword=' + keyword,"mywindow","scrollbars=yes,menubar=1,resizable=1,width=450,height=550");		
	}
	
	function getProfile() {
		var userId = $('#searchOfficeIdString :selected').val();
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
	
	function saveProfile() {
        $.ajax({
            type : 'POST',
            url : 'saveProfile',
            data: $("#thisForm").serialize(),
            success : function(result) {
        		$('#content').html(result);
        		popup("Timezone info saved", 0);        		
			},
			error : function(e) {
				alert('error: ' + e.text());
			}                        
        });
	}
	
</script>

<style>
	tr.redc { background-color: red; };
</style>

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
				<li class="si_sendafriend"><a href="sendAFriend">Send a Friend</a></li>
				<li class="si_reports"><a href="getReports">Reports</a></li>     		
				<li class="si_mobile_profile selected"><a href="getProfile">My Mobile Profile</a></li>	
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
				<li class="si_confirmation"><a href="confirmationMessage">Confirmation Message</a></li>		
				<li class="si_reports"><a href="getReports">Reports</a></li>		
				<li class="si_mobile_profile"><a href="getProfile">My Mobile Profile</a></li>
			</c:if>	
      	
      		<li class="si_toolbox"><a href="cmtoolbox">Convergent Toolbox</a></li>
    </ul>
    <!-- // left side navigation -->
    <!-- content area -->
    <div class="content" id="id_content">
    	<div class="nav_pointer pos_01"></div>
      <!-- subheader -->
      <div class="subheader clearfix">
      	<h1>Mobile Profile</h1>
          <div>
        <p>Office Id:          
                <form:select path="searchOfficeIdString" class="select_send_notification" onchange="getProfile()" >
                	<form:option value="">Select an Office</form:option>
                	<form:options items="${ltUser.sites}" itemValue="userId" itemLabel="customField2"/>            	
            	</form:select>
        </p>
          
          </div>        
      </div>

      <!-- // subheader -->
    	<div class="inner_box">
      	<!-- box -->
        <div class="box box_blue_title box_profile">
        	<!-- title -->
        	<div class="box_title">        	
				<h2>Your Mobile Profile</h2>
			</div>
           <!-- // title -->
          <!-- box profile -->
          <div class="profile_content">
          	<table class="grid grid_profile">
            <colgroup>
            	<col width="" />
              <col width="" />
            </colgroup>
            <tbody>                   		
					<tr>	
						<td class="td_01">
							<form:label path="category.businessName"><spring:message code="label.businessName" /></form:label>
						</td>						
						<td class="td_02"><c:out value="${ltUser.category.businessName}"/></td>
					</tr>
				<%--
					<tr>
						<td class="td_01">
							<form:label path="category.adminMobilePhone"><spring:message code="label.adminMobilePhone" /></form:label>
						</td>
						<td class="td_02"><c:out value="${ltUser.category.adminMobilePhone}"/></td>
					</tr>
				--%>							
					<tr>
						<td class="td_01">
							<form:label path="category.address"><spring:message code="label.address" /></form:label>
						</td>
						<td class="td_02"><c:out value="${ltUser.category.address}" /></td>
					</tr>
					<tr>
						<td class="td_01"><form:label path="category.city"><spring:message code="label.city" /></form:label></td>
						<td class="td_02"><c:out value="${ltUser.category.city}" /></td>
					</tr>
					<tr>
						<td class="td_01"><form:label path="category.state"><spring:message code="label.state" /></form:label></td>
						<td class="td_02"><c:out value="${ltUser.category.state}"/></td>
					</tr>
					<tr>
						<td class="td_01"><form:label path="category.zip"><spring:message code="label.zip" /></form:label></td>
						<td class="td_02"><c:out value="${ltUser.category.zip}" /></td>
					</tr>
					<tr>
						<c:choose>
						<c:when test = '${ltUser.user.roleActions[0].roleType == "AD"}'>	
							<c:set var="phone" value="${ltUser.category.adminMobilePhone}"/>									
						</c:when>
						<c:otherwise>		
							<c:set var="phone" value="${ltUser.category.phone}"/>														
						</c:otherwise>
						</c:choose>						
						<td class="td_01"><form:label path="category.phone"><spring:message code="label.phone" /></form:label></td>
						<td class="td_02">
					<%--
						<c:if test = '${ltUser.user.roleActions[0].roleType == "Entity"}'>  
							<c:set var="phone" value="${ltUser.category.adminMobilePhone}"/>
						</c:if>	
					--%>					
						<c:if test="${fn:length(phone) > 0 }">
							<c:out value="(${fn:substring(phone, 0, 3)}) ${fn:substring(phone, 3, 6)}-${fn:substring(phone, 6, fn:length(phone))}"/>	
					    </c:if>
					    </td>					
					</tr>
					<tr>
						<td class="td_01"><form:label path="category.email"><spring:message code="label.email" /></form:label></td>
						<td  class="td_02"><c:out value="${ltUser.category.email}" /></td>
					</tr>							
	                <tr>
	                  <td class="td_01">
	                  	<form:label path="category.timezone"><spring:message code="label.timezone"/></form:label>
					  </td>
	                  <td class="td_02">
	                  	<form:select path="category.timezone" class="select_send_notification">
	                  		<form:option value="US/Central">Central</form:option>
	                  		<form:option value="US/Eastern">Eastern</form:option>
	                  		<form:option value="US/Mountain">Mountain</form:option>
	                  		<form:option value="US/Pacific">Pacific</form:option>
	                  	</form:select>
	                  </td>
	                </tr> 
				<%--	            									
					<tr>
						<td class="td_01"><form:label path="category.facebookLink"><spring:message code="label.facebookLink" /></form:label></td>
						<td class="td_02"><c:out value="${ltUser.category.facebookLink}" /></td>
					</tr>
					<tr>
						<td class="td_01"><form:label path="category.twitterLink"><spring:message code="label.twitterLink" /></form:label></td>
						<td class="td_02"><c:out value="${ltUser.category.twitterLink}" /></td>
					</tr>
					<tr>
						<td class="td_01"><form:label path="category.areaServed"><spring:message code="label.areaServed" /></form:label></td>
						<td class="td_02"><c:out value="${ltUser.category.areaServed}" /></td>
					</tr>					
					<tr>
						<td class="td_01"><form:label path="category.busHours"><spring:message code="label.hours" /></form:label>
						</td>
						<td class="td_02"><form:textarea path="category.busHours" style="border: 0px solid #000000;" rows="2" readonly="true"/></td>
					</tr>
					<tr>
						<td class="td_01"><form:label path="category.description"><spring:message code="label.description" /></form:label></td>
						<td class="td_02"><form:textarea styleClass="text" path="category.description" style="border: 0px solid #000000;" rows="2" readonly="true"/></td>
					</tr>
				--%>					
		              <tr>
		              	<td class="td_01"></td>
		                <td class="td_02">
		                	<div class="pt7">
		                  	
		                    <a href="#" onclick="saveProfile()" class="btn_green btn_save_lnk left mr7">Save</a>
		                    <!-- <a href="#" class="btn_red btn_cancel_lnk">Cancel</a> -->
		                    <!-- <input type="submit" value="Save" class="btn_green btn_save left mr10"> -->
		                  	<!-- <input type="submit" value="Cancel" class="btn_red_lnk btn_cancel_lnk left"> -->
		                    <div class="floatfix"></div>
		                  </div>
		                </td>
		              </tr>            
            </tbody>
            </table>
          </div>
          <!-- // box profile -->
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
              	<ul>
<li><b>1. Select Your Office</b> you want to claim the correct Timezone.</li><br/>
<li>2. Please make sure your office info is correct. This info comes directly from 
the Liberty Admin system.</li>
		</ul>
                </p>
              </div>

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
                <c:set var="website" value="http://libertytax.com/${ltUser.searchDMAString}"/>                                            
                <c:out value="${website}"/>									
				<br/>
				<c:out value="${ltUser.category.address}"/>
				<br/>
				<c:out value="${ltUser.category.city}"/>, 
				<c:out value="${ltUser.category.state}"/> 
				&nbsp;<c:out value="${ltUser.category.zip}"/>
				<br/>
				<c:choose>
				<c:when test = '${ltUser.user.roleActions[0].roleType == "AD"}'>	
					<c:set var="phone" value="${ltUser.category.adminMobilePhone}"/>									
				</c:when>
				<c:otherwise>		
					<c:set var="phone" value="${ltUser.category.phone}"/>														
				</c:otherwise>
				</c:choose>								
			<%--
				<c:if test = '${ltUser.user.roleActions[0].roleType == "Entity"}'>  
					<c:set var="phone" value="${ltUser.category.adminMobilePhone}"/>
				</c:if>			
			--%>		
				<c:if test="${fn:length(phone) > 0 }">				
					<c:out value="(${fn:substring(phone, 0, 3)}) ${fn:substring(phone, 3, 6)}-${fn:substring(phone, 6, fn:length(phone))}"/>	
				</c:if>
				</div>
		  </td>
		  <!-- 
		  <td class="td_02"><a href="#" onclick="getProfile()">Expand</a></td>		
		  -->		  
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
              <%--
              	<a href="javascript:createHotspot('${keyword}');" class="btn_dark_blue btn_hotspot">Get My Hotspot!</a>
              --%>
				<h3>Please go to the Dashboard page to get your hotspot</h3>                
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
		</form:form>

	
<!-- // page wrapper -->
