package user;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import keyword.KeywordApplication;
import keyword.KeywordDAOManager;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.actions.DispatchAction;

import sms.SMSDelivery;
import sms.SMSMain;
import util.PropertyUtil;

public class UserAction extends DispatchAction {
	Logger logger = Logger.getLogger(UserAction.class);
	protected UserDAOManager dao = new UserDAOManager();

	//Replaced by the new one
	public ActionForward step_2Old(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		UserForm mForm = (UserForm) form;
		HttpSession hps = request.getSession();

		try {
			User user = new User();
			String stmp = mForm.getCatgIdStep3Page(); //this is a concat of catg_id and sub_category_id
			String sfields[] = stmp.split("<>");
			//user.setCategoryId(mForm.getUser().getCategoryId());
			user.setCategoryId(Integer.parseInt(sfields[0]));
			if (sfields[1] != null && ! sfields[1].equals("0")) // "0" => null
				user.setSubCategoryId(Integer.parseInt(sfields[1]));
			
			user.setStatus("A");

			logger.debug("step_2: catg id = " + mForm.getUser().getCategoryId());
			
			hps.setAttribute("User", user);

			return mapping.findForward("step_2");
		} catch (Exception e) {
			logger.error("step_2:" + e.toString());
			ActionErrors errors = new ActionErrors();
			errors.add("error1", new ActionMessage("error.user.create", e
					.toString()));
			//saveErrors(request, errors);
			return mapping.findForward("fail");
		} finally {
		}
	}
	
	//called to create a username & password
	public ActionForward step_2(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		UserForm mForm = (UserForm) form;
		HttpSession hps = request.getSession();

		try {

			String keyword = request.getParameter("keyword");
			if (keyword == null || keyword.length() <= 0) {
				logger.error("Null keyword");
				return mapping.findForward("fail");
			}
			
			String userId = request.getParameter("userId");
			if (userId == null || userId.length() <= 0) {
				logger.error("Null userId");
				return mapping.findForward("fail");
			}
			
			logger.debug("UserAction:keyword, userId = " + keyword + ", " + userId);
			User user = new UserDAOManager().login(Long.valueOf(userId));

			hps.setAttribute("User", user);
			
			return mapping.findForward("step_2");
		} catch (Exception e) {
			logger.error("step_2:" + e.toString());
			ActionErrors errors = new ActionErrors();
			errors.add("error1", new ActionMessage("error.user.create", e
					.toString()));
			//saveErrors(request, errors);
			return mapping.findForward("fail");
		} finally {
		}
	}

	//This is not used anymore
	public ActionForward createLoginOld(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		UserForm mForm = (UserForm) form;
		HttpSession hps = request.getSession();
		ActionErrors errors = new ActionErrors();

		try {
			// user is created in step_2 after the category is selected
			User user = (User) hps.getAttribute("User"); // this is set in
															// UserForm
			if (user == null) {
				errors.add("error1", new ActionMessage("error.null.user"));
				//saveErrors(request, errors);
				logger.error("UserLoginAction:null user");
				return mapping.findForward("fail");
			}

			if (!user.getPassword().equals(mForm.getPassword1())) {
				errors.add("error1", new ActionMessage("error.passwordMatch"));
				//saveErrors(request, errors);
				return mapping.findForward("fail");
			}

			if (mForm.getUser().getKeyword() == null
					|| mForm.getUser().getKeyword().length() <= 0
					|| mForm.getUser().getKeyword().equals(" ")) {
				errors.add("error1", new ActionMessage("errors.required",
						"Keyword"));
				//saveErrors(request, errors);
				return mapping.findForward("fail");
			}

			user.setLogin(mForm.getUser().getLogin());
			user.setEmail(mForm.getUser().getEmail());
			user.setPassword(mForm.getUser().getPassword());

			// check if there is an admin user
			User adminUser = (User) hps.getAttribute("AdminUser");
			if (adminUser != null)
				user.setAdminProfileId(adminUser.getUserId());

			// String[] keywords = new
			// String[]{mForm.getUser().getKeyword().toUpperCase(),
			// mForm.getKeyword1().toUpperCase(),
			// mForm.getKeyword2().toUpperCase(),
			// mForm.getKeyword3().toUpperCase()};
			String[] keywords = new String[] { mForm.getUser().getKeyword()
					.toUpperCase() };
			String keyword = dao.checkKeyword(keywords, user.getLogin());
			if (keyword.equals("2")) { // login exists
				errors.add("error1", new ActionMessage("error.login.exists"));
				//saveErrors(request, errors);
				return mapping.findForward("fail");
			}
			if (keyword.equals("0")) { // none of the keywords are available
				errors.add("error1", new ActionMessage("error.keyword.unavail",
						keywords[0]));
				//saveErrors(request, errors);
				logger.error("createLogin:keyword unavailable");
				return mapping.findForward("fail");
			}

			logger.debug("createLogin: keyword = " + keyword);
			user.setKeyword(keyword);

			logger.debug("createLogin: catg id = "
					+ mForm.getUser().getCategoryId());

			// create the default list (named <keyword>)
			//TUL is now a part of User.hbm.xml
			List<TargetUserList> tuLists = new ArrayList<TargetUserList>();
			TargetUserList tuList = new TargetUserList();
			tuList.setListId(UUID.randomUUID().toString());
			tuList.setListName(keyword);
			tuLists.add(tuList);
			user.setTargetUserLists(tuLists);

			dao.saveUser(user);
			hps.setAttribute("User", user);

			logger.debug("createLogin: user id = " + user.getUserId());
			
			//create a login record
			Login login = new Login(mForm.getUser().getLogin(), mForm.getUser().getPassword(), user.getUserId(), user.getSiteId());
			dao.saveLogin(login);
			
			/*
			TargetUserList tuList = new TargetUserList();
			String listId = UUID.randomUUID().toString();
			tuList.setListId(listId);
			tuList.setUserId(user.getUserId());
			tuList.setListName(keyword);
			new TargetUserListDao().save(tuList);
			*/

			// return mapping.findForward("success");
			return forwardTo(mapping, request); //this will create the category object
		} catch (Exception e) {
			logger.error("CreateLoginAction:" + e);
			errors.add("error1", new ActionMessage("error.user.create", e));
			//saveErrors(request, errors);
			return mapping.findForward("fail");
		} finally {
		}
	}

	public ActionForward createLogin(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserForm mForm = (UserForm) form;
		HttpSession hps = request.getSession();
		ActionErrors errors = new ActionErrors();

		try {
			User user = (User)hps.getAttribute("User");

			String email = mForm.getEmail();
			String mobilePhone = new SMSDelivery().normalizePhoneNumber(mForm.getMobilePhone());
			String keyword = user.getKeyword();
			logger.debug("createLogin: keyword = " + email + ", " + mobilePhone + ", " + keyword + ", " + PropertyUtil.load().getProperty("shortcode"));

			KeywordApplication kwApp = new KeywordDAOManager().getKeyword(email, mobilePhone, keyword, PropertyUtil.load().getProperty("shortcode"));
			if (kwApp == null) {
				errors.add("error1", new ActionMessage("error.nokwApp", email, mobilePhone, keyword));
				//saveErrors(request, errors);
				return mapping.findForward("create_login_fail");
			}

			//check if the keywords match - just in case!
			if (! kwApp.getKeyword().equals(user.getKeyword())) {
				errors.add("error1", new ActionMessage("error.keywordMismatch", keyword));
				//saveErrors(request, errors);
				return mapping.findForward("create_login_fail");
			}
			
			// check if there is an admin user
			User adminUser = (User) hps.getAttribute("AdminUser");
			if (adminUser != null)
				user.setAdminProfileId(adminUser.getUserId());

			/* this is done in the keyword purchase step
			// create the default list (named <keyword>)
			List<TargetUserList> tuLists = new ArrayList<TargetUserList>();
			TargetUserList tuList = new TargetUserList();
			tuList.setListId(UUID.randomUUID().toString());
			tuList.setListName(keyword);
			tuLists.add(tuList);
			user.setTargetUserLists(tuLists);
			*/
			
			//check if we are using fb 
			String mode = request.getParameter("mode");
			logger.debug("mode: " + mode);
			if (mode != null && mode.equals("fb")) {
				logger.debug("calling FBHandler: returnUrl = /us411/UserAction.do?dispatch=createLoginFB" );
				request.getSession().setAttribute("returnUrl", "/us411/UserAction.do?dispatch=createLoginFB");
				return new ActionForward("/FBHandler?action=auth", true);
			}

			//create a login record
			Login login = new Login(mForm.getLogin(), mForm.getPassword(), user.getUserId(), user.getSiteId());
			dao.saveLogin(login);

			errors.add("error1", new ActionMessage("ok.user.create"));
			//saveErrors(request, errors);
			
			//invalidate the sesssion
			hps.invalidate();
			
			return mapping.findForward("step_1");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("CreateLoginAction:" + e);
			errors.add("error1", new ActionMessage("error.user.create", e.getMessage()));
			//saveErrors(request, errors);
			return mapping.findForward("create_login_fail");
		} finally {
		}
	}
	
	public ActionForward createLoginFB(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserForm mForm = (UserForm) form;
		HttpSession hps = request.getSession();
		ActionErrors errors = new ActionErrors();

		try {
			User user = (User)hps.getAttribute("User");

			//check if we are using fb for auth
			String mode = request.getParameter("mode");
			logger.debug("mode: " + mode);
			if (mode != null && mode.equals("fb")) {
				logger.debug("calling FBHandler: returnUrl = /us411/UserAction.do?dispatch=createLoginFB" );
				return new ActionForward("/FBHandler?returnUrl=/us411/UserAction.do?dispatch=createLoginFB", true);
			}
			
			//we should be here after FB auth and FBHandler has got the id
			String fbid = (String) hps.getAttribute("fbid");
			logger.debug("fbid: " + fbid);

			//create a login record
			Login login = new Login(user.getEmail(), "fbuser", user.getUserId(), user.getSiteId());
			login.setNetwork("Facebook");
			login.setNetworkId(fbid);
			dao.saveLogin(login);

			errors.add("error1", new ActionMessage("ok.user.create"));
			//saveErrors(request, errors);
			
			//invalidate the sesssion
			hps.invalidate();
			
			return mapping.findForward("step_1");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("CreateLoginAction:" + e);
			errors.add("error1", new ActionMessage("error.user.create", e.getMessage()));
			//saveErrors(request, errors);
			return mapping.findForward("create_login_fail");
		} finally {
		}
	}
		
	public ActionForward login(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ActionErrors errors = new ActionErrors();
		UserForm mForm = (UserForm) form;
		// UserProfile profile = null;
		
		try {
			Integer siteId = Integer.parseInt(PropertyUtil.load().getProperty("siteId"));
			
			logger.debug("login u,p,siteid = " + mForm.getLogin() + ", " + mForm.getPassword() + ", " + siteId);
			User user = dao.login(mForm.getLogin(), mForm.getPassword(), siteId);

			if (user == null) {
				logger.error("login: null User");
				errors.add("error1", new ActionMessage("error.null.user"));
				//saveErrors(request, errors);
				return mapping.findForward("step_1");
			}

			//check if a session exists
			User u = (User) request.getSession().getAttribute("User");
			if (u != null && u.getUserId() != null) { //for debug only
				logger.debug("existing user id: " + u.getUserId());
			}
			
			if (u != null && u.getUserId() != null && u.getUserId().equals(user.getUserId())) {
				logger.error("login: There is already an existing session on this machine");
				//errors.add("error1", new ActionMessage("error.session.exists"));
				////saveErrors(request, errors);
				//return mapping.findForward("fail");
				return forwardTo(mapping, request);
			}
			
			//set the siteId
			user.setSiteId(siteId);
			logger.debug("siteId = " + siteId);	
			
			// check if it is an admin user
			if (user.getUserType() != null && user.getUserType().equals("SA")) {
				logger.debug("userType: " + user.getUserType());
				request.getSession().setAttribute("User", user);
				request.getSession().setAttribute("AdminUser", user);
				return mapping.findForward("admin_user");
			}
			
			if (user.getUserType() != null && user.getUserType().equals("A")) {
				request.getSession().setAttribute("AdminUser", user);
			}
			
			//create the keyword directory if it does not exist
			//this is to avoid a race condition in the preview popup
			String outDir = PropertyUtil.load().getProperty("xhtmlFilePath") + "/" + user.getKeyword();			
			if (! new File(outDir).exists() && ! new File(outDir).mkdir()) {
				logger.error("Could not create dir: " + outDir);
				errors.add("error1", new ActionMessage("error.dir.create", outDir));
				//saveErrors(request, errors);
				return mapping.findForward("fail");				
			}
			
			/* commented out for now
			//check to see if the user has more than one keyword
			List<User> users = dao.getUsers(user.getUserAccountNumber());
			if (users.size() > 1) {//more than one keyword for this user
				request.getSession().setAttribute("users", users);
				return mapping.findForward("multiple_keywords");
			}
			*/
			
			request.getSession().setAttribute("User", user);

			return forwardTo(mapping, request);
		} catch (Exception e) {
			logger.error("login:" + e);
			errors.add("error1", new ActionMessage("error.user.login", e));
			//saveErrors(request, errors);
			return mapping.findForward("fail");
		}
	}

	public ActionForward save(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		UserForm mForm = (UserForm) form;
		User user = (User) request.getSession().getAttribute("user");

		UserProfile profile = new UserProfile();
		profile.setFieldValues(mForm.getFieldValues());
		profile.setUserId(user.getUserId());
		printUserProfile(profile);

		logger.debug("save:profileId = " + profile.getProfileId());
		dao.saveUserProfile(profile);

		return mapping.findForward("success");
	}

	/*
	//to access the configurator from the mobile profile page
	public ActionForward createHotspot(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		User user = (User)request.getSession().getAttribute("User");
		
		response.sendRedirect("./keyword/keyword_preview.php?mode=FT&keyword=" + user.getKeyword());
		return null;
	}
	
	public ActionForward afterCreateHotspot(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		User user = (User)request.getSession().getAttribute("User");
		String hotspotFile = request.getParameter("imgFile");
		logger.debug("afterCreateHotspot: file = " + hotspotFile);
		user.setHotspotFile(hotspotFile);
		dao.saveUser(user);
		
		return this.forwardTo(mapping, request);
	}
	*/
	
	// called from the admin feature
	public ActionForward getProfile(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		ActionErrors errors = new ActionErrors();
		
		HttpSession hps = request.getSession(false);
		if (! this.checkUserInSession(request)) {
			logger.error("login: null User");
			errors.add("error1", new ActionMessage("error.null.session"));
			//saveErrors(request, errors);
			return mapping.findForward("fail");
		}
		try {
			String userId = request.getParameter("userId");
			logger.debug("userId = " + userId);

			User user = new UserDAOManager().login(new Long(userId));
			hps.setAttribute("User", user);

			return forwardTo(mapping, request);
		} catch (Exception e) {
			logger.error("get: " + e);
			errors.add("error1", new ActionMessage("error.user.create", e
					.toString()));
			//saveErrors(request, errors);
			return mapping.findForward("fail");
		} finally {
		}
	}
	
	// called from the admin feature
	public ActionForward getProfileFromKW(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		ActionErrors errors = new ActionErrors();
		
		HttpSession hps = request.getSession(false);
		if (! this.checkUserInSession(request)) {
			logger.error("login: null User");
			errors.add("error1", new ActionMessage("error.null.session"));
			//saveErrors(request, errors);
			return mapping.findForward("fail");
		}
		try {
			Integer siteId = Integer.valueOf(request.getParameter("siteId"));
			String keyword = request.getParameter("keyword");

			User user = new UserDAOManager().login(keyword, siteId);
			hps.setAttribute("User", user);

			return forwardTo(mapping, request);
			//return mapping.findForward("category_" + user.getCategoryId() + "_popup");
		} catch (Exception e) {
			logger.error("get: " + e);
			errors.add("error1", new ActionMessage("error.user.create", e
					.toString()));
			//saveErrors(request, errors);
			return mapping.findForward("fail");
		} finally {
		}
	}

	public ActionForward forwardTo(ActionMapping mapping,
			HttpServletRequest request) {
		logger.debug("In UserAction: forwardTo");

		try {
			User user = (User) request.getSession().getAttribute("User");
			CategoryBase cbase = CategoryFactory.getInstance(user.getUserId(), user.getCategoryId());
			if (cbase == null) {
				logger.error("Null instance from factory");
				return mapping.findForward(null);
			}

			logger.debug("cbase categoryId: " + cbase.getCategoryId());

			request.getSession().setAttribute("category", cbase);
			
			return mapping.findForward(cbase.getCategoryName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ActionForward forgotPassword(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionErrors errors = new ActionErrors();

		UserForm mForm = (UserForm) form;

		String login = mForm.getLogin();
		String email = mForm.getEmail();
		Integer siteId = Integer.parseInt(PropertyUtil.load().getProperty("siteId"));
					
		String passwd = dao.validUser(login, email, siteId);
		if (passwd != null) { // valid user, email it
			StringBuffer sb = new StringBuffer();
			sb.append("<html>");
			sb.append("Your new temporary password is ").append(passwd).append(" - Click ");
			sb.append("<a href='").append(PropertyUtil.load().getProperty("baseURL")).append("'>here</a>");
			sb.append(" to reset your password.");
			sb.append("</html>");

			new SMSMain().sendEmail(null, email, PropertyUtil.load().getProperty("noticeEmailSender"),
					"Forgot Password?", sb.toString(), "HTML");

			//errors.add("error1", new ActionMessage("success.changePassword"));
			//saveErrors(request, errors);
		} else {
			errors.add("error1", new ActionMessage("error.user.invalid"));
			//saveErrors(request, errors);
			return mapping.findForward("forgot_password");
		}

		return mapping.findForward("forgot_password_success");
	}
	
	public ActionForward forgotLogin(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionErrors errors = new ActionErrors();

		UserForm mForm = (UserForm) form;

		String mobilePhone = new SMSDelivery().normalizePhoneNumber(mForm.getMobilePhone());
		String email = mForm.getEmail();
		Integer siteId = Integer.parseInt(PropertyUtil.load().getProperty("siteId"));
					
		String username = dao.validUserByPhone(mobilePhone, email, siteId, mForm.getKeyword());
		if (username != null) { // valid user, email it
			StringBuffer sb = new StringBuffer();
			sb.append("<html>");
			sb.append("Your username is ").append(username);
			sb.append(" - Click <a href='").append(PropertyUtil.load().getProperty("baseURL")).append("'>here</a>");
			sb.append(" to log in with this username and your current password.");
			
			new SMSMain().sendEmail(null, email, PropertyUtil.load().getProperty("noticeEmailSender"),
					"Forgot Username?", sb.toString(), "HTML");

			//errors.add("error1", new ActionMessage("ok.forgotLogin"));
			//saveErrors(request, errors);
		} else {
			errors.add("error1", new ActionMessage("error.forgotLogin"));
			//saveErrors(request, errors);
			return mapping.findForward("forgot_username");
		}

		return mapping.findForward("forgot_username_success");
	}
	
	public ActionForward resetPassword(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		UserForm mForm = (UserForm) form;

		String login = mForm.getLogin();
		String email = mForm.getEmail();
		String oldPassword = mForm.getPassword();
		String newPassword1 = mForm.getPassword1();
		String newPassword2 = mForm.getPassword2();

		ActionErrors errors = new ActionErrors();

		if (!newPassword1.equals(newPassword2)) {
			errors.add("error1", new ActionMessage("error.passwordMatch"));
			//saveErrors(request, errors);
			return mapping.findForward("reset_password");
		}

		int ret = dao.resetPassword(login, email, oldPassword, newPassword1);
		if (ret == 0) {
			errors.add("error1", new ActionMessage("error.changePassword"));
			//saveErrors(request, errors);
			return mapping.findForward("reset_password");
		}
		
		return mapping.findForward("reset_password_success");
	}

	public ActionForward loginFB(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionErrors errors = new ActionErrors();
		UserForm mForm = (UserForm) form;
		
		try {
			Integer siteId = Integer.parseInt(PropertyUtil.load().getProperty("siteId"));

			//check if we are using fb for auth
			String mode = request.getParameter("mode");
			logger.debug("mode: " + mode);
			if (mode != null && mode.equals("fb")) {
				logger.debug("calling FBHandler");
				request.getSession().setAttribute("returnUrl", "/us411/UserAction.do?dispatch=loginFB");
				return new ActionForward("/FBHandler?action=auth", true);
			}
			
			//we should be here after FB auth and FBHandler has got the id
			String networkId = (String) request.getSession().getAttribute("fbid");
			logger.debug("networkId: " + networkId);
			
			User user = dao.loginNetwork("Facebook", networkId, siteId);

			if (user == null) {
				logger.error("login: null User");
				errors.add("error1", new ActionMessage("error.null.user"));
				//saveErrors(request, errors);
				return mapping.findForward("step_1");
			}
			
			//check if a session exists
			User u = (User) request.getSession().getAttribute("User");
			if (u != null && u.getUserId() != null) { //for debug only
				logger.debug("existing user id: " + u.getUserId());
			}
						
			//set the siteId
			user.setSiteId(siteId);
			logger.debug("siteId = " + siteId);	
			
			// check if it is an admin user
			if (user.getUserType() != null && user.getUserType().equals("A")) {
				logger.debug("userType: " + user.getUserType());
				request.getSession().setAttribute("AdminUser", user);
				return mapping.findForward("admin_user");
			}
			
			//create the keyword directory if it does not exist
			//this is to avoid a race condition in the preview popup
			String outDir = PropertyUtil.load().getProperty("xhtmlFilePath") + "/" + user.getKeyword();			
			if (! new File(outDir).exists() && ! new File(outDir).mkdir()) {
				logger.error("Could not create dir: " + outDir);
				errors.add("error1", new ActionMessage("error.dir.create", outDir));
				//saveErrors(request, errors);
				return mapping.findForward("fail");				
			}
			
			/* commented out for now
			//check to see if the user has more than one keyword
			List<User> users = dao.getUsers(user.getUserAccountNumber());
			if (users.size() > 1) {//more than one keyword for this user
				request.getSession().setAttribute("users", users);
				return mapping.findForward("multiple_keywords");
			}
			*/
			
			request.getSession().setAttribute("User", user);

			return forwardTo(mapping, request);
		} catch (Exception e) {
			logger.error("login:" + e);
			errors.add("error1", new ActionMessage("error.user.login", e));
			//saveErrors(request, errors);
			return mapping.findForward("fail");
		}
	}
	
	//check if user has logged in. In case of multiple sessions for a user, he may have logged out of one of them
	public boolean checkUserInSession(HttpServletRequest request) throws Exception {
		HttpSession hps = request.getSession();
		User user = (User)hps.getAttribute("User");
		User adminUser = (User)hps.getAttribute("AdminUser");
		
		if (user == null && adminUser == null)
			return false;
		
		return true;
	}
	
	public void printUserProfile(UserProfile profile) {
		Map<String, Object> fvalues = profile.getFieldValues();

		logger.debug("printing UserProfile - fvalues");
		for (Map.Entry<String, Object> entry : fvalues.entrySet())
			logger.debug(entry.getKey() + ": " + entry.getValue());
	}
}
