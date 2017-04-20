<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<script type="text/JavaScript">	
	$(document).ready(function() {
		resetForm($('#thisForm'));
		
		var settings = {
			    url: "uploadFileOneClick",
			    method: "POST",
			    allowedTypes:"xls, clsx, csv, txt",
			    fileName: "name",
			    multiple: false,
			    showStatusAfterSuccess: false,
			    dragDrop: false,
			    showProgress: true,
			    formData: {
			    	name: "accountingList",
			    	type: 'accounting',
			    },
			    onSuccess:function(files,data,xhr) {
				//$("#status").html("<span><font color='green'>Upload is success</font></span>");
				  if (data.indexOf("Error") >= 0) {
				  	popup(data, 0);
				  } else { //we get back listId || msg
					//location.reload();
					var fields = data.split('||');
					addCheckbox(files[0], fields[0]);	
					$('#sendSearchCityString').val(fields[1]);					
				  } 			    
			    },
			    afterUploadAll:function() {
					alert("all images uploaded!!");
			    },
			    onError: function(files,status,errMsg) {        
					$("#status").html("<span><font color='red'>Upload Failed</font></span>");
			    }
			}
			$("#fileuploader").uploadFile(settings);			
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

	function deleteMsg(msgId) {
		var ans = confirm("Are you sure you want to delete this message?");
		if (! ans) {
			return;
		}
		
	        $.ajax({
	            type : 'POST',
	            url : 'deleteCustomMessage',
            	    data: 'msgId=' + msgId,
	            success : function(result) {          
	            	popup(result, 1);
				},
				error : function(e) {
					alert('error: ' + e);
				}                        
	        });	
	}
	
	function viewList1(listId) {
		window.open('viewListData?listId=' + listId,"mywindow","scrollbars=yes,menubar=1,resizable=1,width=450,height=550");			
	}
	
	function viewList(listId) {
	        $.ajax({
	            type : 'GET',
	            url : 'viewListData',
	            data: 'listId='+ listId,
	            success : function(result) {
			$('#dialog1').dialog({
				title : '',
				height : 500,
				width : 1000,
			});
			$(".ui-dialog-titlebar").hide();
			$('#dialog1').html(result);				
	            },
			error : function(e) {
				alert('error: ' + e.toString());
			}                        
	        });		
	}

	function listMgmt() {
        $.ajax({
            type : 'GET',
            url : 'listMgmt',
            success : function(result) {
				$('#dialog1').dialog({
					title : '',
					height : 600,
					width : 675,
					
				});
				$(".ui-dialog-titlebar").hide();
				$('#dialog1').html(result);				
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
				
		astr2 = '&nbsp;<a href="#"  onclick="viewList(\''+val+'\')">' + name + '</a>';
		var astr1 = '<li>' + astr2;
		astr1 += '&nbsp;<input id="listIds' + id + '"type="checkbox" class="chk_light" name="listIds" value="' + val + '"/>';
		astr1 += '&nbsp;<a href="#" onclick="deleteList(\''+val+'\', \''+name+'\')"><img src="images/delete.png" title="Delete"/></a></li>';

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
			alert("Must specify a User Name");
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
            		//alert(result);
					//$('#errwin').html('<div align="center">' + result + '</div>');
					//$( "#errwin" ).dialog('open');   
					popup(result, 1);					
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
	

	function checkAll() {	
		var cblist = $("input[name='listIds']");				
		cblist.prop("checked", ! cblist.prop("checked"));
	}
	
	function deleteList(listId, listName) {	
		var ok = confirm("Are you sure you want to delete list '" + listName + "' ?"); 
		if (! ok)
			return;
		
	        $.ajax({
	            type : 'POST',
	            url : 'deleteList',
            	    data: 'listId=' + listId + '&listName=' + listName,
	            success : function(result) {     
	            	popup(result, 1);
				},
				error : function(e) {
					alert('error: ' + e);
				}                        
	        });	
	}	
</script>

<style>
   .chkbox {
	padding-left: 50px;
	width: 15%;
    }
	
    .ui-dialog { z-index: 20000001 !important ;}

	.lispan {
	float: left;
	}       
          
</style>

  <!-- // header -->
  <!-- content wrapper -->
  <div class="content_wrapper" id="content_wrapper">
   <form:form id="thisForm" method="post" action="dashboard" commandName="ltUser">
   	<form:hidden path="currentPage" value="accountingSend"/>

		<div id="dialog1" style="display:none">
		</div>
	
  	<!-- left side navigation -->
  	<ul class="ul_left_nav">
			<c:if test = '${ltUser.user.roleActions[0].roleType == "Entity"}'>  	
				<li class="si_dashboard"><a href="dashboardEntity">Dashboard</a></li>
				<li class="si_custom_msg"><a href="customMessageEntity">Create Custom Message</a></li>
				<li class="si_confirmation"><a href="confirmationMessage">Confirmation Message</a></li>		
				<li class="si_send_msg selected"><a href="sendMessage">Send Message</a></li>
				<li class="si_sendafriend"><a href="sendAFriend">Send a Friend</a></li>
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
    <div class="content" id="id_content">
    	<div class="nav_pointer pos_01"></div>
      <!-- subheader -->
      <div class="subheader clearfix">
      	<h1>OPERATIONS SEND MESSAGE</h1> 
      	<p>Corporate</p>    	
      </div>
      <!-- // subheader -->
    	<div class="inner_box">
      	<!-- box -->
        <div class="box box_red_title box_send_msg">
        	<!-- title -->
        	<div class="box_title">
          	<h2>Send Message</h2>
          </div>
          
          <div class="campaign">
            <label for="campaign">User Name:</label>
            <form:input path="sendSearchKeywordString"/>
            <a href="images/TextTemplate.xlsx" class="btn_dark_blue">List Template</a>

          </div>	    
	              
          <!-- // title -->
          <!-- horizontal checkboxes wrapper -->
          <div class="horizontal_checkboxes">          	
          	<div>
				<span>
					<div class="btn_dark_blue" id="fileuploader">Upload List</div>
					<div id="status"></div>
					<div id="output"></div>
				</span> 
          	</div>
          	<h3 class="h3_sub ico_offices">Mobile List</h3>          	
          
            <div class="hc_wrapper">
              <ul class="ul_hc_scrollbox" id="id_hc_scrollbox">
			<div id="lists">
			</div>
              </ul>
              <div class="floatfix"></div> 
            </div>
          </div>              
                   
          <!-- two columns -->
          <div class="two_cols_wrapper_01 clearfix">


          	<!-- right column -->
            <div class="btn_message_box bmb">
              <div class="chk_container">              	
              </div> 
              		<br/>
	          	<form:hidden path="nowSched" />
			<a href="#" id="sendNow" onclick="sendIt('Y')" class="btn_send_now_lnk lnk_centered"> Send Now </a>               	                	               	
                <div>

              </div>            
            </div>
            <!-- // right column -->
          </div>
          <!-- // two columns -->

          <!-- two columns -->
          <div class="two_cols_wrapper_01 clearfix" style="padding:0 0 0 15px;">

          	<!-- right column -->
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
	  </center>
	</div>
                
              </div>
            </div>
            <!-- // right column -->
          </div>
          <!-- // two columns -->
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
              	<p>
	<ul style="font-size:110%;">
	<li><b>1.</b> - Enter User Name (Your Name)</li><p/>
	<li><b>2.</b> - Upload List containing the Entity ID and message</li><p/>
	<li><b>3.</b> - View the List to check that it is the right one</li><p/>	
	<li><b>4.</b> - Check the Message</li><p/>
	<li><b>5.</b> - Select Delivery</p/>
			Send Now or
			Schedule for later
	</li><p/>	
	</ul>              		
                </p>
              </div>
              <!-- // slide -->
            </div>
            <!-- // slider -->
          </div>
          <!-- // information wrapper -->
          <!-- biz info wrapper -->
          <div class="biz_info_wrapper">
            <div class="biz_info_grid_wrapper">
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

