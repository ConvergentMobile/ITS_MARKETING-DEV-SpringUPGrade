<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<script type="text/JavaScript">	
	$(document).ready(function() {
		resetForm($('#thisForm'));		
	});

	function resetForm(form) {
	/*
	    var elems = form.find('input:checkbox').each(
	    function(index){  
		var input = $(this);
		alert('Type: ' + input.attr('type') + 'Name: ' + input.attr('name') + ' Value: ' + input.val());
	    }
    	);
	*/
	    form.find('input:text, input:password, input:file, select, textarea').val('');
	    form.find('input:radio, input:checkbox')
		 .removeAttr('checked').removeAttr('selected');

	
	    $('#lists li').remove();
	}

 	$(function() {
	    $( "#schedDate" ).datepicker({
		'format': 'm/d/yyyy',
		'autoclose': true,
		minDate: 0
	    });
	});
	
	$(function() {
	    $('#schedTime').timepicker({
		'showDuration': false,
		'timeFormat': 'h:i a',
		'minTime': new Date(0,0,0,8,0,0),
		'maxTime': new Date(0,0,0,20,0,0)
	    });
	});

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

	function uploadList(){		
		window.open('uploadFile',"mywindow","scrollbars=yes,menubar=1,resizable=1,width=450,height=550");		
	}
	
	function createHotspot(keyword){		
		window.open('createHotspot?keyword=' + keyword,"mywindow","scrollbars=yes,menubar=1,resizable=1,width=450,height=550");		
	}
	
	function showSelectedMsg(elem, msgType) {
		elemId = $(elem).attr("id");	
		var msgText = $("label[for='"+elemId+"']").text();					
		$('#sendSearchCityString').val(msgText);
	}

	function deleteNumber(listId, number) {
	        $.ajax({
	            type : 'POST',
	            url : 'deleteNumber',
            	    data: 'listId=' + listId + '&number=' + number,
	            success : function(result) {
            		alert(result);
				},
				error : function(e) {
					alert('error: ' + e);
				}                        
	        });	
	}
	
	function viewList(listId) {
		window.open('viewListData?listId=' + listId,"mywindow","scrollbars=yes,menubar=1,resizable=1,width=450,height=550");			
	}
	
	function viewList1(listId) {
		var outstr = '';
	        $.ajax({
	            type : 'GET',
	            url : 'viewListData',
	            contentType: "application/html",
	            data: 'listId='+ listId,
	            success : function(result) {
					newwin = window.open('',"mywindow","scrollbars=yes,menubar=1,resizable=1,width=450,height=550");		
	            	
	                if (result.length == 0) {
                        	outstr = "<span class='name'><font size='-0.5'>Empty list</font></span>";
                	}  else {
                        	$.each(result, function(index, value) {
					outstr += '<br/>' + value;
					outstr += ' <a href="javascript:deleteNumber(\'' + listId + '\', \'' + value + '\');"><img src="images/delete.png" title="Delete"/></a>';
            			});
           		}	
           		//$(newwin.document,body).html(outstr);
           		newwin.document.write(outstr);
	            },
				error : function(e) {
					alert('error: ' + e.toString());
				}                        
	        });		
	}

	function addCheckbox(name, val) {
		   var container = $('#lists');
		   var inputs = container.find('input');
		   var id = inputs.length+1;
		
		var astr = '<li><label for="listIds' + id + '">' + name + '</label>';
		astr += '<input id="listIds' + id + '" type="checkbox" class="chk_light" name="listIds" value="' + val + '"/></li>';
		astr += '&nbsp;<a href="#" onclick="viewList(\''+val+'\')">View</a>';
				
		astr2 = '&nbsp;<a href="#" class="lnk_chk_001" onclick="viewList(\''+val+'\')">' + name + '</a>';
		var astr1 = '<li>' + astr2;
		astr1 += '<input id="listIds' + id + '"type="checkbox" class="chk_light" name="listIds" value="' + val + '"/></li>';
		
		//alert(astr);
		
		container.append(astr1);
	}
	
	function addCheckbox1(name, val) {
		   var container = $('#lists');
		   var inputs = container.find('input');
		   var id = inputs.length+1;
		
	           container.append("<li>");
		   $('<label />', { 'for': 'listIds'+id, text: name }).appendTo(container);
		   $('<input />', { type: 'checkbox', id: 'listIds'+id, name: 'listIds', value: val}).appendTo(container);
		  container.append("</li>");
		 var astr = '&nbsp;<a href="#" onclick="viewList(\''+val+'\')">View</a>';
		   container.append(astr);
		   
		   //viewLink = $('<a class="" href="#">View</a>');
		   //viewLink.bind("click", function(){
		//	alert('val = ' + val);
		//	window.open('createHotspot?keyword=test',"mywindow","scrollbars=yes,menubar=1,resizable=1,width=450,height=550");					
		//      });
		//   container.append(viewLink);
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
				if (category.website != null) {
					outstr += category.website + '<br/>';
				}
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
	
	function getList(userId) {
		//alert('cnt: ' + $('[name="officeIds"]:checked').length);
		if ($('[name="officeIds"]:checked').length <= 0) {
			alert('Must select atleast one office');
			return;
		}
		
	        $.ajax({
	            type : 'GET',
	            url : 'getList',
	            contentType: "application/html",
	            data: $('#thisForm').serialize(),
	            success : function(result) {
	                if (result.length == 0) {
                        outstr = "<span class='name'><font size='-0.5'>No lists found</font></span>";
                        $("#lists").html(outstr);
                	}  else {
                	$('#lists').empty();
                        $.each(result, function(index, value) {
							addCheckbox(value.listName, value.listId);
            			});
           			}			
	            },
				error : function(e) {
					alert('error: ' + e.toString());
				}                        
	        });
	        
	        getProfile(userId);
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
		
	}
	
	function sendIt(isNow) {
		if ($('#sendSearchKeywordString').val() == "") {
			alert("Must specify a Campaign Name");
			return;		
		}
		
		if ($('#sendSearchCityString').val() == "") {
			alert("Must select a Message");
			return;		
		}
		
		//alert('cnt: ' + $('[name="listIds"]:checked').length);
		cnt = $('[name="listIds"]:checked').length;
		if (cnt == 0) {
			alert("Must select a list to send to");
			return;
		}		
		
		$('#nowSched').val(isNow);

		$('#sendNow').hide();
		$('#scheduleIt').hide();
		
	        $.ajax({
	            type : 'POST',
	            url : 'sendMessage',
            	    data: $("#thisForm").serialize(),
	            success : function(result) {
            		alert(result);
            		resetForm($('#thisForm'));
				$('#sendNow').show();
				$('#scheduleIt').show();	            		
			},
			error : function(e) {
				alert('error: ' + e);
				$('#sendNow').show();
				$('#scheduleIt').show();					
			}                        
	        });		
	}
	
	function getJobs() {
		//check only if it is not Corporate
		if ('${ltUser.user.roleActions[0].roleType}' != "Corporate") {
			if ($('[name="officeIds"]:checked').length <= 0) {
				alert("Must select atleast one office");
				return;
			}
		}
		
	        $.ajax({
	            type : 'GET',
	            url : 'getScheduledJobs',
            	    data: $("#thisForm").serialize(),
	            success : function(result) {
					var newwin = window.open('',"mywindow","scrollbars=yes,menubar=1,resizable=1,width=450,height=550");					
	            	newwin.document.write(result);
				},
				error : function(e) {
					alert('error: ' + e);
				}                        
	        });	
	}
	
	function checkAll() {	
		var cblist = $("input[name='listIds']");				
		cblist.prop("checked", ! cblist.prop("checked"));
	}
</script>

<style>
           .chkbox {
                padding-left: 50px;
                width: 15%;
            }
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
				<li class="si_send_msg selected"><a href="sendMessage">Send Message</a></li>
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
      	<h1>SEND MESSAGE</h1>
        <p>Entity Id: <c:out value="${sites[0].customField1}"/></p>
      </div>
      <!-- // subheader -->
    	<div class="inner_box">
      	<!-- box -->
        <div class="box box_red_title box_send_msg">
        	<!-- title -->
        	<div class="box_title">
          	<h2>Send Mesage</h2>
          </div>
          
        <div  class="grid grid_06">
			<label class="h3_sub" for="campaign">Campaign Name: </label>    
			<form:input path="sendSearchKeywordString"/>
			<a href="#" class="h3_sub" onclick="uploadList()">Upload List</a>			
	    </div>
	              
          <!-- // title -->
          <!-- horizontal checkboxes wrapper -->
          <div class="horizontal_checkboxes">
          	<h3 class="h3_sub ico_offices">Select one or more offices:</h3>
            <div class="hc_wrapper">
              <ul class="ul_hc_scrollbox" id="id_hc_scrollbox">
		<c:forEach var="item" items="${ltUser.sites}" varStatus="loopStatus">
		    <li>
		    	<label><c:out value="${item.customField2}"/></label>
		    	<form:checkbox path="officeIds" 
		    		onclick="getList('${item.userId}')" value="${item.customField2}"/>
		    </li>
		</c:forEach> 
              </ul>
              <div class="floatfix"></div> 
            </div>
          </div>              
          <!-- // horizontal checkboxes wrapper -->
          <!-- two columns -->
          <div class="two_cols_wrapper_01 clearfix">
          	<!-- left column -->
            <div class="left">
            	<h3 class="h3_sub ico_select">Select a Message to Send:</h3>
              <!-- tabs ///////////////////////////////////////////////////////////////////  -->
              <div class="tabs_01 tabs_height_01" id="tabs_01">
                <ul class="ul_tabs_select">
                  <li class="tab_01"><a href="#tabs_01_1" class="selected">Corporate</a></li>
                  <li class="tab_02"><a href="#tabs_01_2">Custom</a></li>
                </ul>
                <!-- tab 01 -->
                <div class="tabs_01_content" id="tabs_01_1">
                  <ul class="ul_scroll_list scroll_list_001">
                  	<form:radiobuttons element="li" path="sendSearchStateString" onchange="showSelectedMsg(this, 'Corp')" items="${ltUser.approvedMsgs}" itemValue="messageId" itemLabel="messageText"/>  
	          </ul>
                </div>
                      <!-- // tab 01 -->
                <!-- tab 02 -->
                <div class="tabs_01_content" id="tabs_01_2">
                  <c:if test = "${fn:length(ltUser.customMsgs) > 0}">
                  <ul class="ul_scroll_list scroll_list_001">          
                  	<form:radiobuttons element="li" path="sendSearchDMAString" onchange="showSelectedMsg(this, 'Cust')" items="${ltUser.customMsgs}" itemValue="messageId" itemLabel="messageText"/>  
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
            	<div class="msg_content_box mcb_height_01">
              	<h4>Message to Send:</h4>
		<form:textarea path="sendSearchCityString" rows="4" class="ta_text_msg" readonly="true"/>				                
                <div class="chk_wrapper clearfix">
                	<form:checkbox path="includePhone" id="includePhone" class="chk_light mr5"/>
                	<label for="includePhone">Include default phone number</label>
                </div>
	          	<form:hidden path="nowSched" />
                <center>
					<input type="button" id="sendNow" onclick="sendIt('Y')" value="Send Now" class="btn_send_now">                	                	               	
                </center>	        
              </div>
            </div>
            <!-- // right column -->
          </div>
          <!-- // two columns -->

          <!-- two columns -->
          <div class="two_cols_wrapper_01 clearfix">
          	<!-- left column -->
            <div class="left">
            	<h3 class="h3_sub ico_select">Select List of Mobile Numbers:</h3>
              <div class="grey_box_01 mobile_numbers_wrapper">
              	<div class="grey_box_title_01">
                	<input type="checkbox" onclick="checkAll()" class="chk_dark" name="all_numbers" id="all_numbers">
                  	<label for="all_numbers">Check All</label>                  	
                </div>    
		<ul class="ul_phone_numbers">
			<div id="lists">
			</div>
		</ul>  
                <div class="floatfix"></div>
              </div>             
            </div>               
             <!-- // left column -->
          	<!-- right column -->
            <div class="right">
	            <h3 class="h3_sub ico_schedule">Schedule for Later:</h3>
              <div class="grey_box_01">
              	<!-- date picker -->
                <div class="date_picker">
                	<div class="inner">         
				<label for="schedDate" class="lb_date">Date:</label>
				<form:input path="schedDate" class="input_date"/> 				
				<label for="schedTime" class="lb_time">Time:</label>
				<form:input path="schedTime" class="input_time"/>               
                  	</div>
                </div>
                <!-- // date picker -->
                <!-- repeat -->
                <div class="repeat">
                	<table class="grid grid_repeat" width="272">
                  <colgroup>
                  	<col width="96" />
                    <col width="53" />
                    <col width="7" />
                    <col width="116" />
                  </colgroup>
                  <tbody>
		<tr>
		<td class="td_01"><label for="repeat" class="lb_repeat">Repeat every</label></td>
	      <td class="td_02">                  	
                <form:select path="repeatDayCount" >
                    <form:option value="0" />0
                    <form:option value="1" />1
                    <form:option value="2" />2
                    <form:option value="3" />3
                    <form:option value="4" />4
                    <form:option value="5" />5
                 </form:select>
	      </td>
              <td class="empty_cell"></td>
	      <td class="td_03">                  	
                <form:select path="repeatPeriod" >
                    <form:option value="Days" />Days
                    <form:option value="Months" />Months
                 </form:select>
               </td>
	    </tr>
	    <tr>
		<td class="td_01"><label for="times" class="lb_for_times">For</label></td>
	      <td class="td_02">	
                <form:select path="numberOccurrencesDays" >
                    <form:option value="0" />0
                    <form:option value="1" />1
                    <form:option value="2" />2
                    <form:option value="3" />3
                    <form:option value="4" />4                 
                </form:select>
              </td>
	      <td class="empty_cell"></td>
	      <td class="td_03"><label for="times" class="lb_times">Times</label></td>              
	     </tr>         
	  </tbody>
        </table>
      </div>      
	<!-- // repeat -->
	<div class="btn_schedule_box">
	  <center>
	    <input type="button" id="scheduleIt" onclick="sendIt('N')" value="Schedule" class="btn_schedule"><br>
	    <a href="#" onclick="getJobs()" class="lnk_scheduled">See Scheduled</a>
	  </center>
	</div>
                
              </div>
            </div>
            <!-- // right column -->
          </div>
          <!-- // two columns -->
        </div>
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
                  	<div  id="biz_info">
                  		<c:set var="userId" value="${ltUser.category.userId}"/>
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
  </div>
  <!-- // content wrapper -->
</div>

		</form:form>
	
<!-- // page wrapper -->

