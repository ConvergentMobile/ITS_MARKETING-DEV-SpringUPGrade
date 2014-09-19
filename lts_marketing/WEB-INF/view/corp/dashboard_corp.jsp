<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<style>
.ui-dialog .ui-dialog-title {
  text-align: center;
  width: 100%;
}

tr.redc { background-color: red; };
</style>

<script type="text/JavaScript">	
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
        
    	function createMessage() {
            $.ajax({
                type : 'POST',
                url : 'createCorpMessage',
                data: $("#thisForm").serialize(),
                success : function(result) {
            		alert(result);
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
		
		form.action = 'dashboardCorp';
		form.method = 'GET';
		form.submit();	
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
    	<li class="si_dashboard" selected><a href="dashboardEntity">Dashboard</a></li>
		<li class="si_custom_msg"><a href="customMessageEntity">Create Custom Message</a></li>
	</c:if>
	<c:if test = '${ltUser.user.roleActions[0].roleType == "Office"}'>
    		<li class="si_dashboard" selected><a href="dashboardOffice">Dashboard</a></li>	
		<li class="si_custom_msg"><a href="customMessage">Create Custom Message</a></li>
	</c:if>   
 	<c:if test = '${ltUser.user.roleActions[0].roleType == "Corporate"}'>
    		<li class="si_dashboard" selected><a href="dashboardCorp">Dashboard</a></li>	
      		<li class="si_custom_msg_approve"><a href="customMessageCorp">Approve Custom Messages</a></li>   
	      	<li class="si_search"><a href="corpSearch">Search</a></li>
	</c:if>       	
	<li class="si_send_msg"><a href="sendMessage">Send Message</a></li>
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
          </colgroup>
          <thead>
          	<tr>
            	<th class="th_01"><div>#</div></th>
              <th class="th_02"><div><a href="javascript:runSort('keyword')">Keyword</a></div></th>
              <th class="th_02"><div><a href="javascript:runSort('customField1')">Entity Id</a></div></th>
              <th class="th_02"><div><a href="javascript:runSort('customField2')">Office Id</a></div></th>              
            </tr>
          </thead>
          </table>
           <div id="id_entity_keywords">
            <table width="100%" class="grid grid_keyword">
            <colgroup>
              <col width="6%"  />
              <col width="32%" />
              <col width="23%" />
              <col width="20%" />
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
            </colgroup>
            <tfoot>
              <tr>
                <td colspan="4"></td>
              </tr>
            </tfoot>
          </table>
        </div>
        <!-- // box -->
      	<!-- box -->
        <div class="box box_red_title box_quick_search">
        	<!-- title -->
        	<div class="box_title mb9">
          	<h2>Quick Search</h2>
          </div>
          <!-- // title -->
          <div class="wide_column_wrapper search_container">
          	<table width="461" class="grid grid_03">
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
            <div class="sb_title sb_title_ico ico_sb_msg">
              <h2 class="Mobile Marketing">Create Message</h2>
            </div>
            <!-- // title -->
            <!-- corp msg wrapper -->
            <div class="corp_msg_wrapper">
              <!-- corp msg box -->
              <div class="corp_msg_box">
              <!-- 
                <div class="chk_wrapper_01 clearfix">
                </div>
              -->
                <h4>Message Text</h4>
                <div class="corp_msg_text" id="id_corp_msg">
					<form:textarea path="adNewMsg"/>         
                </div>
                <div class="btn_04_wrapper">
                	<input type="button" onclick="createMessage()" value="Save Message" class="btn_dark_blue btn_04"></td>         	
                </div>
              </div>
              <!-- corp msg box -->
               
              <!-- grid wrapper -->
              <div class="corp_messages_wrapper" id="corp_messages">
              	<table class="grid grid_04" width="100%">
                <colgroup>
                	<col width="84%" />
                  <col width="7%" />
                  <col width="9%" />
                </colgroup>
                <tbody>
					<c:forEach var="pMsg" items="${ltUser.pendingMsgs}" varStatus="loopStatus"> 
						<c:set var="keyword" value="${site.keyword}" />
						<tr>
                			<tr class="odd">						
							<td class="td_01"><div><c:out  value="${pMsg.messageText}"/></div></td>								
							<td class="td_01"><div><fmt:formatDate type="both" pattern="MM/dd/yyyy hh:mm a z" value="${pMsg.lastUpdated}" /></div></td>
                    		<td class="td_02"><a href="#" class="lnk_edit_small">Edit</a></td>
                    		<td class="td_03"><a href="#" class="lnk_delete_small">Delete</a></td>						
						</tr>
					</c:forEach> 
                </tbody>
                </table>
              </div>
               <!-- // grid wrapper -->
            </div>
            <!-- // corp msg wrapper -->
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
  	
			