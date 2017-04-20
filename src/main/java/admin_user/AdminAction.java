package admin_user;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import keyword.KeywordApplication;
import keyword.KeywordDAOManager;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.util.LabelValueBean;

import sms.SMSDelivery;
import user.Login;
import user.TargetUserList;
import user.User;
import user.UserDAOManager;
import util.PropertyUtil;

public class AdminAction extends DispatchAction {
	Logger logger = Logger.getLogger(AdminAction.class);
	AdminDAOManager dao = new AdminDAOManager();
        ActionMessages errors = new ActionMessages();
	//ActionErrors errors = new ActionErrors();

	public ActionForward get(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		HttpSession hps = request.getSession();

		try {
			User user = (User)hps.getAttribute("AdminUser");
			
			AdminForm mForm = (AdminForm)form;

			List<UserProfileVO> sites = null;
			String letter = request.getParameter("letter");
			if (letter != null)
				sites = dao.getSites(user.getSiteId(), letter);
			
			hps.setAttribute("profiles", sites);
			
			mForm.setProfiles(sites);
			
			String mode = request.getParameter("mode");
			if (mode != null && mode.equals("reports"))
				return mapping.findForward("report_step1");

			return mapping.findForward("step_1");
		} catch (Exception e) {
			logger.error("get: " + e);
			e.printStackTrace();
			ActionMessages errors = new ActionMessages();
			errors.add("error1", new ActionMessage("error.user.create", e.toString()));
			//saveErrors(request, errors);
			return mapping.findForward("fail");
		} finally {
		}
	}
	
	//called by admin user to create a new site
	public ActionForward createKeyword(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		errors.clear();
		
		AdminForm mForm = (AdminForm) form;
		HttpSession hps = request.getSession();

		try {
			User adminUser = (User)hps.getAttribute("AdminUser");
			if (adminUser == null) {
				errors.add("error1", new ActionMessage("error.notAdminUser"));
				//saveErrors(request, errors);
				return mapping.findForward("create_keyword");				
			}

			User formUser = mForm.getUser(); //this is to get values from the form
			
			String email = formUser.getEmail();
			String mobilePhone = new SMSDelivery().normalizePhoneNumber(mForm.getMobilePhone());
			String keyword = formUser.getKeyword().toUpperCase();
			logger.debug("createLogin: keyword = " + email + ", " + mobilePhone + ", " + keyword + ", " + PropertyUtil.load().getProperty("shortcode"));
			
			//check if the keyword is available
			List<KeywordApplication> kwApps = new KeywordDAOManager().checkKWAvail(keyword, PropertyUtil.load().getProperty("shortcode"));
			if (kwApps != null) {
				errors.add("error1", new ActionMessage("error.keyword.unavail", keyword));
				//saveErrors(request, errors);
				return mapping.findForward("create_keyword");
			}
			
			//create User
			User user = new User();
			user.setSiteId(adminUser.getSiteId());
			user.setCategoryId(formUser.getCategoryId());
			user.setKeyword(keyword);

			//create this keyword
			KeywordApplication kwAppl = new KeywordApplication();
			kwAppl.setEmail(email);
			kwAppl.setMobilePhone(mobilePhone);
			kwAppl.setKeyword(keyword);
			kwAppl.setCategoryId(formUser.getCategoryId());
			kwAppl.setStatus("P");
			kwAppl.setRepName(mForm.getRepName());
			kwAppl.setAcceptTerms(true);
			kwAppl.setBusinessName(mForm.getBusinessName());
			kwAppl.setShortcode(PropertyUtil.load().getProperty("shortcode"));

			//set the siteId
			kwAppl.setSiteId(adminUser.getSiteId());
			
			new KeywordDAOManager().save(kwAppl);
						
			// create the default list (named <keyword>)
			List<TargetUserList> tuLists = new ArrayList<TargetUserList>();
			TargetUserList tuList = new TargetUserList();
			tuList.setListId(UUID.randomUUID().toString());
			tuList.setListName(keyword);
			tuLists.add(tuList);
			user.setTargetUserLists(tuLists);

			UserDAOManager userDAO = new UserDAOManager();
			userDAO.saveUser(user);
		
			//check if we are using fb 
			String mode = request.getParameter("mode");
			logger.debug("mode: " + mode);
			if (mode != null && mode.equals("fb")) {
				logger.debug("calling FBHandler: returnUrl = /us411/UserAction.do?dispatch=createLoginFB" );
				request.getSession().setAttribute("returnUrl", "/us411/UserAction.do?dispatch=createLoginFB");
				return new ActionForward("/FBHandler?action=auth", true);
			}

			//create a login record
			Login login = new Login(formUser.getLogin(), formUser.getPassword(), user.getUserId(), formUser.getSiteId());
			userDAO.saveLogin(login);

			errors.add("error1", new ActionMessage("error.keyword.reserved", keyword));
			//saveErrors(request, errors);
			
			//invalidate the sesssion
			//hps.invalidate();
			
			//refresh the keyword list
			this.get(mapping, mForm, request, response);
			
			mForm.reset(mapping, request);
			
			return mapping.findForward("create_keyword");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("CreateLoginAction:" + e);
			errors.add("error1", new ActionMessage("error.user.create", e.getMessage()));
			//saveErrors(request, errors);
			return mapping.findForward("create_keyword");
		} finally {
		}
	}		
	
	//check if a keyword is available
	public ActionForward checkKeywordAvail(ActionMapping mapping, ActionForm form,
										HttpServletRequest request, HttpServletResponse response)
										throws Exception {
		errors.clear();
		
		AdminForm mForm = (AdminForm)form;	
		User user = mForm.getUser();
		String keyword = user.getKeyword().toUpperCase();
		
		List<KeywordApplication> kwApps = new KeywordDAOManager().checkKWAvail(keyword, PropertyUtil.load().getProperty("shortcode"));
		if (kwApps != null) {
			logger.debug("keyword exists");
			errors.add("error1", new ActionMessage("error.keyword.unavail", keyword));
			//saveErrors(request, errors);
			return mapping.findForward("create_keyword");
		}
		
		errors.add("error1", new ActionMessage("error.keyword.avail", keyword));
		//saveErrors(request, errors);
		
		return mapping.findForward("create_keyword");
	}
	
	//used for Reports
	public ActionForward selectKeyword(ActionMapping mapping, ActionForm form,
										HttpServletRequest request, HttpServletResponse response)
												throws Exception {
		errors.clear();
		
		HttpSession hps = request.getSession();
		List<LabelValueBean>sites = (List<LabelValueBean>)hps.getAttribute("sites");
		
		if (sites == null)
			sites = new ArrayList<LabelValueBean>();
		
		try {
			User user = (User)hps.getAttribute("AdminUser");
			
			AdminForm mForm = (AdminForm)form;
			String keyword = request.getParameter("keyword");
			String userId = request.getParameter("userId");
			sites.add(new LabelValueBean(keyword, userId));
			
			hps.setAttribute("sites", sites);

			return mapping.findForward("report_run");
		} catch (Exception e) {
			errors.add("error1", new ActionMessage("error.user.create", e.toString()));
			//saveErrors(request, errors);
			return mapping.findForward("report_step1");
		} finally {
		}
	}
	
	public ActionForward runReport(ActionMapping mapping, ActionForm form,
										HttpServletRequest request, HttpServletResponse response)
												throws Exception {
		errors.clear();
		
		HttpSession hps = request.getSession();
		List<LabelValueBean>sites = (List<LabelValueBean>)hps.getAttribute("sites");
		
		try {
			User user = (User)hps.getAttribute("AdminUser");
			
			AdminForm mForm = (AdminForm)form;
			
			for (LabelValueBean site: sites)
				logger.debug("Got site: " + site.getLabel());
			
			return mapping.findForward("report_step1");
		} catch (Exception e) {
			errors.add("error1", new ActionMessage("error.user.create", e.toString()));
			//saveErrors(request, errors);
			return mapping.findForward("fail");
		} finally {
		}
	}	
}
