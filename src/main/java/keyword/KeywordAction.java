package keyword;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mdp_common.ANetHandler;
import net.authorize.data.Customer;
import net.authorize.data.creditcard.CreditCard;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.util.LabelValueBean;
import org.apache.struts.action.ActionMessages;

import sms.SMSDelivery;
import sms.SMSMain;
import user.Login;
import user.TargetUserList;
import user.User;
import user.UserDAOManager;
import user.UserForm;
import util.EncryptDecrypt;
import util.PropertyUtil;

public class KeywordAction extends DispatchAction {
	Logger logger = Logger.getLogger(KeywordAction.class);
	ActionMessages errors = new ActionMessages();
	KeywordDAOManager kwDAO = new KeywordDAOManager();
	
	private final Integer siteId = Integer.parseInt(PropertyUtil.load().getProperty("siteId"));

	public ActionForward pre(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {	
			errors.clear();			
			KeywordForm mForm = (KeywordForm)form;

			String mode = request.getParameter("mode");
			logger.debug("In KeywordAction:mode = " + request.getParameter("mode"));
			
			if (mode != null && (mode.equals("purchase"))) {
				List<KeywordApplication> kwAppls = new ArrayList<KeywordApplication>();
				request.getSession().setAttribute("kwAppls", kwAppls);
				return mapping.findForward(mode);
			}
			
			return mapping.findForward("success");
		} catch (Exception e) {
			errors.add("error1", new ActionMessage("kwappl.error", e));
			//saveErrors(request, errors);			
			e.printStackTrace();
			return mapping.findForward("fail");
		}
	}

	//this is used by US411
	public ActionForward checkAvail(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {		
			errors.clear();
			logger.debug("In KeywordAction:checkAvail");
			
			String mode = request.getParameter("mode"); 
			if (mode == null || mode.length() <= 0)
				mode = "check";
			
			KeywordForm mForm = (KeywordForm)form;
			User user = mForm.getUser();
		
			String keyword = mForm.getKeywordApplication().getKeyword().toUpperCase();
			
			KeywordDAOManager kwdao = new KeywordDAOManager();		
			List<KeywordApplication> kwApps = kwdao.checkKWAvail(keyword, 
								mForm.getKeywordApplication().getShortcode());
			if (kwApps != null) {
				errors.add("error1", new ActionMessage("error.keyword.unavail", keyword));
				//saveErrors(request, errors);
				return mapping.findForward("fail");
			}
			
			if (user == null) {
				logger.debug("no user found");
				return mapping.findForward("fail");
			}
			
			user.setKeyword(keyword);
			request.getSession().setAttribute("User", user);

			errors.add("error1", new ActionMessage("error.keyword.avail", keyword));
			//saveErrors(request, errors);
			
			logger.debug("keyword " + keyword + " is available");
						
			if (mode.equals("purchase"))
				return mapping.findForward("payment_1");
			else
				return mapping.findForward("available");

			//pass the mode=FT so that the php session vars can be reset
			//this is for US411 - figure out the flow
			
			//response.sendRedirect("./keyword/keyword_preview.php?mode=FT&keyword=" + user.getKeyword());
			//return null;
		} catch (Exception e) {
			errors.clear();
			errors.add("error1", new ActionMessage("kwappl.error", e));
			//saveErrors(request, errors);			
			e.printStackTrace();
			return mapping.findForward("fail");
		}
	}
	
	public ActionForward next(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {		
			logger.debug("In KeywordAction:next");
			
			errors.clear();

			KeywordForm mForm = (KeywordForm)form;
			User user = (User)request.getSession().getAttribute("User");
			mForm.setUser(user);
            String keyword = request.getParameter("keyword");
            String imgFile = request.getParameter("imgFile");
            
            user.setKeyword(keyword);
            user.setHotspotFile(imgFile);
			user.setAmount(Double.valueOf(mForm.getProps().getProperty("kwappl.cost")));

			logger.debug("keyword, id = " + user.getKeyword() + ", " + user.getUserId());
			
			return mapping.findForward("next");
		} catch (Exception e) {
			errors.add("error1", new ActionMessage("kwappl.error", e));
			//saveErrors(request, errors);			
			e.printStackTrace();
			return mapping.findForward("fail");
		}
	}
	
	public ActionForward payment_1(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {		
			logger.debug("In KeywordAction:payment_1");
			
			errors.clear();

			KeywordForm mForm = (KeywordForm)form;
			User user = (User)request.getSession().getAttribute("User");
			mForm.setUser(user);
            String keyword = user.getKeyword();
            
			logger.debug("keyword, id = " + user.getKeyword() + ", " + user.getUserId());
			
			return mapping.findForward("next");
		} catch (Exception e) {
			errors.add("error1", new ActionMessage("kwappl.error", e));
			//saveErrors(request, errors);			
			e.printStackTrace();
			return mapping.findForward("fail");
		}
	}
	
	public ActionForward paymentConfirm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {			
			KeywordForm mForm = (KeywordForm)form;
			//User user = mForm.getUser();
			User user = (User)request.getSession().getAttribute("User");
			KeywordApplication kwAppl = mForm.getKeywordApplication();
			
			errors.clear();
		
			CreditCard cc = CreditCard.createCreditCard();
			cc.setCreditCardNumber(mForm.getCcNumber());
			cc.setExpirationMonth(mForm.getCcExpirationMon());
			cc.setExpirationYear(mForm.getCcExpirationYear());
			
			//calculate the cost after promotion
			String promoCode = mForm.getPromoCode().toUpperCase();
			Promotion promo = null;
					
			//logger.debug("promoCode: " + promoCode);
			//figure out what the promo is for
			if (promoCode != null && promoCode.length() > 0) {
				promo = new KeywordDAOManager().getPromotion(promoCode);
				if (promo == null) {
					errors.add("error1", new ActionMessage("kwappl.invalid.promo", promoCode));
					//saveErrors(request, errors);			
					return mapping.findForward("payment_fail");					
				}
			}
			
			BigDecimal finalCost = this.calculatePrice(promo);

			if (finalCost == null) { //invalid promoCode
				errors.add("error1", new ActionMessage("kwappl.invalid.promo", promoCode));
				//saveErrors(request, errors);			
				return mapping.findForward("payment_fail");	
			}
			
			logger.debug("finalCost = " + finalCost.toString());
			logger.debug("categoryId, ccExpMon: " + mForm.getUser().getCategoryId() + ", " + mForm.getCcExpirationMon());

			user.setCategoryId(mForm.getUser().getCategoryId()); // set the categoryId
			user.setAmount(finalCost.doubleValue());			
			request.getSession().setAttribute("User", user);
			request.getSession().setAttribute("cc", cc);
			mForm.setUser(user);
			
			saveToken(request);
			
			return mapping.findForward("payment_confirm");
		} catch (Exception e) {
			errors.add("error1", new ActionMessage("kwappl.payment_error", e.getMessage()));
			//saveErrors(request, errors);			
			return mapping.findForward("payment_fail");
		}		
	}

	public ActionForward payment(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {			
			errors.clear();

			//check for resubmit
		     if (isTokenValid(request)) {  
		            resetToken(request);  
		        } else if (! isTokenValid(request)) {  
		            logger.error("You have already submitted the request");
					errors.add("error1", new ActionMessage("error.resubmit"));
					//saveErrors(request, errors);			            
		            return mapping.findForward("payment_fail");
		        } 
		     
			KeywordForm mForm = (KeywordForm)form;
			User user = (User)request.getSession().getAttribute("User");
			KeywordApplication kwAppl = mForm.getKeywordApplication();
					
			Customer customer = Customer.createCustomer();
			customer.setFirstName(mForm.getUser().getFirstName());
			customer.setLastName(mForm.getUser().getLastName());
			customer.setAddress(mForm.getUser().getBillingAddress1() + " " + mForm.getUser().getBillingAddress2());
			customer.setCity(mForm.getUser().getBillingCity());
			customer.setState(mForm.getUser().getBillingState());
			customer.setZipPostalCode(mForm.getUser().getBillingZip());

			/*
			CreditCard cc = CreditCard.createCreditCard();
			cc.setCreditCardNumber(mForm.getCcNumber());
			cc.setExpirationMonth(mForm.getCcExpirationMon());
			cc.setExpirationYear(mForm.getCcExpirationYear());
			*/

			CreditCard cc = (CreditCard)request.getSession().getAttribute("cc");
			
			//calculate the cost after promotion
			String promoCode = mForm.getPromoCode().toUpperCase();
			Promotion promo = null;
			if (promoCode != null && promoCode.length() > 0) { //do this only if a promoCode was entered
				logger.debug("promoCode: " + promoCode);
				//figure out what the promo is for
				promo = new KeywordDAOManager().getPromotion(promoCode);
				if (promo == null) {
					errors.add("error1", new ActionMessage("kwappl.invalid.promo", promoCode));
					//saveErrors(request, errors);			
					return mapping.findForward("payment_fail");					
				}
			}
			
			BigDecimal finalCost = this.calculatePrice(promo);
			if (finalCost == null) { //invalid promoCode
				errors.add("error1", new ActionMessage("kwappl.invalid.promo", promoCode));
				//saveErrors(request, errors);			
				return mapping.findForward("payment_fail");	
			}
			
			ANetHandler paymentHandler = new ANetHandler();
			String retStatus = paymentHandler.handlePayment(cc, customer, finalCost, user.getKeyword());
			logger.debug("payment ret status: " + retStatus);

			user.setAmount(finalCost.doubleValue());
			user.setPaymentTxId(retStatus);
			//new UserDAOManager().saveUser(user);
			
			//create a ARB transaction as well
			//BigDecimal arbCost = this.calculateARBPrice();
			BigDecimal arbCost = finalCost;	//use the discounted promo price throughout - 5/30/12
			Integer freeMonths = (promo == null) ? null : promo.getFreeMonths();
			String arbRet = new ANetHandler().createARB(cc, customer, arbCost, user.getKeyword(), freeMonths);
			if (arbRet == null) {
				errors.add("error1", new ActionMessage("kwappl.payment_error", "Error in payment processing - Could not create ARB transaction"));
				//saveErrors(request, errors);			
				return mapping.findForward("payment_fail");
			}
			logger.debug("arb payment ret status: " + arbRet);
			user.setArbAmount(arbCost.doubleValue());
			user.setArbPaymentTxId(arbRet);
			
			kwAppl.setEmail(user.getEmail());
			String mobilePhone = new SMSDelivery().normalizePhoneNumber(mForm.getMobilePhone());
			kwAppl.setMobilePhone(mobilePhone);
			kwAppl.setKeyword(user.getKeyword());
			kwAppl.setPromoCode(mForm.getPromoCode());
			kwAppl.setCategoryId(user.getCategoryId());
			kwAppl.setStatus("P");
			kwAppl.setRepName(mForm.getRepName());
			kwAppl.setAcceptTerms(mForm.getAcceptTerms());
			kwAppl.setBusinessName(mForm.getBusinessName());

			//set the siteId
			kwAppl.setSiteId(siteId);
			
			kwDAO.save(kwAppl);
			
			List<TargetUserList> tuLists = new ArrayList<TargetUserList>();
			TargetUserList tuList = new TargetUserList();
			tuList.setListId(UUID.randomUUID().toString());
			tuList.setListName(user.getKeyword());
			tuLists.add(tuList);
			user.setTargetUserLists(tuLists);
			
			Properties props = PropertyUtil.load();

			/*
			//check to see if there is a keyword associated with this user's email
			List<User> users =  kwDAO.getKeywordByEmail(user.getEmail(), props.getProperty("shortcode"), 
															Integer.parseInt(props.getProperty("siteId")));
			if (! users.isEmpty()) { //there is at least one other keyword associated with this email, so get the userId
				logger.debug("existing userAccountNumber: " + users.get(0).getUserAccountNumber());
				user.setUserAccountNumber(users.get(0).getUserAccountNumber()); //use the existing userAccountNumber
				new UserDAOManager().saveUser(user);
				return mapping.findForward("all_done");
			}
			*/
			
			//create a userAccountNumber - use the email for now
			user.setUserAccountNumber(user.getEmail());		
			new UserDAOManager().saveUser(user);			
			logger.debug("userId: " + user.getUserId());
			
			//send the welcome email
        	Map<String, String> params = new HashMap<String, String>();
        	params.put("[KEYWORD]", kwAppl.getKeyword());
        	params.put("[BUSINESS NAME]", kwAppl.getBusinessName());
        	//params.put("[BUSINESS NAME]", user.getFirstName() + " " + user.getLastName());
        	EncryptDecrypt ed = new EncryptDecrypt();
        	String url = props.getProperty("baseURL") + "/sec?" + URLEncoder.encode(ed.encrypt("base"), "UTF-8") + "=";
        	String encString = ed.encrypt(props.getProperty("firstLoginUrl") + "?keyword=" + user.getKeyword()
        										+ "&userId=" + user.getUserId());
        	params.put("[FIRST LOGIN]", url + URLEncoder.encode(encString, "UTF-8"));
        	String welcomeEmailFile = props.getProperty("welcomeEmailFile");
        	String ccTo = props.getProperty("welcomeEmailCC");
			new SMSMain().sendEmailFromFile(user.getFirstName() + " " + user.getLastName(), user.getEmail(),
					"support@convergentmobile.com", "Welcome to US411 - " + kwAppl.getKeyword(), welcomeEmailFile, params, ccTo);
						
			request.getSession().setAttribute("User", user);
			
			//response.sendRedirect("./keyword/keyword_preview.php?mode=FT&keyword=" + user.getKeyword());
			
			return mapping.findForward("all_done");
		} catch (Exception e) {
			errors.add("error1", new ActionMessage("kwappl.payment_error", e.getMessage()));
			//saveErrors(request, errors);			
			return mapping.findForward("payment_fail");
		}		
	}
	
	//after payment is made and hotspot file has been created
	public ActionForward purchase(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {		
			errors.clear();

			KeywordForm mForm = (KeywordForm)form;
			String hotspotFile = (String)request.getParameter("imgFile");
			User user = (User)request.getSession().getAttribute("User");

			logger.debug("keyword " + user.getKeyword());
			logger.debug("hotspotFile = " + hotspotFile);
			
			List<TargetUserList> tuLists = new ArrayList<TargetUserList>();
			TargetUserList tuList = new TargetUserList();
			tuList.setListId(UUID.randomUUID().toString());
			tuList.setListName(user.getKeyword());
			tuLists.add(tuList);
			user.setTargetUserLists(tuLists);
			
			user.setHotspotFile(hotspotFile);
			
			new UserDAOManager().saveUser(user);
			
			logger.debug("userId: " + user.getUserId());
			
			//send the welcome email
			Properties props = PropertyUtil.load();
        	Map<String, String> params = new HashMap<String, String>();
        	params.put("[BUSINESS NAME]", user.getFirstName() + " " + user.getLastName());
        	EncryptDecrypt ed = new EncryptDecrypt();
        	String url = props.getProperty("baseURL") + "/sec?" + URLEncoder.encode(ed.encrypt("base"), "UTF-8") + "=";
        	String encString = ed.encrypt(props.getProperty("firstLoginUrl") + "?keyword=" + URLEncoder.encode(user.getKeyword(), "UTF-8")
        										+ "&userId=" + user.getUserId());
        	params.put("[FIRST LOGIN]", url + URLEncoder.encode(encString, "UTF-8"));
        	params.put("[LOGIN]", props.getProperty("baseURL"));
        	String welcomeEmailFile = props.getProperty("welcomeEmailFile");
        	String ccTo = props.getProperty("welcomeEmailCC");
			new SMSMain().sendEmailFromFile(user.getFirstName() + " " + user.getLastName(), user.getEmail(),
					props.getProperty("welcomeEmailSender"), "Welcome to US411", welcomeEmailFile, params, ccTo);
			
			//send alert to us411 rep
			String us411Msg = "Keyword " + user.getKeyword() + " purchased";
			new SMSMain().sendEmail(props.getProperty("us411AlertToName"), props.getProperty("us411AlertToEmail"), 
										props.getProperty("welcomeEmailSender"), "Keyword Purchase Alert", us411Msg, "HTML");
			
			return mapping.findForward("keyword_detail");
		} catch (Exception e) {
			errors.add("error1", new ActionMessage("kwappl.error", e));
			//saveErrors(request, errors);			
			e.printStackTrace();
			return mapping.findForward("keyword_detail");
		}
	}	

	//get all the keywords for a rep given a search string
	public ActionForward search(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {		
			logger.debug("In KeywordAction:search");
			
			errors.clear();

			KeywordForm mForm = (KeywordForm)form;
			KeywordApplication kwApplication = (KeywordApplication)request.getSession().getAttribute("kwAppl");
			
			String repMobilePhone = new SMSDelivery().normalizePhoneNumber(kwApplication.getRepMobilePhone());
			logger.debug("repMobilePhone: " + repMobilePhone);

			List<KeywordApplication> kwAppls = kwDAO.getAllKeywords(kwApplication.getRepName(), kwApplication.getRepEmail(), 
														repMobilePhone, PropertyUtil.load().getProperty("shortcode"), mForm.getKeywordSearchString());	
			if (kwAppls == null) {
				errors.add("error1", new ActionMessage("kwappl.nodata"));
				//saveErrors(request, errors);			
				kwAppls = new ArrayList<KeywordApplication>();	
			}
			request.getSession().setAttribute("kwAppls", kwAppls);
			request.getSession().setAttribute("kwAppl", kwApplication); //save this info for the back button
			
			return mapping.findForward("get");
		} catch (Exception e) {
			errors.add("error1", new ActionMessage("kwappl.error", e));
			//saveErrors(request, errors);			
			e.printStackTrace();
			return mapping.findForward("fail");
		}
	}
	
	//get all the business categories
	public ActionForward getBusCat(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {					
			errors.clear();

			KeywordForm mForm = (KeywordForm)form;

			List<LabelValueBean> busCategories = kwDAO.getBusCategories();	
			request.getSession().setAttribute("busCategories", busCategories);
			
			return mapping.findForward("busCatSearch");
		} catch (Exception e) {
			errors.add("error1", new ActionMessage("busCatSearch.error", e));
			//saveErrors(request, errors);			
			return mapping.findForward("failBusCatSearch");
		}
	}
	
	//get all the business categories given search string
	public ActionForward searchBusCat(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {					
			errors.clear();

			KeywordForm mForm = (KeywordForm)form;

			List<LabelValueBean> busCategories = kwDAO.getBusCategories(mForm.getBusCatSearchString());	
			request.getSession().setAttribute("busCategories", busCategories);
			
			return mapping.findForward("busCatSearch");
		} catch (Exception e) {
			errors.add("error1", new ActionMessage("busCatSearch.error", e));
			//saveErrors(request, errors);			
			return mapping.findForward("failBusCatSearch");
		}
	}	
	
	public BigDecimal calculatePrice(Promotion promo) throws Exception {
		Properties props = PropertyUtil.load();
		//props.load(this.getClass().getResourceAsStream("/MessageResources.properties"));
		
		BigDecimal setupCost = new BigDecimal(props.getProperty("kwappl.setupCost"));
		BigDecimal monthlyCost = new BigDecimal(props.getProperty("kwappl.monthlyCost"));
		BigDecimal months = new BigDecimal(props.getProperty("kwappl.minMonths"));
		BigDecimal h = new BigDecimal("100");
		
		monthlyCost = monthlyCost.multiply(months);
		
		if (promo == null)
			return setupCost.add(monthlyCost);
	
		//logger.debug("setup, monthly = " + setupCost + ", " + monthlyCost);
		//logger.debug("discountpercent: " + promo.getMonthlyDiscountPercent());
		
		if (promo.getSetupDiscountAmount() != null) {
			setupCost = setupCost.subtract(promo.getSetupDiscountAmount());
		}
		
		if (promo.getSetupDiscountPercent() != null) {
			setupCost = setupCost.subtract(setupCost.multiply(promo.getSetupDiscountPercent().divide(h)));
		}
		
		if (promo.getMonthlyDiscountAmount() != null) {
			monthlyCost = monthlyCost.subtract(promo.getMonthlyDiscountAmount().multiply(months));
		}
		
		if (promo.getMonthlyDiscountPercent() != null) {			
			monthlyCost = monthlyCost.subtract(monthlyCost.multiply(promo.getMonthlyDiscountPercent().divide(h)));
		}	
		
		return setupCost.add(monthlyCost).setScale(2, BigDecimal.ROUND_HALF_UP);		
	}
	
	public BigDecimal calculateARBPrice() throws Exception {
		Properties props = PropertyUtil.load();
		
		BigDecimal monthlyCost = new BigDecimal(props.getProperty("kwappl.monthlyCost"));
		BigDecimal months = new BigDecimal(props.getProperty("kwappl.minMonths"));
		
		return monthlyCost.multiply(months);
	}
}
