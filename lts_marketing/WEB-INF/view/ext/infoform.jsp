<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd"><html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href='http://fonts.googleapis.com/css?family=Open+Sans:400,300,300italic,400italic,600,600italic,700,700italic,800,800italic|Open+Sans+Condensed:300,300italic,700&subset=latin,latin-ext,' rel='stylesheet' type='text/css'>
<link rel="stylesheet" type="text/css" href="../ext/styles/common.css">
<title>Liberty Tax Service</title>
<meta content="width=device-width" name="viewport">
<script type="text/javascript" src="../ext/scripts/modernizr.custom.95238.js"></script>

<script src="//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>

<script src="../js/jquery.validate.min.js"></script>
<script src="../js/additional-methods.min.js"></script>
    
<script>
	function resetForm(form) {
	    form.find('input:text, input:password, input:file, select, textarea').val('');
	    form.find('input:radio, input:checkbox')
		 .removeAttr('checked').removeAttr('selected');
	}

	$(document).ready(function() {
		resetForm($('#thisForm'));	
		validateInit();
	});
	
	function saveIt() {
		var valid = $('#thisForm').valid();

		if (! valid) {
			valid.errors;
			return;
		}	

		var outstr = '';
		$.ajax({
		    type : 'POST',
		    url : 'infoForm',
		    data: $('#thisForm').serialize(),
		    success : function(result) {	
				if (result.indexOf('Success') >= 0) {
					//alert("Information has been saved");
					outstr = '<div class="success">Information has been saved</div>';
				} else {
					//alert(result);
					outstr = '<div class="error">'+result+'</div>';
				}
				$("#result").html(outstr).slideDown();
				$('#result').show();				
			},
			error : function(e) {
				alert('error: ' + e.text());
			}                        
		});		
	}
	
	function validateInit() {
		var validator = $("#thisForm").validate({
			rules: {			
				'searchStateString': {
					required: true,
					phoneUS: true,
				},			
				'searchCityString': {
					required: true,
					email: true,
				},
				'searchKeywordString': {
					required: true,	
				},
				'searchDMAString': {
					required: true,				
				},				
			},
			messages: {			
				'searchStateString': {
					required: "Please enter a valid phone number",
					minlength: "Please enter a valid phone number",
				},			
				'searchCityString': {
					required: "Please enter a valid email address",
					minlength: "Please enter a valid email address",
				},
				'searchKeywordString': {
					required: "Please enter a first name",
				},
				'searchDMAString': {
					required: "Please enter a last name",
				},					
			},
			errorContainer: $('#errorContainer'),
			errorLabelContainer: $('#errorContainer ul'),
			wrapper: 'li'
		});		
	}
</script>

<style>
	tr.redc { background-color: red; };
	
	#errorContainer {		
	    display: none;
	    overflow: auto;
	    background-color: #FFDDDD;
	    border: 1px solid #FF2323;
	    padding-top: 0;
	}

	#errorContainer label {
	    float: none;
	    width: auto;
	    color: red;
	}	
	
	input.error {
	    border: 1px solid #FF2323;
	}	
</style>
</head>

<body>
   <form:form id="thisForm" method="post" action="" commandName="ltUser">
	
   	<form:hidden path="sortOrder"/>
   	<form:hidden path="sortColumn"/>
   	
<div id="page" class="">
	<div class="logo">Liberty Tax Service</div>
	<div class="title">Request for More<br>Information</div>
	<div id="errorContainer" style="display: none">
	    <p>Please correct the following errors and try again:</p>
	    <ul>
	    </ul>
	    <br/>
	</div>	
	<div class="form" id="contact_form">
		<div id="result" style="display:none"></div>
   	
   		<div class="field">
   			<label for="fname">First Name:</label>
   			<form:input path="searchKeywordString"/>
   		</div>
   		<div class="field">
   			<label for="lname">Last Name:</label>
   			<form:input path="searchDMAString"/>
   		</div>
   		<div class="field">
   			<label for="phone">Mobile Phone:</label>
   			<form:input path="searchStateString"/>
   		</div>
   		<div class="field">
   			<label for="email">Email:</label>
   			<form:input path="searchCityString"/>
   		</div>
    		<div class="field">
   			<label for="dummy"></label>
   			<br/><br/><br/>
   		</div> 		
		<div class="field">
			<a href="#" onclick="saveIt()">Submit</a>			
		</div> 
	</div>
</div>
   </form:form>
   </body>
</html>