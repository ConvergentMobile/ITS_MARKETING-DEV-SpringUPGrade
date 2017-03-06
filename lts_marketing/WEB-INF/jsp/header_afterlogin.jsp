<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd"><html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="user.User"%>

<script language="JavaScript">
	function popup(message, reload) {
	    // get the screen height and width
	    var maskHeight = $(document).height();
	    var maskWidth = $(window).width();

	    // calculate the values for center alignment
	    var dialogTop = (maskHeight/3) - ($('#dialog-box').height());
	    var dialogLeft = (maskWidth/2) - ($('#dialog-box').width()/2);

	    // assign values to the overlay and dialog box
	    $('#dialog-overlay').css({height:maskHeight, width:maskWidth}).show();
	    $('#dialog-box').css({top:dialogTop, left:dialogLeft}).show();

	    // display the message
	    $('#dialog-message').html(message);
	    //$( "#dialog-message" ).dialog('open');	    
	    
	    if (reload == "1") {
	    	$('#reloadFlag').val("1");
	    }

	}

	function closePopup() {
		$('#dialog-overlay, #dialog-box').hide();  
		
		if ($('#reloadFlag').val() == "1") {
			location.reload();
		}
	}
	
	function set(form, action) {
		form.dispatch.value = action;		
		form.submit();
	}
	
	function setAction(action) {
		var form = document.getElementById("signin");

		form.action = action;		
		form.submit();
	}

	function openwindow(of) {
		window.open(of,"mywindow","menubar=1,resizable=1,width=950,height=750");
	}

	$(function() {
		 if (${error != null}) {
		    $( "#errwin" ).dialog({
				title: 'Alerts & Notifications',
				width: 400,
				height: 200,		
				dialogClass: 'no-close',
			       buttons: {
				  OK: function() {
				  	$(this).dialog("close");
				  	//location.reload();
				  }
			       },
			    });					
		}
	});
	
	//spinner
	function ajaxindicatorstart(text) {
		if (jQuery('body').find('#resultLoading').attr('id') != 'resultLoading') {
			jQuery('body').append('<div id="resultLoading" style="display:none"><div><img src="images/logo_lts.png"><div>'+text+'</div></div><div class="bg"></div></div>');
		}

		jQuery('#resultLoading').css({
			'width':'100%',
			'height':'100%',
			'position':'fixed',
			'z-index':'10000000',
			'top':'0',
			'left':'0',
			'right':'0',
			'bottom':'0',
			'margin':'auto'
		});

		jQuery('#resultLoading .bg').css({
			'background':'#000000',
			'opacity':'0.7',
			'width':'100%',
			'height':'100%',
			'position':'absolute',
			'top':'0'
		});

		jQuery('#resultLoading>div:first').css({
			'width': '250px',
			'height':'75px',
			'text-align': 'center',
			'position': 'fixed',
			'top':'0',
			'left':'0',
			'right':'0',
			'bottom':'0',
			'margin':'auto',
			'font-size':'16px',
			'z-index':'10',
			'color':'#ffffff'

		});

	    jQuery('#resultLoading .bg').height('100%');
	    jQuery('#resultLoading').fadeIn(300);
	    jQuery('body').css('cursor', 'wait');
	}

	function ajaxindicatorstop() {
	    jQuery('#resultLoading .bg').height('100%');
	    jQuery('#resultLoading').fadeOut(300);
	    jQuery('body').css('cursor', 'default');
	}

	jQuery(document).ajaxStart(function () {
		//show ajax indicator
		ajaxindicatorstart('Processing. Please wait ...');
	}).ajaxStop(function () {
		//hide ajax indicator
		ajaxindicatorstop();
	});		
	
</script>

<style>
.err_msg {font-size:large; color:white; font-weight:bold};
</style>

<!-- page wrapper -->
<div class="wrapper">
	<!-- header -->
  <div class="header">
  <c:choose>
	<c:when test = '${loc == "CA"}'>  	
		<a href="#" class="logo_lts_01"></a>
		<div class="logo_txt_01"><a href="#">Liberty Tax Service</a></div>
    		<div class="logo_87411"><a href="#"></a></div>		
	</c:when>
	<c:otherwise>
  		<a href="#" class="logo_lts"></a>
    		<div class="logo_siempre"><a href="dashboard-corporate.html"></a></div>  		
  	</c:otherwise>
  </c:choose>

    <ul class="top_nav">
      <li class="help"><a href="javascript:openwindow('US411_Help_Guide_2014.pdf')">Help</a></li>

<form:form id="signin" method="post" action="logout" commandName="ltUser"> 		 	
      
      <li class="signout"><a href="javascript:setAction('logout');">Signout</a></li>
     <!-- 
      <li class="welcome"><p>Entity: <c:out  value="${sessionScope.eId}"/></p></li>  
     -->
</form:form>
    </ul>
	<c:if test = '${loc != "CA"}'>     
        	<div class="logo_us411_r"><a href="dashboard-corporate.html"></a></div>
        </c:if>

  </div>
   
<div  class="err_msg" align="center">
		<c:if test = "${error != null}">
			*** <c:out value="${error}"/> ***
		</c:if>
</div>

<div id="errwin">
		<c:if test = "${error != null}">
			*** <c:out value="${error}"/> ***
		</c:if>
</div>

	<input type="hidden" id="reloadFlag"/>
	
    <div id="dialog-overlay"></div>
    <div id="dialog-box">
        <div class="dialog-content">
            <div align="center" id="dialog-message"></div>
            <br/>
            <a href="#" onclick="closePopup()" class="button">Close</a>
        </div>
    </div>
