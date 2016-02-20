/* popups.js */

$(window).ready(function(){

  $('#id_search_pu').slimScroll({
    height: '390px',
    distance: '6px',
    railVisible: true,
    alwaysVisible: false,
    disableFadeOut: true
  });
  

  $.magnificPopup.open({
        items: { src: '#id_popup_01' },
        type: 'inline',
        fixedContentPos: false,
        fixedBgPos: true,
        overflowY: 'auto',
        closeBtnInside: true,
        preloader: false,
      }, 
    0
  );
  
  
  $('.lnk_del_01').bind('click', showPuDel);



  
})

/////////////////////////////////////

function showPuDel(e) {
  showMask();
  unbindPuDel();
  bindPuDel();
  calcPuDelPos();
  $('#id_pu_del').show();
}


function showMask() {
  $('#mask').fadeIn(10);
  $('#mask').fadeTo("fast", 0.5);
}

function hideMask() {
  $('#mask').hide();
}


function bindPuDel() {
  $('#mask').bind('click', closePuDel);
  $('#id_pu_del .btn_pu_del').bind('click', closePuDel);
  $('#id_pu_del .lnk_close').bind('click', closePuDel);
}

function unbindPuDel() {
  $('#id_pu_del .lnk_close').unbind('click', closePuDel);
  $('#id_pu_del .btn_pu_del').unbind('click', closePuDel);

  $('#mask').unbind('click', closePuDel);
}


function closePuDel() {
  $('#id_pu_del').hide();
  hideMask();
}

function calcPuDelPos() {
  var viewPortH = window.innerHeight;
  var puDelH = $('#id_pu_del').outerHeight();
  
  var puDelTop = (viewPortH / 2) - ( puDelH / 2 ) - 20;
  
  $('#id_pu_del').css('top', puDelTop);
  
}
