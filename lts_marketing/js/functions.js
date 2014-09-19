/* file: functions.js */

function clearText(field) {
	if (field.defaultValue == field.value) field.value = "";
	else if (field.value == "") field.value = field.defaultValue;
}

/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

$(document).ready(function(){

  /* infoslider */
	
  $('#infoslider').cycle({ 
		timeout: 9000,
		speed: 400,
		// cleartypeNoBg: true,
		prev: "#id_prev_info",
		next: ".get_next_info",
    cleartypeNoBg: true,
		pagerAnchorBuilder: function(idx, slide) { 
			return '<li><a href="javascript:void(0);">&nbsp;</a></li>'; 
		} 
	}); 


  $("#tabs_01 ul").idTabs();
  $("#tabs_02 ul").idTabs();
  
  $('.scroll_list_001').slimScroll({
    height: '137px',
    distance: '6px',
    railVisible: true,
    alwaysVisible: true
  });

  $('#id_corp_keywords').slimScroll({
    height: '390px',
    distance: '6px',
    railVisible: true,
    alwaysVisible: true
  });
  
  $('#id_report_01').slimScroll({
    height: '565px',
    distance: '0px',
    railVisible: true,
    alwaysVisible: true
  });  
  
  $('#results_scroll').slimScroll({
    height: '380px',
    distance: '0',
    railVisible: true,
    alwaysVisible: true
  });

  $('#id_entity_keywords').slimScroll({
    height: '150px',
    distance: '6px',
    railVisible: true,
    alwaysVisible: true
  });  
  

  $('#id_corp_msg').slimScroll({
    height: '110px',
    distance: '0px',
    railVisible: true,
    alwaysVisible: true
  });
  
  $('#corp_messages').slimScroll({
    height: '175px',
    distance: '6px',
    railVisible: true,
    alwaysVisible: true
  });

  $("#id_pending").slimScroll({
    height: '81px',
    distance: '6px',
    railVisible: true,
    alwaysVisible: true
    // disableFadeOut: true
  });

  $("#id_msg_history_001").slimScroll({
    height: '162px',
    distance: '6px',
    railVisible: true,
    alwaysVisible: true,
    // disableFadeOut: true
  });

  $("#id_rejected_messages_001").slimScroll({
    height: '162px',
    distance: '6px',
    railVisible: true,
    alwaysVisible: true,
    // disableFadeOut: true
  });  
  
  $('.mts_01').slimScroll({
    height: '86px',
    distance: '6px',
    // railVisible: true,
    // alwaysVisible: false,
    disableFadeOut: true
  });
  
  $('.mts_02').slimScroll({
    height: '103px',
    distance: '6px',
    // railVisible: true,
    // alwaysVisible: false,
    disableFadeOut: true
  });

  $('.ul_phone_numbers').slimScroll({
    height: '155px',
    distance: '6px',
    railVisible: true,
    alwaysVisible: true
  });
  
  $('#id_hc_scrollbox').slimScroll({
    height: '78px',
    distance: '0px',
    railVisible: true,
    alwaysVisible: true
  });
  
  $('.ul_hc_scrollbox li .chk_light').change(function(){
    if (this.checked) {
      $(this).parent().parent().removeClass('selected');
      // alert("uncheck")
      // 
    } else {
      // alert(1)
      $(this).parent().parent().addClass('selected');
      // 
    }
  });
  
  $('.ul_scroll_list input:radio').screwDefaultButtons({ 
  	image: "url(images/radio.png)",
  	width:	 18,
  	height:	 18
  });
  
  $('.chk_light').screwDefaultButtons({ 
  	image: "url(images/checkbox.png)",
  	width:	 13,
  	height:	 13
  });

  $('.chk_dark').screwDefaultButtons({ 
  	image: "url(images/checkbox_dark.png)",
  	width:	 13,
  	height:	 13
  });

  $('.ul_scroll_list input[type=radio]').change(function(){
    if ($(this).is(":checked")) {
      $(this).parent().parent().parent().find(".selected").removeClass("selected");
      $(this).parent().parent().addClass("selected");
      // alert($(this).parent().parent().find('input[tye="hidden"]').val());
      // alert($(this).closest('.two_cols_wrapper_01').find(".msg_text_scroll p").html())
      // $(this).closest('.two_cols_wrapper_01').find(".msg_text_scroll p").text($(this).parent().parent().find('input[type="hidden"]').val());

      $(this).closest('.two_cols_wrapper_01').find(".ta_text_msg").val($(this).parent().parent().find('input[type="hidden"]').val());

      // $('#id_msg').text($(this).parent().parent().find('input[type="hidden"]').val());
    } 
  });


  $('#all_numbers').change(function(){
    if (this.checked) {
      $('.ul_phone_numbers .chk_light').screwDefaultButtons("check");
    } else {
      $('.ul_phone_numbers .chk_light').screwDefaultButtons("uncheck");
    }
  });
  
  $('.ul_phone_numbers .chk_light').change(function(){
    if (this.checked) {
      $(this).parent().parent().addClass('selected');
    } else {
      $(this).parent().parent().removeClass('selected');
    }
  });

  $('.scroll_list_002').slimScroll({
    height: '206px',
    distance: '6px',
    railVisible: true,
    alwaysVisible: true
  });
  

  // setTimeout ( function () {var ht = $('#id_content').outerHeight( true ); }, 1);
 
  set_equal_heights();  
  // alert(0);
});

/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////

function set_equal_heights() {

 var h_content = $('#id_content').outerHeight(); 
 var h_sidebar = $('#id_sidebar').outerHeight();
 
 if (h_content >= h_sidebar) {
   // alert(h_content)
   $('#id_sidebar').css('min-height', (h_content - 6) + "px");
 } else {
   $('#id_content').css('min-height', (h_sidebar - 3) + "px");
 }
}
