<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd"><html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

<%@ page import="user.User"%>

<script language="JavaScript">
	function set(form, action) {
		form.dispatch.value = action;		
		form.submit();
	}
	
	function openwindow(of) {
		window.open(of,"mywindow","menubar=1,resizable=1,width=950,height=750");
	}	
</script>

<style>
.err_msg {size:14px; color:red; font-weight:bold};
</style>

<%
	//User user = (User)request.getSession().getAttribute("User");
	//String decal = "http://localhost:8080/outfiles/hotspot/" + user.getHotspotFile();
%>

<div id="wrap">
	
    <div id="header">
    
    	<div id="header_top">
        	<a class="left" href="index.jsp"><img id="logo" src="images/liberty_logo.gif" width="270" height="65" alt="CompareNetworks" title="CompareNetworks" /></a>
         
            <div id="login">
            <div id="topnav"><a href="http://www.facebook.com/liberty_tax.com" target="_blank"><img src="images/facebook.gif" width=32"" height="32" alt="Facebook"></a>
            	</div>
            
            <html:form styleId="signin" action="/logout" method="post">
            	<html:hidden property="dispatch" value="logout" />
               <div><a href="/liberty_tax/forgot_password.do">Forgot password?</a> / <a href="/liberty_tax/reset_password.do">Reset password</a></div>		
            
                <html:image src="images/sign_out.jpg" onclick="window.close();" property="signoutButton" alt="sign out" />
            </html:form>
            </div>
		</div>
		
        <div id="header_main">
        	<div id="topinfo">Instantly Connect with Local Business and Find Information</div>
        	<div id="mainnav">
        		<a href="/liberty_tax/step_1.do">Home</a>
                <a href="javascript:openwindow('./SalesAid.pdf')">Info</a>
        		<a href="javascript:openwindow('http://www.libertytax.com')">Contact Us</a>
        	</div>
        </div>
        
	</div>     
        
    <div class="err_msg" align="center">
        <html:errors />
        <html:messages id="message"
            message="true">
            <li><bean:write name="message" /></li>
        </html:messages>
    </div>		