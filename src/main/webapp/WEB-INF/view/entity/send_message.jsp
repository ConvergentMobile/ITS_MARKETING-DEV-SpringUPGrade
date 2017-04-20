<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<script type="text/javascript" src="js/tipped.js"></script>
<link rel="stylesheet" type="text/css" href="css/tipped.css" />


<script>
  $(document).ready(function() {
    Tipped.create('#tabs-01', 'Liberty English');
    Tipped.create('#tabs-02', 'Liberty Spanish');
    Tipped.create('#tabs-03', 'Siempre English');
    Tipped.create('#tabs-04', 'Siempre Spanish');
    Tipped.create('#tabs-05', 'Juntos Podemos English');
    Tipped.create('#tabs-06', 'Juntos Podemos Spanish');
    Tipped.create('#tabs-07', 'Custom');
  });
</script>

<script>
  $(document).ready(function() {
    Tipped.create('#tabs-08', 'Liberty English');
    Tipped.create('#tabs-09', 'Liberty Spanish');
    Tipped.create('#tabs-10', 'Siempre English');
    Tipped.create('#tabs-11', 'Siempre Spanish');
    Tipped.create('#tabs-12', 'Juntos Podemos English');
    Tipped.create('#tabs-13', 'Juntos Podemos Spanish');
    Tipped.create('#tabs-14', 'Custom');
  });
</script>

<script type="text/JavaScript">	
	$(document).ready(function() {
		resetForm($('#thisForm'));
		
		/*
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
		*/
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
	
	function showSelectedMsgOld(elem, msgType, msg) {
		elemId = $(elem).attr("id");	
		var msgText = $("label[for='"+elemId+"']").text();
		if (msgType == 'Cust') {
			msgText = msg;
		}
		$('#sendSearchCityString').val(msgText);
	}
	
	function showSelectedMsg(elem, msgType, msg) {
		elemId = $(elem).attr("id");	
		var msgText = $("label[for='"+elemId+"']").text();
		if (msgType == 'Cust') {
			msgText = msg;
		}
		var msgId = $('input[name="sendSearchEntityIdString"]:checked').val();
		//alert('1: ' + $('input[name="sendSearchEntityIdString"]:checked').val());
		$('#sendSearchCityString').val(msgId);
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
				width : 800,
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
		var chkArray = [];

		$('input[name="listIds"]:checked').each(function() {
			chkArray.push($(this).val());
		});

		if (chkArray.length > 1) {
			popup("You must select only one list", 0);	
			return;
		} else if (chkArray.length == 0) {
			popup("Please select at least one list", 0);
			return;
		}
		
        $.ajax({
            type : 'GET',
            url : 'listMgmt',
		    data: {
		    	listId: chkArray[0],
		    },            
            success : function(result) {
				$('#dialog1').dialog({
					title : '',
					height : 640,
					width : 1300,
					
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
		astr += '<input id="listIds' + id + '" type="checkbox" class="chk_dark" name="listIds" value="' + val + '"/></li>';
		astr += '&nbsp;<a href="#" onclick="viewList(\''+val+'\')">View</a>';
				
		astr2 = '&nbsp;<a href="#"  onclick="viewList(\''+val+'\')">' + name + '</a>';
		var astr1 = '<li>' + astr2;
		astr1 += '&nbsp;<input id="listIds' + id + '"type="checkbox" class="chk_dark" name="listIds" value="' + val + '"/>';
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

	function getProfile(userId, officeId) {
		var outstr = '';
		var outstr2 = '';
		$.ajax({
			type : 'GET',
			contentType : "application/json",
			url : 'getProfileInfo',
			data : 'userId=' + userId,
			success : function(category) {
				if (!category) {
					//alert("No Business Information found");
					$('#biz_info').html("No Business Information found");
					$('#biz_info_2').html(outstr2);						
					return;
				}
				if (category.businessName != null) {
					outstr += category.businessName + '<br/>';
				}
				//if (category.website != null) {
					//outstr += category.website + '<br/>';
				//}
				if (officeId != '' && officeId != 'Entity') {
					outstr += "http://libertytax.com/" + officeId + '<br/>';
				} else {
					outstr += "http://libertytax.com" + '<br/>';
				}				
				if (category.address != null) {
					outstr += category.address + '<br/>';
					outstr += category.city + ',' + category.state + '&nbsp;'
						+ category.zip + '<br/>';					
				}
				if (category.phone != null && category.phone != '') {
					outstr += '(' + category.phone.substring(0, 3) + ') ' + category.phone.substring(3, 6) + '-' + category.phone.substring(6);
				}				
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
	
	function getList(userId, officeId) {
		//alert('cnt: ' + $('[name="officeIds"]:checked').length);
		if ($('[name="officeIds"]:checked').length <= 0) {
			alert('Must select atleast one office');
			return;
		}
		
	        $.ajax({
	            type : 'GET',
	            url : 'getList',
	            contentType: "application/json",
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
	        
	        getProfile(userId, officeId);
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
	
	function getJobs1() {
		/*
		//check only if it is not Corporate
		if ('${ltUser.user.roleActions[0].roleType}' != "Corporate") {
			if ($('[name="officeIds"]:checked').length <= 0) {
				alert("Must select atleast one office");
				return;
			}
		}
		*/
		
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
	
	function getJobs() {
	        $.ajax({
	            type : 'GET',
	            url : 'getScheduledJobs',
            	    data: $("#thisForm").serialize(),
	            success : function(result) {
			$('#dialog1').dialog({
				title : '',
				height : 400,
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
	
	function optout() {
		var mp = $('#searchCityString').val();
		if (mp == '') {
			popup("Must enter a mobile phone number", 0);
			return;
		}
		
	        $.ajax({
	            type : 'POST',
	            url : 'optout',
	            data: {
	            	'mobilePhone': mp,
	            },
	            success : function(result) {
 					popup(result, 0);    
 					//resetForm();
 					$('#searchCityString').val('');
 		    	},
		    	error : function(e) {
					alert('error: ' + e.text());
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
    <div class="content" id="id_content_08">
    	<div class="nav_pointer pos_01"></div>
      <!-- subheader -->
      <div class="subheader clearfix">
      	<h1>SEND MESSAGE</h1>
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
        <div class="box box_red_title box_send_msg">
        	<!-- title -->
        	<div class="box_title">
          	<h2>Send Message</h2>
          </div>
          
          <div class="campaign">
            <label for="campaign">Campaign Name:</label>
            <form:input path="sendSearchKeywordString"/>
                <c:choose>
                        <c:when test = '${ltUser.user.roleActions[0].roleType == "Corporate"}'>
                                <a href="accountingSend" class="btn_dark_blue">OPERATIONS</a>
                </c:when>
                <c:otherwise>
         <%--
            <a href="#" onclick="uploadList()" class="btn_dark_blue">Upload List</a>
         --%>
           <a href="#" onclick="listMgmt()" class="btn_dark_blue">Create List</a>
                </c:otherwise>
            </c:choose>

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
		    		onclick="getList('${item.userId}', '${item.customField2}')" value="${item.customField2}" />
		    </li>
		</c:forEach> 
              </ul>
              <div class="floatfix"></div> 
            </div>
          </div>              
          <!-- // horizontal checkboxes wrapper -->
            	<h3 class="h3_sub ico_select">Select List of Mobile Numbers:</h3>
              <div class="grey_box_01 mobile_numbers_wrapper">
              	<div class="grey_box_title_01">
                	<input type="checkbox" onclick="checkAll()" name="all_numbers" id="all_numbers" style="position:relative;top:4px;">
                  	<label for="all_numbers">Check All</label>                  	
                </div>    
		<ul class="ul_mn_scrollbox" id="id_mn_scrollbox">
			<div id="lists">
			</div>
		</ul>  
                <div class="floatfix"></div>
              </div>             
           <!-- // horizontal checkboxes wrapper -->
         
         <form:hidden path="sendSearchCityString"/>
          
          <!-- two columns -->
          <div class="two_cols_wrapper_01 clearfix">
          	<!-- left column -->
            	<h3 class="h3_sub ico_message">Select a Message to Send:</h3>
              <!-- tabs ///////////////////////////////////////////////////////////////////  -->
              <div class="tabs_01 tabs_height_01" id="tabs_01">
                <ul class="ul_tabs_select">
                  <li class="tab_01"><a href="#tabs_01_1" class="selected" id="tabs-01">LTS ENG</a></li>
                  <li class="tab_02"><a href="#tabs_01_2" id="tabs-02">LTS SPAN</a></li>
                  <li class="tab_02"><a href="#tabs_01_3" id="tabs-03">ST ENG</a></li>                  
                  <li class="tab_02"><a href="#tabs_01_4" id="tabs-04">ST SPAN</a></li>                  
                  <li class="tab_02"><a href="#tabs_01_5" id="tabs-05">JP ENG</a></li>                  
                  <li class="tab_02"><a href="#tabs_01_6" id="tabs-06">JP SPAN</a></li>                  
                  <li class="tab_02"><a href="#tabs_01_7" id="tabs-07">CUSTOM</a></li>                  
                </ul>
                <!-- tab 01 -->
                <div class="tabs_01_content" id="tabs_01_1">
                <c:if test = "${fn:length(ltUser.approvedMsgs) > 0}">
                  <ul class="ul_scroll_list scroll_list_001">
                  	<form:radiobuttons element="li" path="sendSearchEntityIdString" onchange="showSelectedMsg(this, 'Corp')" items="${ltUser.approvedMsgs}" itemValue="messageId" itemLabel="messageText"/>  
	          	  </ul>
                </c:if>	          	  
                </div>
                      <!-- // tab 01 -->
                <!-- tab 02 -->
                <div class="tabs_01_content" id="tabs_01_2">
                  <c:if test = "${fn:length(ltUser.approvedMsgsSP) > 0}">
                  <ul class="ul_scroll_list scroll_list_001">          
                  	<form:radiobuttons element="li" path="sendSearchEntityIdString" onchange="showSelectedMsg(this, 'SP')" items="${ltUser.approvedMsgsSP}" itemValue="messageId" itemLabel="messageText"/>  
                  </ul>
                  </c:if>
                </div>         
                <!-- // tab 02 -->
                <!-- tab 03 -->
                <div class="tabs_01_content" id="tabs_01_3">
                  <c:if test = "${fn:length(ltUser.approvedMsgsST) > 0}">
                  <ul class="ul_scroll_list scroll_list_001">  
                  	<form:radiobuttons element="li" path="sendSearchEntityIdString" onchange="showSelectedMsg(this, 'Corp')" items="${ltUser.approvedMsgsST}" itemValue="messageId" itemLabel="messageText"/>  
             	<%--                 
            		<c:forEach var="row" items="${ltUser.approvedMsgsST}" varStatus="loopStatus">
            		<li>
                 	<span class="lispan"><a href="JavaScript:void(0)" onclick="deleteMsg('${row.messageId}')" class="lnk_del_01"></a> </span>
                  	<form:radiobutton path="sendSearchCityString" onchange="showSelectedMsg(this, 'Corp', '${row.messageText}')" value="messageId"/>${row.messageText}
			</li>
                  	</c:forEach>
            	--%>                  	
                  </ul>
                  </c:if>
                </div>         
                <!-- // tab 03 -->                
                <!-- tab 04-->
                <div class="tabs_01_content" id="tabs_01_4">
                  <c:if test = "${fn:length(ltUser.approvedMsgsSTSP) > 0}">
                  <ul class="ul_scroll_list scroll_list_001">          
                  	<form:radiobuttons element="li" path="sendSearchEntityIdString" onchange="showSelectedMsg(this, 'Corp')" items="${ltUser.approvedMsgsSTSP}" itemValue="messageId" itemLabel="messageText"/>  
                  </ul>
                  </c:if>
                </div>         
                <!-- // tab 04 -->
                <!-- tab 05 -->
                <div class="tabs_01_content" id="tabs_01_5">
                  <c:if test = "${fn:length(ltUser.approvedMsgsJT) > 0}">
                  <ul class="ul_scroll_list scroll_list_001">          
                  	<form:radiobuttons element="li" path="sendSearchEntityIdString" onchange="showSelectedMsg(this, 'Corp')" items="${ltUser.approvedMsgsJT}" itemValue="messageId" itemLabel="messageText"/>  
                  </ul>
                  </c:if>
                </div>         
                <!-- // tab 05 -->
                <!-- tab 06 -->
                <div class="tabs_01_content" id="tabs_01_6">
                  <c:if test = "${fn:length(ltUser.approvedMsgsJTSP) > 0}">
                  <ul class="ul_scroll_list scroll_list_001">          
                  	<form:radiobuttons element="li" path="sendSearchEntityIdString" onchange="showSelectedMsg(this, 'Corp')" items="${ltUser.approvedMsgsJTSP}" itemValue="messageId" itemLabel="messageText"/>  
                  </ul>
                  </c:if>
                </div>         
                <!-- // tab 06 -->
                <!-- tab 07 -->
                <div class="tabs_01_content" id="tabs_01_7">
                  <c:if test = "${fn:length(ltUser.customMsgs) > 0}">
                  <ul class="ul_scroll_list scroll_list_001">          
                  	<li><strong>If you have a message with single or double quotes, they will be escaped with the \ character in this list.
                  	Don't worry - The message will appear correctly when it is received/viewed on the mobile phone
                  	</strong></li>                  
                  	<form:radiobuttons element="li" path="sendSearchEntityIdString" onchange="showSelectedMsg(this, 'Cust')" items="${ltUser.customMsgs}" itemValue="messageId" itemLabel="messageText"/>  
                  </ul>
                  </c:if>
                </div>         
                <!-- // tab 07 -->
              </div>
              <!-- // tabs ////////////////////////////////////////////////////////////////  -->
            <!-- // left column -->

          	<!-- right column -->
            <div class="btn_message_box bmb">
              <div class="chk_container">
                	<form:checkbox path="includePhone" id="includePhone" class="chk_light mr5"/>
                	<label for="includePhone">Include default phone number</label>
                <!-- 
                	<form:checkbox path="includeLink" id="includeLink" class="chk_light mr5"/>
                	<label for="includeLink">Include default link</label>   
                --> <br /><br />
                	<label for="includeLink">Select link</label>                  	
					<form:select path="adNewMsg">
						<form:option value="">No Link</form:option>
			                  	<form:option value="1">Info Form</form:option>
			                  	<form:option value="2">Default Link</form:option>
					</form:select>                  	            	
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
          <div class="two_cols_wrapper_01 clearfix">

          	<!-- right column -->
	            <h3 class="h3_sub ico_schedule">Schedule for Later:</h3>
	    <a href="#" onclick="getJobs()" class="lnk_scheduled">See Scheduled Messages</a>
	            
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
    <div class="sidebar" id="id_sidebar_08">
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
	<li><b>1. NAME YOUR CAMPAIGN</b> - NO ONE other then you will ever see this.  Your campaign names 
	appear in the reports for you to identify the messages you have sent out.</li><p/>
	<li><b>2. SELECT YOUR OFFICE(s)</b> - When you select your office(s), the corresponding opt-in list 
	will appear below.</li><p/>
	<li><b>3. SELECT YOUR LIST</b> - Choose the list(s) that you would like to send your message to. 
	To see the numbers in your list, click on the KEYWORD, and a pop-up will show you all of your 
	mobile numbers.  You may also notice customer names and opt-in timestamps in your list. </li><p/>	
	</ul>
                </p>
              </div>
              <!-- // slide -->
            	<!-- slide -->
              <div class="slide">
              	<p>
	<ul>
	<li><b>4. SELECT A MESSAGE</b> - Click on one of the standard corporate messages, or pick from 
	one of the custom messages if you have created one.</li><p/>
	<li><b>5. SEND or SCHEDULE </b> - You can send a message immediately by clicking the green "SEND NOW"
		button, or you can click the blue "SCHEDULE" button to schedule a message to go out at a 
		later time. There are 2 check boxes in this section. The first check box allows you 
		to include your office number, and the second check box is to include your office page 
		link.  The office page link will only go out if you are sending from the OFFICE level 
		login.  If you send from the ENTITY level login, the corporate link will be sent out 
		with the message.</li><p/>	
	</ul>              	
                </p>
              </div>
              <!-- // slide -->

            	<!-- slide -->
              <div class="slide">
              	<p>
	<ul>
	<li><b>6. SEE SCHEDULED MESSAGES</b> - If you click on this link, you will see all messages 
		scheduled to go out in the future.</li><p/>
	<li><b>7. ***Create List***</b> This is a new feature that allows you to create sub-lists from 
		your main opt-in list.  Click on CREATE LIST, give your list a name, select numbers from 
		your opt-in list on the left, and arrow them over to the right.  These numbers will never 
		disappear from your main opt-in list. Once you have selected all the numbers for your new 
		list, click the SAVE button. <b>ONCE YOU SET A LIST, YOU CANNOT MODIFY IT.</b></li>
	</ul>              	
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
				<c:when test = '${fn:length(ltUser.sites[0].customField2) > 0 
						&& ltUser.sites[0].customField2 != "Entity"}'>	
					<c:set var="website" value="http://libertytax.com/${ltUser.sites[0].customField2}"/>									
				</c:when>
				<c:otherwise>		
					<c:set var="website" value="http://libertytax.com"/>														
				</c:otherwise>
				</c:choose>
				<c:if test="${fn:length(website) > 0}">
					<c:out value="${website}"/>
					<br/>
				</c:if>				
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
		
		<!-- biz info wrapper -->
          <div class="biz_info_wrapper">
            <!-- title -->
            <div class="sb_title sb_title_ico ico_sb_biz">
              <h2 class="Mobile Marketing">Opt-Out</h2>
            </div>
            <!-- // title -->
            <div class="biz_info_grid_wrapper" style="padding: 20px;height:110px;">
              <table class="grid bizinfo_grid" width="100%">
              <tr>
                 <td class="td_01">
        		This will allow you to opt-out any number in all the Liberty Tax US411 database. 
        		If the number is not in the lts database you will be notified                 
                 </td>
              </tr>
              <tr><td>&nbsp;</td></tr>
              <tr>
              	<td class="mt40 corp">
          		<form:input path="searchCityString" placeholder="Enter a number to opt-out"/>
	  		<a href="#" onclick="optout()" class="btn_dark_blue btn_03_lnk">Submit</a>               	
              	</td>
              </tr>              
	      </table>	      
	    </div>
	  </div>				
          <!-- // opt-out wrapper -->

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

