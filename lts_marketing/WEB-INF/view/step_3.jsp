<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="user.CategoryBase"%>

<script LANGUAGE="JavaScript" SRC="scripts/CalendarPopup.js"></script>
<script LANGUAGE="JavaScript">
	var cal = new CalendarPopup();
</script>

<script language="JavaScript">
	function validate(form) {
		if (! isZip(form['category_3.zip'].value)) {
			alert("Please enter a valid Zip");
			return false;
		}	
	
		if (form['category_3.email'].value == null 
				|| form['category_3.email'].value.length <= 0) {
			alert("Please enter a valid Email");
			return false;
		}	

		if (form['category_3.adminMobilePhone'].value == null 
				|| form['category_3.adminMobilePhone'].value.length <= 0) {
			alert("Please enter a valid Business Mobile Phone");
			return false;
		}			

		if (form['category_3.description'].value == null 
				|| form['category_3.description'].value.length <= 0) {
			alert("Please enter a valid Description");
			return false;
		}	
		
		return true;
	}
</script>
		
		<div id="content" class="content">
		
			<div class="inner">
			
			<form:form method="post" action="" commandName="ltUser">

			<h3>Your Mobile Profile</h3>
			<hr/>
        		
			<table border="0" align="center" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top">
					<table width="455" border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td width="5">&nbsp;</td>
							<td width="150" colspan="2" valign="top">&nbsp;</td>
							<td width="200">&nbsp;</td>
							<td width="50">&nbsp;</td>
						</tr>
						<tr>	
							<td><font color="red">*</font></td>						
							<td colspan="2" valign="top">
								<strong><form:label path="category.businessName"><spring:message code="label.businessName" /></form:label></strong>
							</td>
							<td><form:input path="category.businessName" size="20" /></td>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td><font color="red">*</font></td>						
							<td colspan="2" valign="top">
								<strong><form:label path="category.adminMobilePhone"><spring:message code="label.adminMobilePhone" /></form:label></strong>
							</td>
							<td><form:input path="category.adminMobilePhone" size="15" />
								<a href="javascript:show_help('./help/adminMobilePhone.html')" styleClass="link_text"> 
								<img src="images/help.gif" width="16" height="16" alt="Help">
								</a>									
							</td>
							<td>&nbsp;</td>
						</tr>							
						<tr>
							<td><font color="red">*</font></td>	
							<td colspan="2" valign="top">
								<strong><form:label path="category.address"><spring:message code="label.address" /></form:label></strong>
							</td>
							<td><form:input path="category.address" size="35" /></td>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td><font color="red">*</font></td>	
							<td colspan="2" valign="top"><strong><form:label path="category.city"><spring:message code="label.city" /></form:label></strong></td>
							<td><form:input path="category.city" size="20" /></td>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td><font color="red">*</font></td>	
							<td colspan="2" valign="top"><strong><form:label path="category.state"><spring:message code="label.state" /></form:label></strong></td>
							<td><form:select path="category.state">
								<form:options collection="state_codes" property="value" labelProperty="label" />
							</form:select></td>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td><font color="red">*</font></td>	
							<td colspan="2" valign="top"><strong><form:label path="category.zip"><spring:message code="label.zip" /></form:label></strong></td>
							<td><form:input path="category.zip" size="7" /></td>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td><font color="red">*</font></td>	
							<td colspan="2" valign="top"><strong><form:label path="category.phone"><spring:message code="label.phone" /></form:label></strong></td>
							<td><form:input path="category.phone" size="20" /></td>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td><font color="red">*</font></td>	
							<td colspan="2" valign="top"><strong><form:label path="category.email"><spring:message code="label.email" /></form:label></strong></td>
							<td><form:input path="category.email" size="20" /></td>
							<td>&nbsp;</td>
						</tr>							
		                <tr>
		                	<td><font color="red">*</font></td>	
		                  <td colspan="2" valign="top">
		                  	<strong><form:label path="category.timezone"><spring:message code="label.timezone"/></form:label></strong>
						  </td>
		                  <td><form:select path="category.timezone">
		                  		<form:options collection="timezones" property="value" labelProperty="label" />
		                     </form:select>
		                  </td>
		                  <td>&nbsp;</td>
		                </tr> 									
					</table>
					</td>
					<td valign="top">
					<table border="0" cellpadding="3" cellspacing="0">
						<tr>
							<td>&nbsp;</td>
							<td colspan="2" valign="top">&nbsp;</td>
							<td colspan="2">&nbsp;</td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td colspan="2" valign="top"><strong><form:label path="category.facebookLink"><spring:message code="label.facebookLink" /></form:label></strong></td>
							<td><form:input path="category.facebookLink" size="20" /></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td colspan="2" valign="top"><strong><form:label path="category.twitterLink"><spring:message code="label.twitterLink" /></form:label></strong></td>
							<td><form:input path="category.twitterLink" size="20" /></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td colspan="2" valign="top"><strong><form:label path="category.busHours"><spring:message code="label.hours" /></form:label></strong><br />
							</td>
							<td colspan="2"><form:textarea path="category.busHours" cols="18" rows="3" /></td>
						</tr>
						<tr>
							<td>&nbsp;</td>
							<td colspan="2" valign="top"><strong><form:label path="category.areaServed"><spring:message code="label.areaServed" /></form:label></strong></td>
							<td><form:input path="category.areaServed" size="20" /></td>
						</tr>
						<tr>
							<td><font color="red">*</font></td>
							<td colspan="2" valign="top"><strong><form:label path="category.description"><spring:message code="label.description" /></form:label></strong><br/><span
								class="TextSize9">(limit 200 characters) </span></td>
							<td><form:input styleClass="text" path="category.description" rows="5" cols="35" /></td>
						</tr>

					</table>
					</td>
				</tr>
			</table>

			<br />

			<div align="center"> 
                <img src="images/btn_save.jpg"
                onclick="return save_preview(this.form, 'save');" title="Save" alt="Save" /> 				
			</div>

</form:form>
<br/>
</div>
</div>

