<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd"><html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

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
<div id="wrap">
	
    <div id="header">
    
    	<div id="header_top">
        	<a class="left" href="./"><img id="logo" src="images/liberty_logo.gif" width="172" height="104" alt="Liberty Tax" title="Liberty Tax" /></a>
        	
            <div id="login">
            	</div>
                            
                <html:form styleId="signin" action="/UserAction" method="post">		
                    <html:hidden property="dispatch" value="error"/>			                    
                        <div style="*position: relative; *margin-top: -15px;">
                            <html:text styleClass="text" title="Login" property="login" value="" />
                            <html:password styleClass="text" title="Password" property="password" value=""/>
                            <html:image src="images/btn_signin.gif" onclick="javascript:set(this.form, 'login');" property="loginButton" alt="sign in" />
                        </div>
                        
                </html:form>
            
            </div>
		</div>
        
        <div id="header_main">
        	<div id="mainnav">
        		<a href="index.jsp">Home</a>
        	</div>
        </div>
    </div>
    
<script type="text/javascript">
	defaultValue('login > input.text', '#555');
</script>

<div  class="err_msg" align="center">
    <html:errors />
    <html:messages id="message"
        message="true">
        <li><bean:write name="message" /></li>
    </html:messages>
</div>		

