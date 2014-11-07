<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

  <!-- // header -->
  <!-- content wrapper -->
  <div class="content_wrapper" id="content_wrapper">
   <form:form id="thisForm" method="post" action="dashboard" commandName="ltUser">
		
  	<!-- left side navigation -->
  	<ul class="ul_left_nav">
		<c:if test = '${ltUser.user.roleActions[0].roleType == "Entity"}'>  	
	    		<li class="si_dashboard"><a href="dashboardEntity">Dashboard</a></li>
			<li class="si_custom_msg selected"><a href="customMessageEntity">Create Custom Message</a></li>
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
	    		<li class="si_dashboard"><a href="dashboardCorp">Dashboard</a></li>	
	      		<li class="si_custom_msg_approve"><a href="customMessageCorp">Approve Custom Messages</a></li>   
				<li class="si_send_msg"><a href="sendMessage">Send Message</a></li>
				<li class="si_search"><a href="corpSearch">Search</a></li>			
				<li class="si_reports"><a href="getReports">Reports</a></li>
		</c:if> 
		<c:if test = '${ltUser.user.roleActions[0].roleType == "AD"}'>  	
	    		<li class="si_dashboard"><a href="dashboardAD">Dashboard</a></li>
			<li class="si_custom_msg"><a href="customMessageEntity">Create Custom Message</a></li>
			<li class="si_reports"><a href="getReports">Reports</a></li>
		</c:if>	
	
      	<li class="si_toolbox selected"><a href="cmtoolbox">Convergent Toolbox</a></li>
    </ul>

    <!-- // left side navigation -->
    <!-- content area -->
    <div class="content" id="id_content">
    	<div class="nav_pointer pos_01"></div>
      <!-- subheader -->
      <div class="subheader clearfix">
      	<h1>Mobile Profile</h1>
      </div>
      <!-- // subheader -->
    	<div class="inner_box">
      	<!-- box -->
        <div class="box box_blue_title box_profile">
        	<!-- title -->
        	<div class="box_title">
          	<h2>Convergent Mobile Toolbox</h2>
          </div>
          <!-- // title -->
          <!-- box profile -->
          <div class="profile_content">

			<div align="center">Coming soon ...</div>
          </div>
          <!-- // box profile -->
        </div>
        <!-- // box -->
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
                  	<div>
                    </div>
                  </td>
                </tr>
              </tbody>
              </table>
            </div>
          </div>
          <!-- // biz info wrapper -->

        </div>
        <!-- // sidebar box -->
      </div>

    </div>
    <!-- // sidebar -->
    <div class="floatfix"></div>
  </div>
  
  		</form:form>

  <!-- // content wrapper -->