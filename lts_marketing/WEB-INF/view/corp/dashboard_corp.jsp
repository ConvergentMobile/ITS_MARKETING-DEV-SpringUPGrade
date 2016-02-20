<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<script type="text/JavaScript">	
	function resetForm() {
		$('#thisForm')[0].reset();			
		//$('input[type="radio"]').prop('checked', false);
		//$('input:checkbox').removeAttr('checked');
		$('#thisFormP')[0].reset();
				
	}
	
	 $(document).ready(function() {
		resetForm();
		
		/*
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
		*/
	});	
 
	function sendEvent(evId) {
		var form = document.getElementById("thisForm");
		form.action = 'getListData?evId=' + evId;
		form.method = 'POST';			
		form.submit();	
	}
	
	function searchIt(){
		var form = document.getElementById("thisForm");
		//form.action ='searchListData?from=dashboard';
		form.action ='quickSearch';
		form.submit();
	}	

	function createHotspot(keyword){		
		window.open('createHotspot?keyword=' + keyword,"mywindow","scrollbars=yes,menubar=1,resizable=1,width=450,height=550");		
	}
	
	function showSelectedMsg(msgType) {
		if (msgType == "Corp") {		
			val = document.getElementById("sendSearchStateString").value;
			var msgText = $('#sendSearchStateString option:selected').text();
			alert('val, msgText: ' + val + ', ' + msgText);
		}
		if (msgType == "Cust") {		
			val = document.getElementById("sendSearchDMAString").value;
			var msgText = $('#sendSearchDMAString option:selected').text();
			alert('val, msgText: ' + val + ', ' + msgText);
		}	
					
		$('#sendSearchCityString').val(msgText);
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
 					resetForm();
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
            		$('#content').html(result);
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
		
	}
	
	function setKeyword(userId, entId, offId, status) {
		if (status == 'R') {
			var val = offId;
			var off = 'true';
			if (offId == '') {
				val = entId;
				off = 'false';
			}
			allocateKeyword(val, off);
			return;
		} 
		getProfile(userId);
	}
	
	function allocateKeyword(entOffId, off, userId) {
	        $.ajax({
	            type : 'GET',
	            url : 'allocateKeyword',
	            data: 'entOffId=' + entOffId + '&off=' + off,
	            success : function(result) {
            		$('#content').html(result);
				},
				error : function(e) {
					alert('error: ' + e.text());
				}                        
	        });
	}
	
    function qsearch(){
        var kw = $('#searchKeywordString').val();
        var off = $('#searchOfficeIdString').val();
        var ent = $('#searchEntityIdString').val();

        $.ajax({
            type : 'GET',
            url : 'quickSearch',
            contentType: "application/html",
                        data:"keyword=" + kw + "&officeId=" + off + "&entityId=" + ent,
            success : function(searchResult) {
                var outstr = "";
                $("#qresult").html(outstr);
                if (searchResult.length == 0) {
                        outstr = "<span class='name'><font size='-0.5'>No records found.</font></span>";
                        $("#qresult").html(outstr);
                }
                else if (searchResult.length > 1) {
                        outstr = "<span class='name'><font size='-0.5'>Multiple records found. Use the Search function</font></span>";
                        $("#qresult").html(outstr);
                } else {
                                $.each(searchResult, function(index, value) {
                                                outstr = "<span class='name'>" + value.keyword + " " + value.customField1 + "</span><span class='phone'> " + value.customField2 + "</span>";
                                        $("#qresult").append(outstr);
                                });
                                }
                                $('#qresult').attr("tabindex",-1).focus();
                        },
                        error : function(e) {
                                alert('error: ' + e.text());
                        }
        });
       }
        
	function deleteMessage(msgId) {
		$.ajax({
		    type : 'POST',
		    url : 'deleteCorpMessage',
		    data: 'msgId=' + msgId,
		    success : function(result) {
				//alert(result);
        		//$('#errwin').html('<div align="center">' + result + '</div>');
        		//$( "#errwin" ).dialog('open');
        		popup(result, 1);
                //location.reload();            						
			},
			error : function(e) {
					alert('error: ' + e.text());
			}                        
		});
	}     
	
	function createMessage1(){		
		//window.open('createCorpMessageG',"mywindow","scrollbars=yes,menubar=1,resizable=1,width=550,height=250");		
		$.ajax({
			type : 'GET',
			url : 'createCorpMessageG',
			success : function(result) {
				$('#dialog1').html(result);
				$('#dialog1').dialog({
					title : 'Create Message',
					height : 350,
					width : 550,
					position: {
					    my: "center",
					    at: "center",
					    of: "#corp_messages"
					},						
					buttons : {
						"Close" : function() {
							$(this).dialog("close");
							location.reload();
						}
					},
				});

			},
			error : function(e) {
				alert('error: ' + e.text());
			}
		});	
	}	     

    	function createMessage() {
            $.ajax({
                type : 'POST',
                url : 'createCorpMessage',
                data: $("#thisFormP").serialize(),
                success : function(result) {
            		//alert(result);
        			//$('#errwin').html('<div align="center">' + result + '</div>');
        			//$( "#errwin" ).dialog('open');	  
        			popup(result, 1);
            		//location.reload();            		
    			},
    			error : function(e) {
    				alert('error: ' + e.text());
    			}                        
            });
    	}   
    	
    	function editMessage(msgId, idx) {
    		var msgText = document.getElementById('approvedMsgs'+idx+'.messageText').value;
    		$('#adNewMsg').val(msgText);
    	
            $.ajax({
                type : 'POST',
                url : 'editCorpMessage',
                data: 'msgId=' + msgId + '&msgText=' + msgText,
                success : function(result) {
            		//alert(result);
        			//$('#errwin').html('<div align="center">' + result + '</div>');
        			//$( "#errwin" ).dialog('open');	
        			popup(result, 1);
                	//location.reload();            		
    			},
    			error : function(e) {
    				alert('error: ' + e.text());
    			}                        
            });
    	}        
        	
    
	function runSort(sortBy) {
		var form = document.getElementById("thisForm");

		var oldCol = $('#sortColumn').val();
		var oldOrd = $('#sortOrder').val();
		
		if (sortBy == oldCol) {		
			if (oldOrd == "asc") {
				$('#sortOrder').val('desc');
			} else {
				$('#sortOrder').val('asc')			}
		} else {
			$('#sortColumn').val(sortBy);
			$('#sortOrder').val('asc');
		}
		
		form.action = 'dashboardCorpP';
		form.method = 'POST';
		form.submit();	
	}
	

	function editMsgShow1(msgId) {
		$('#msgId').val(msgId);
		$('#regDiv_' + msgId).hide();

		$('.odd').not('#regDiv_' + msgId).show();
		$('.even').not('#regDiv_' + msgId).show();

		$('.odd.edit').not('#editDiv_' + msgId).hide();
		$('.even.edit').not('#editDiv_' + msgId).hide();
		
		$('#editDiv_' + msgId).show();
	}
	
	function editMsgShow(msgId) {
		$('#searchDMAString').val(msgId);
		
		$("[id^=editDiv]").hide();
		$("[id^=regDiv]").show();

				
		$('#editDiv_' + msgId).show();
		$('#regDiv_' + msgId).hide();
	}
	
	function changeKeyword(keyword, status) {
		if (status == 'R') {
			alert("Must allocate your keyword first");
			return;
		}
	        $.ajax({
	            type : 'GET',
	            url : 'changeKeyword',
            	    data: 'keyword=' + keyword,
	            success : function(result) {
			$('#dialog1').html(result);
			$('#dialog1').dialog({
 				dialogClass: "no-close",			
				title: 'Change Keyword',
				modal: false,
				width:600,	
                position: {
                    my: "center",
                    at: "center",
                    of: "#id_corp_keywords"
                },	       
			    	buttons: {
					"Close": function () {
				    		$(this).dialog("close");
				    		location.reload();				    		
					}
			    	},								
			});            	    
            	    },
				error : function(e) {
					alert('error: ' + e);
				}                        
	        });	
	}	
</script>

  <!-- // header -->
  <!-- content wrapper -->
  <div class="content_wrapper" id="content_wrapper">


<!-- Create New -->

<div id="popup-book" class="zoom-anim-dialog mfp-hide">
  <h1>Create new message</h1>
  <form:form id="thisFormP" method="post" action="" commandName="ltUser">
	<label>Language:</label>
	<form:select path="searchStateString">
		<form:option value="EN">English</form:option>
		<form:option value="SP">Spanish</form:option>		
	</form:select>      
	<br/>
    	<label>Message Text:</label>
    	<form:textarea path="adNewMsg" />         
	<br/><br/>
    <input type="button" onclick="createMessage()" value="Create Message" class="btn_dark_blue btn_04">    
  </form:form>
  <div class="floatfix"></div>
</div>

<!-- // Create New -->

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
				<li class="si_dashboard selected"><a href="dashboardCorp">Dashboard</a></li>	
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
      	<h1>DASHBOARD</h1>
        <p>Corporate</p>
      </div>
      
		<form:hidden path="sortColumn"/>
      	<form:hidden path="sortOrder"/>
      	      
       <!-- // subheader -->
    	<div class="inner_box">
      	<!-- box -->
        <div class="box box_blue_title box_keyword">
        	<!-- title -->
        	<div class="box_title">
          	<h2>List of Keywords</h2>
          </div>
          <!-- // title -->
          <table width="100%" class="grid grid_keyword">
          <colgroup>
          	<col width="6%"  />
            <col width="32%" />
            <col width="23%" />
            <col width="20%" />
            <col width="19%" />            
          </colgroup>
          <thead>
          	<tr>
            	<th class="th_01"><div>#</div></th>
              <th class="th_02"><div><a href="javascript:runSort('keyword')">Keyword</a></div></th>
              <th class="th_02"><div><a href="javascript:runSort('customField1')">Entity Id</a></div></th>
              <th class="th_02"><div><a href="javascript:runSort('customField2')">Office Id</a></div></th>              
              <th class="th_03"></th>              
            </tr>
          </thead>
          </table>
           <div id="id_corp_keywords">
            <table width="100%" class="grid grid_keyword">
            <colgroup>
              <col width="6%"  />
              <col width="32%" />
              <col width="23%" />
              <col width="20%" />
            	<col width="19%" />                          
            </colgroup>
             <tbody>           							
		<c:forEach var="site" items="${ltUser.sites}" varStatus="loopStatus"> 
			<c:set var="keyword" value="${site.keyword}" />
			<c:choose>
			   <c:when test = "${site.customField3 == 'R'}">
				<c:set var="status" value="${site.customField3}"/>
				<tr class="redc">
			   </c:when>
			<c:otherwise>
				<tr class="${loopStatus.index % 2 == 0 ? 'even' : 'odd'}">
			</c:otherwise>
			</c:choose>				
				<td class="td_01"><div><c:out  value="${loopStatus.count}"/></div></td>																
				<td class="td_02"><div><a href="#" onclick="setKeyword('${site.userId}', '${site.customField1}', '${site.customField2}', '${site.customField3}')">
						<c:out  value="${site.keyword}"/></a></div></td>								
				<td class="td_03"><div><c:out  value="${site.customField1}"/></div></td>
				<td class="td_03"><div><c:out  value="${site.customField2}"/></div></td>									
				<td class="td_04"><a href="#" onclick="changeKeyword('${site.keyword}', '${status}')" class="btn_select_01">Change</td>
			        
			</tr>			   
		</c:forEach>	
            </tbody>
            </table>
          </div>
          <table width="100%" class="grid grid_keyword">
            <colgroup>
              <col width="6%"  />
              <col width="32%" />
              <col width="23%" />
              <col width="20%" />
              <col width="19%" />              
            </colgroup>
            <tfoot>
              <tr>
                <td colspan="5"></td>
              </tr>
            </tfoot>
          </table>
        </div>
        <!-- // box -->
      	<!-- box -->
        <div class="box box_red_title ico_sb_msg">
        	<!-- title -->
        	<div class="box_title">
          	<h2>Edit Message</h2>
          	<a href="#popup-book" class="btn_dark_blue btn_05_lnk popup-with-zoom-anim" style="line-height:28px;" >Create Message</a>
          	
          </div>
          <!-- // title -->
            <div class="corp_msg_wrapper">
              
              <!-- grid wrapper -->
              <div class="corp_messages_wrapper" id="corp_messages">
                <table class="grid grid_04" width="100%">
                <colgroup>
                  <col width="90%" />
                  <col width="5%" />
                  <col width="5%" />
                </colgroup>        
                <tbody>
                 <form:hidden path="searchDMAString"/>
                 <form:hidden path="adNewMsg"/>
                 
                  <c:forEach var="corpMsg" items="${ltUser.approvedMsgs}" varStatus="loopStatus">
			  <tr id="regDiv_${corpMsg.messageId}" class="${loopStatus.index % 2 == 0 ? 'even' : 'odd'}">			  
			    <td class="td_01"><div><a href="#"><c:out value="${corpMsg.messageText}"/></a></div></td>
			    <td class="td_02"><a href="#" onclick="editMsgShow(${corpMsg.messageId})" class="lnk_edit_small">Edit</a></td>
			    <td class="td_03"><a href="#" onclick="deleteMessage(${corpMsg.messageId});" class="lnk_delete_small">Delete</a></td>
			  </tr>   
			  
			  <tr id="editDiv_${corpMsg.messageId}" class="${loopStatus.index % 2 == 0 ? 'even edit' : 'odd edit'}" style="display:none;">			  
			    <td class="td_01"><div><form:textarea path="approvedMsgs[${loopStatus.index}].messageText"/></div></td>
			    <td class="td_02"><a href="#" onclick="editMessage(${corpMsg.messageId}, ${loopStatus.index});" class="lnk_approve_small"></a></td>
			    <td class="td_03"><a href="#" onclick="deleteMessage(${corpMsg.messageId});" class="lnk_delete_small">Delete</a></td>
			  </tr> 			  
                 </c:forEach>
                 </tbody>
                </table>
              </div>
               <!-- // grid wrapper -->
            </div>

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
            <div class="sb_title sb_title_ico box_opt-out">
              <h2>Corporate Opt-Out</h2>
            </div>
            <!-- // title -->
            <!-- corp msg wrapper -->
            <div class="wide_column_wrapper search_container">
            <p>This will allow you to opt-out any number in all the liberty tax us411 database. If the number is not in the lts database you will be notified.</p>
            <div class="mt40 corp">
              <form:input path="searchCityString" placeholder="Enter a number to opt-out"/>
              <%--
              <a href="#" onclick="optout()" class="btn_dark_blue btn_03_lnk popup-with-zoom-anim">Submit</a>
              --%>
				<a href="#" onclick="optout()" class="btn_dark_blue btn_03_lnk">Submit</a>              
            </div>
          </div>


            <!-- // corp msg wrapper -->
            <div class="floatfix" style="height:5px;"></div>          
            <!-- title -->
            <div class="sb_title sb_title_ico box_quick_search">
        	<!-- title -->
          	<h2>Quick Search</h2>
          </div>
          <!-- // title -->
          <div class="wide_column_wrapper search_container">
          	<table width="130%" class="grid grid_03">
            <colgroup>
            	<col width="83"/>
              <col width="378"/>
            </colgroup>
            <tbody>
              <tr>
              	<td class="td_01"><form:label path="searchKeywordString"><spring:message code="label.keyword" /></form:label></strong></td>
                <td class="td_02"><form:input path="searchKeywordString" /></td>
              </tr>
              <tr>
              	<td class="td_01"><form:label path="searchOfficeIdString"><spring:message code="label.officeId" /></form:label></td>
                <td class="td_02"><form:input path="searchOfficeIdString" /></td>
              </tr>
              <tr>
              	<td class="td_01"><form:label path="searchEntityIdString"><spring:message code="label.entityId" /></form:label></td>
                <td class="td_02"><form:input path="searchEntityIdString" /></td>
              </tr>
              <tr>
              	<td></td>
                <td class="td_03"><input type="button" onclick="qsearch()" value="Search" class="btn_dark_blue btn_03"></td>
              </tr>
            </tbody>
            </table>
		<div class="result" id="qresult">
			<span class="label">Search <br>Result</span>						
		</div>              
          </div>
          
            
            

            <div class="floatfix" style="height:5px;"></div>
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
  	
			