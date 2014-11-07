package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import keyword.KeywordApplication;
import keyword.KeywordDAOManager;
import liberty.CustomFields;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import reports.LTGenerateReportData;
import reports.LTReport;
import reports.ReportData;
import reports.ReportParams;
import service_impl.LTSCorpServiceImpl;
import service_impl.LTSMarketingServiceImpl;
import service_impl.LTSMessageServiceImpl;
import sms.SMSDelivery;
import sms.SMSMain;
import subclass.LTCategory_3;
import user.Campaign;
import user.CategoryBase;
import user.JobScheduler;
import user.RoleAction;
import user.TargetListData;
import user.TargetUserList;
import user.TargetUserListDao;
import user.User;
import user.UserDAOManager;
import util.InputDecoder;
import util.LTException;
import util.PropertyUtil;
import util.US411Exception;
import util.Utility;
import admin_user.UserProfileVO;
import dao.LTUserDAOManager;
import dao.LibertyAdminDAOManager;
import data.ApprovedMessage;
import data.LTUserForm;
import data.ValueObject;

@Controller
public class MarketingController {
	protected static final Logger logger = Logger.getLogger(MarketingController.class);
	
	protected LTSMarketingServiceImpl mktgService = new LTSMarketingServiceImpl();
	protected LTSCorpServiceImpl corpService = new LTSCorpServiceImpl();
	
	private String utz = PropertyUtil.load().getProperty("ServerTZ");
	private Integer siteId = Integer.parseInt(PropertyUtil.load().getProperty("siteId"));
	private Locale locale = Locale.US;
	
	@Autowired
	private LTUserDAOManager dao;
	
	private ResourceBundle bundle = ResourceBundle.getBundle("MessageResources");
	//private ApplicationContext appCtx = new ClassPathXmlApplicationContext("application_context.xml");
    
	 @Autowired
	 private MessageSource message;
	 
	@ModelAttribute("officeId")
	@Cacheable("officeId")
	public String getOfficeId(Long userId) throws Exception {
		if (userId == null)
			userId = new Utility().getUserFromSecurityContext().getUserId();

		List<UserProfileVO> sites = mktgService.getSites(userId);
		if (sites == null)
			return null;
		
		return sites.get(0).getCustomField2().toString();
	}
	
	@ModelAttribute("entityId")
	@Cacheable("entityId")
	public String getEntityId(Long userId) throws Exception {
		if (userId == null)
			userId = new Utility().getUserFromSecurityContext().getUserId();

		List<UserProfileVO> sites = mktgService.getSites(userId);
		if (sites == null)
			return null;
		
		for (UserProfileVO site : sites)
			if (site.getCustomField2().equals("Entity") || site.getCustomField2().equals("AD"))
				return site.getCustomField1();		
		
		return null;
	}
	
	//get the Office or Entity Id given a userId
	@Cacheable("officeId")
	public String getEntOfficeId(Long userId) throws Exception {
		if (userId == null)
			userId = new Utility().getUserFromSecurityContext().getUserId();

		List<UserProfileVO> sites = mktgService.getSites(userId);
		if (sites == null)
			return null;
		
		for (UserProfileVO site : sites)
			if (site.getUserId().equals(userId))
					return site.getCustomField2();
		
		return null;
	}
	
	 @ModelAttribute("reports")
	 public List<LTReport> getAllReports() throws Exception {
		return mktgService.getReports();
	 }
	 
	 @ModelAttribute("sites")
	 @Cacheable("sites")
	 public List<UserProfileVO> getSites() throws Exception {
		Long userId = new Utility().getUserFromSecurityContext().getUserId();
		
		User user = new Utility().getUserFromSecurityContext();
		if (user.getRoleActions().get(0).getRoleType().equals("AD")) {
			return mktgService.getSitesAD(userId);
		}
		
		//return mktgService.getAllSites(userId);
		return mktgService.getSites(userId);
	 }
	 
	//get keyword for a given officeId
	public String getKeyword(Long userId, String officeId) throws Exception {
		List<UserProfileVO> sites = mktgService.getSites(userId);
		if (sites == null)
			return null;
		
		if (officeId == null) { //entity
			for (UserProfileVO site : sites) {
				if (site.getUserId().equals(userId))
					return site.getKeyword();
			}	
		}
		
		for (UserProfileVO site : sites) {
			if (site.getCustomField2().equals(officeId))
				return site.getKeyword();
		}
		
		return null;
	}
	
	//get userId for a given keyword
	public Long getUserId(String keyword) throws Exception {
		List<UserProfileVO> sites = this.getSites();
		if (sites == null)
			return null;
		
		for (UserProfileVO site : sites) {
			if (site.getKeyword().equals(keyword))
				return site.getUserId();
		}
		
		return null;
	}
	
	@RequestMapping(value = "/cmtoolbox", method = RequestMethod.GET)
	public ModelAndView cmtoolbox(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) {
		User user = new Utility().getUserFromSecurityContext();
		ltUser.setUser(user);
		
		return  new ModelAndView("cmtoolbox", "ltUser", ltUser);
	}
	
	@RequestMapping(value = "/dashboardOffice", method = RequestMethod.GET)
	public ModelAndView dashboardOffice(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) {
		ModelAndView errorMV = new ModelAndView("error", "ltUser", ltUser);
		ModelAndView mv = new ModelAndView("dashboard_office", "ltUser", ltUser);
		
		User user = new Utility().getUserFromSecurityContext();
		ltUser.setUser(user);			

		try {
			List<UserProfileVO> sites = mktgService.getAllSites(user.getUserId());
			
			if (sites == null) {
				logger.error("No sites found");
				errorMV.addObject("error", "No sites found");
				return errorMV;			
			}			
			
			ltUser.setSites(sites);
			
			user = dao.login(user.getUserId(), 1);
			ltUser.getUser().setTargetUserLists(user.getTargetUserLists());
						
			CustomFields cfields = dao.getCustomFields(user.getUserId());
			String loc = cfields.getLocation() != null ? cfields.getLocation() : "US";
			user.setBillingCountry(loc);
			
			//set CA states for CA zees/offices
			if (user != null && user.getBillingCountry().equals("CA")) {
				List<ValueObject> state_codes = dao.getCAStatesLT();
				request.getSession().setAttribute("state_codes", state_codes);
				ltUser.setApprovedMsgs(mktgService.getCorporateMessagesCA(siteId));
			} else {			
				ltUser.setApprovedMsgs(mktgService.getCorporateMessages(siteId));
			}
			
			ltUser.setCustomMsgs(mktgService.approvedMsgsFromDate(user.getUserId(), "Off", "A"));	
			
			LTCategory_3 catg = mktgService.getProfile(user.getUserId());
			if (catg == null) {
				logger.error("No profile found for userId: " + user.getUserId());
			}
			
			ltUser.setCategory(catg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mv;
	}
	
	@RequestMapping(value = "/dashboardEntityD", method = RequestMethod.GET)
	public ModelAndView dashboardEntityD(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) {
		return  new ModelAndView("/WEB-INF/view/entity/dashboard-entity.html", "ltUser", ltUser);
	}
	
	@RequestMapping(value = "/dashboardEntity", method = RequestMethod.GET)
	public ModelAndView dashboardEntity(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) {
		ModelAndView errorMV = new ModelAndView("error", "ltUser", ltUser);
		ModelAndView mv = new ModelAndView("dashboard_entity", "ltUser", ltUser);
		
		User user = new Utility().getUserFromSecurityContext();

		try {
			logger.debug("dashboardEntity start 1: " + Calendar.getInstance().getTimeInMillis());
			List<UserProfileVO> sites = mktgService.getAllSites(user.getUserId(), ltUser.getSortColumn(), ltUser.getSortOrder());
			logger.debug("dashboardEntity end 1: " + Calendar.getInstance().getTimeInMillis());
			
			if (sites == null) {
				logger.error("No sites found");
				errorMV.addObject("error", "No sites found");
				return errorMV;			
			}			
			
			ltUser.setSites(sites);
			
			ltUser.setUser(user);			
			
			logger.debug("dashboardEntity start 2: " + Calendar.getInstance().getTimeInMillis());
			CustomFields cfields = dao.getCustomFields(user.getUserId());
			String loc = cfields.getLocation() != null ? cfields.getLocation() : "US";
			user.setBillingCountry(loc);
			logger.debug("dashboardEntity end 2: " + Calendar.getInstance().getTimeInMillis());

			logger.debug("dashboardEntity start 3: " + Calendar.getInstance().getTimeInMillis());
			//set CA states for CA zees/offices
			/*
			if (user != null && user.getBillingCountry().equals("CA")) {
				List<ValueObject> state_codes = dao.getCAStatesLT();
				request.getSession().setAttribute("state_codes", state_codes);
				ltUser.setApprovedMsgs(mktgService.getCorporateMessagesCA(siteId));
			} else {			
				ltUser.setApprovedMsgs(mktgService.getCorporateMessages(siteId));
			}
			*/
			logger.debug("dashboardEntity end 3: " + Calendar.getInstance().getTimeInMillis());

			//ltUser.setCustomMsgs(mktgService.getCustomMessages(siteId, this.getOfficeId(user.getUserId())));
			
			logger.debug("dashboardEntity start 4: " + Calendar.getInstance().getTimeInMillis());
			
			//get the info for the first office
			LTCategory_3 catg = mktgService.getProfile(ltUser.getSites().get(0).getUserId());
			if (catg == null) {
				logger.error("No profile found for userId: " + user.getUserId());
			}
			logger.debug("dashboardEntity end 4: " + Calendar.getInstance().getTimeInMillis());

			ltUser.setCategory(catg);
			
			//request.getSession().setAttribute("keyword", ltUser.getSites().get(0).getKeyword());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mv;
	}
	
	@RequestMapping(value = "/dashboardAD", method = RequestMethod.GET)
	public ModelAndView dashboardAD(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) {
		ModelAndView errorMV = new ModelAndView("error", "ltUser", ltUser);
		ModelAndView mv = new ModelAndView("dashboard_ad", "ltUser", ltUser);
		
		User user = new Utility().getUserFromSecurityContext();
		ltUser.setUser(user);			

		try {
			List<UserProfileVO> sites = mktgService.getAllSitesAD(user.getUserId());
			
			if (sites == null) {
				logger.error("No sites found");
				errorMV.addObject("error", "No sites found");
				return errorMV;			
			}			
			
			ltUser.setSites(sites);
			
			user = dao.login(user.getUserId(), 1);
			ltUser.getUser().setTargetUserLists(user.getTargetUserLists());
						
			CustomFields cfields = dao.getCustomFields(user.getUserId());
			String loc = cfields.getLocation() != null ? cfields.getLocation() : "US";
			user.setBillingCountry(loc);
			
			//set CA states for CA zees/offices
			if (user != null && user.getBillingCountry().equals("CA")) {
				List<ValueObject> state_codes = dao.getCAStatesLT();
				request.getSession().setAttribute("state_codes", state_codes);
				ltUser.setApprovedMsgs(mktgService.getCorporateMessagesCA(siteId));
			} else {			
				ltUser.setApprovedMsgs(mktgService.getCorporateMessages(siteId));
			}
			
			ltUser.setCustomMsgs(mktgService.approvedMsgsFromDate(user.getUserId(), "Off", "A"));	
			
			LTCategory_3 catg = mktgService.getProfile(user.getUserId());
			if (catg == null) {
				logger.error("No profile found for userId: " + user.getUserId());
				catg = new LTCategory_3();
				catg.setUserId(user.getUserId());
			}
			
			ltUser.setCategory(catg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mv;
	}
	
	@RequestMapping(value = "/dashboardCorp", method = RequestMethod.GET)
	public ModelAndView dashboardCorp(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) {
		ModelAndView errorMV = new ModelAndView("error", "ltUser", ltUser);
		ModelAndView mv = new ModelAndView("dashboard_corp", "ltUser", ltUser);
		
		logger.debug("dashboardCorp start 1: " + Calendar.getInstance().getTimeInMillis());
		
		User user = new Utility().getUserFromSecurityContext();
		logger.debug("dashboardCorp end 1: " + Calendar.getInstance().getTimeInMillis());

		logger.debug("dashboardCorp start 2: " + Calendar.getInstance().getTimeInMillis());

		try {
			List<UserProfileVO> sites = corpService.getAllSites(ltUser.getSortColumn(), ltUser.getSortOrder());
			logger.debug("dashboardCorp end 2-0: " + Calendar.getInstance().getTimeInMillis());
		
			if (sites == null) {
				logger.error("No sites found");
				errorMV.addObject("error", "No sites found");
				return errorMV;			
			}			
			
			ltUser.setSites(sites);
			ltUser.setUser(user);			
			logger.debug("dashboardCorp end 2: " + Calendar.getInstance().getTimeInMillis());
			
			//CustomFields cfields = dao.getCustomFields(user.getUserId());
			//String loc = cfields.getLocation() != null ? cfields.getLocation() : "US";
			//user.setBillingCountry(loc);
			
			logger.debug("dashboardCorp start 3: " + Calendar.getInstance().getTimeInMillis());

			//List<ApprovedMessage> msgsPending = mktgService.approvedMsgsFromDate(null, "Corp", "P"); //show all the Pending msgs
			//ltUser.setPendingMsgs(msgsPending);
			logger.debug("dashboardCorp end 3: " + Calendar.getInstance().getTimeInMillis());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.debug("dashboardCorp end: " + Calendar.getInstance().getTimeInMillis());

		return mv;
	}
	
	/*
	@RequestMapping(value = "/getProfile", method = RequestMethod.GET)
	public ModelAndView getProfile(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView("/WEB-INF/view/step_3.jsp", "ltUser", ltUser);
		ModelAndView errorMV = new ModelAndView("error", "ltUser", ltUser);

		User user = new Utility().getUserFromSecurityContext();
		Long userId = user.getUserId();
		
		if (request.getParameter("userId") != null && request.getParameter("userId").length() > 0) // will be null for Office role
			userId = Long.valueOf(request.getParameter("userId"));

		LTCategory_3 catg = mktgService.getProfile(userId);
		if (catg == null) {
			logger.error("No profile found for userId: " + user.getUserId());
			return errorMV;
		}
		
		ltUser.setCategory(catg);
		
		return mv;
	}
	*/
	
	//return the profile info as data
	@RequestMapping(value = "/getProfileInfo", method = RequestMethod.GET)
	public @ResponseBody LTCategory_3 getProfileInfo(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView("profile", "ltUser", ltUser);
		ModelAndView errorMV = new ModelAndView("error", "ltUser", ltUser);

		User user = new Utility().getUserFromSecurityContext();
		ltUser.setSites(mktgService.getSites(user.getUserId()));

		if (request.getParameter("userId") == null || request.getParameter("userId").length() <= 0)  {
			return null;
		}
			
		Long userId = Long.valueOf(request.getParameter("userId"));

		LTCategory_3 catg = mktgService.getProfile(userId);
		if (catg == null) {
			logger.error("No profile found for userId: " + user.getUserId());		
			return null;
		}
		
		ltUser.setCategory(catg);
		
		return catg;
	}
	
	@RequestMapping(value = "/getProfile", method = RequestMethod.GET)
	public ModelAndView getProfile(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView("profile", "ltUser", ltUser);
		ModelAndView errorMV = new ModelAndView("error", "ltUser", ltUser);

		User user = new Utility().getUserFromSecurityContext();		
		ltUser.setUser(user);

		List<UserProfileVO> sites = null;

		if (user.getRoleActions().get(0).getRoleType().equals("Corporate"))
			sites = corpService.getAllSites();
		else
			sites = mktgService.getSites(user.getUserId());
		
		ltUser.setSites(sites);

		if (ltUser.getSites() == null) {
			logger.error("No sites found");
			mv.addObject("error", message.getMessage("error.nosites", new Object[] {}, locale));
			return mv;
		}
		
		Long userId = null;
		
		if (request.getParameter("userId") == null || request.getParameter("userId").length() <= 0)  {
			//return mv;
			userId = sites.get(0).getUserId(); //use the first one
		} else
			userId = Long.valueOf(request.getParameter("userId"));
		
		ltUser.setSearchOfficeIdString(userId.toString());

		LTCategory_3 catg = mktgService.getProfile(userId);
		if (catg == null) {
			logger.error("No profile found for userId: " + userId);
			mv.addObject("error", message.getMessage("error.noprofile", new Object[] {userId.toString()}, locale));
			return mv;
		}
		
		ltUser.setCategory(catg);
		
		return mv;
	}
	
	@RequestMapping(value = "/getList", method = RequestMethod.GET)
	public @ResponseBody List<TargetUserList> getList(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView("/WEB-INF/view/entity/send_message.jsp", "ltUser", ltUser);

		User user = new Utility().getUserFromSecurityContext();
		List<String> officeIds = ltUser.getOfficeIds();
		List<TargetUserList> tuList = null;

		//Check if the list contains Entity
		if (officeIds.contains("Entity")) {
			officeIds.remove("Entity");
			if (officeIds.isEmpty()) {
				officeIds.add("0"); //dummy value
			}
			tuList = dao.getList(officeIds, this.getEntityId(user.getUserId()));
		} else {
			if (officeIds.isEmpty()) {
				officeIds.add("0"); //dummy value
			}
			tuList = dao.getList(officeIds);
		}
		
		if (tuList == null) {
			logger.error("No lists found for userId: " + user.getUserId());
		}
				
		return tuList;
	}
	
	@RequestMapping(value = "/saveProfile", method = RequestMethod.POST)
	public ModelAndView saveProfile(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView("profile", "ltUser", ltUser);

		User user = new Utility().getUserFromSecurityContext();
		
		//LTCategory_3 catg = mktgService.getProfile(user.getUserId());
		LTCategory_3 catg = mktgService.getProfile(Long.valueOf(ltUser.getSearchOfficeIdString()));		
		if (catg == null) {
			logger.error("No profile found for userId: " + user.getUserId());
		}
		
		LTCategory_3 formCatg = ltUser.getCategory();
		/*
		catg.setBusinessName(formCatg.getBusinessName());
		catg.setAddress(formCatg.getAddress());
		catg.setCity(formCatg.getCity());
		catg.setState(formCatg.getState());
		catg.setZip(formCatg.getZip());
		catg.setPhone(formCatg.getPhone());
		*/
		
		//You can only save the timezone field - everything else is read-only in the Mobile Profile
		catg.setTimezone(formCatg.getTimezone());
		
		dao.saveDetails(catg);
		
		ltUser.setCategory(catg);
		
		return mv;
	}
		
	@RequestMapping(value = "/createHotspot", method = RequestMethod.GET)
	public String createHotspot(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request, HttpServletResponse response) {
		User user = new Utility().getUserFromSecurityContext();
		
		KeywordApplication kw = null;
		//Long userId = ltUser.getUserId();
		
		Long userId = user.getUserId();;
		String uid = request.getParameter("userId");
		if (uid != null && uid.length() > 0) { //will be null the first time if user has not clicked on any keyword
			userId = Long.valueOf(uid);
		}
		
		try {
			//check if this userId belongs to this user
			Boolean belongs = false;
			for (UserProfileVO site : this.getSites())
				if (site.getUserId().equals(userId)) {
					belongs = true;
					break;
				}
			
			if (! belongs) {
				logger.error("userId: " + userId + " is not valid");
				return "redirect:./keyword/keyword_preview.php?mode=FT";
			}
			
			kw = mktgService.getKeywordByUserId(userId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (kw == null) {
			logger.error("No keyword found");
		}

		ltUser.setSearchKeywordString(kw.getKeyword());
		
		//return "redirect:./keyword/keyword_preview.php?mode=FT&keyword=" + request.getParameter("keyword");
		Cookie cookie = new Cookie("keyword", kw.getKeyword());
		response.addCookie(cookie);
		return "redirect:./keyword/keyword_preview.php?mode=FT";

		//return "redirect:./keyword/keyword_preview.php?mode=FT&keyword=" + kw.getKeyword();
		
		//return new ModelAndView("/keyword/keyword_preview.jsp", "ltUser", ltUser);
	}
	
	@RequestMapping(value = "/customMessage", method = RequestMethod.GET)
	public ModelAndView customMessage(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) {
		User user = new Utility().getUserFromSecurityContext();
		ModelAndView errorMV = new ModelAndView("error", "ltUser", ltUser);
		ModelAndView mv = new ModelAndView("customMessage", "ltUser", ltUser);
		Integer daysBack = Integer.parseInt(PropertyUtil.load().getProperty("daysBack"));

		ltUser.setUser(user);
		
		try {
			String officeId = this.getOfficeId(user.getUserId());
			if (officeId == null) {
				logger.error("No keyword found");
				mv.addObject("error", message.getMessage("error.keywordNotFound", new Object[] {}, locale));								
				//return mv;
			}			
			List<ApprovedMessage> msgs = mktgService.approvedMsgsFromDate(user.getUserId(), "Off", "A", daysBack);
			List<ApprovedMessage> msgsPending = mktgService.approvedMsgsFromDate(user.getUserId(), "Off", "P"); //show all the Pending msgs
			List<ApprovedMessage> msgsRejected = mktgService.approvedMsgsFromDate(user.getUserId(), "Off", "R", daysBack);

			ltUser.setApprovedMsgs(msgs);
			ltUser.setPendingMsgs(msgsPending);
			ltUser.setCustomMsgs(msgsRejected);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mv;
	}
	
	@RequestMapping(value = "/customMessageEntity", method = RequestMethod.GET)
	public ModelAndView customMessageEntity(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) {
		User user = new Utility().getUserFromSecurityContext();
		ModelAndView errorMV = new ModelAndView("error", "ltUser", ltUser);
		ModelAndView mv = new ModelAndView("customMessageEntity", "ltUser", ltUser);
		
		Integer daysBack = Integer.parseInt(PropertyUtil.load().getProperty("daysBack"));

		try {
			/*
			List<UserProfileVO> sites = mktgService.getSites(user.getUserId());

			if (sites == null) {
				logger.error("No keyword found");
				return errorMV;
			}			
			ltUser.setSites(sites);
			*/
			
			/*
			if (ltUser.getSites() == null) {
				logger.error("No keyword found");
				mv.addObject("error", message.getMessage("error.keywordNotFound", new Object[] {}, locale));								
				//return mv;
			}	
			*/
			
			ltUser.setUser(user);
			
			List<ApprovedMessage> msgs = mktgService.approvedMsgsFromDate(user.getUserId(), "Ent", "A", daysBack);
			List<ApprovedMessage> msgsPending = mktgService.approvedMsgsFromDate(user.getUserId(), "Ent", "P");
			List<ApprovedMessage> msgsRejected = mktgService.approvedMsgsFromDate(user.getUserId(), "Ent", "R", daysBack);

			ltUser.setApprovedMsgs(msgs);
			ltUser.setPendingMsgs(msgsPending);
			ltUser.setCustomMsgs(msgsRejected);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mv;
	}
	
	@RequestMapping(value = "/customMessageCorp", method = RequestMethod.GET)
	public ModelAndView customMessageCorp(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) {
		User user = new Utility().getUserFromSecurityContext();
		ModelAndView errorMV = new ModelAndView("error", "ltUser", ltUser);
		ModelAndView mv = new ModelAndView("customMessageCorp", "ltUser", ltUser);
		
		Integer daysBack = Integer.parseInt(PropertyUtil.load().getProperty("daysBack"));

		try {
			/*
			List<UserProfileVO> sites = corpService.getAllSites();

			if (sites == null) {
				logger.error("No keyword found");
				return errorMV;
			}			
			ltUser.setSites(sites);
			*/
			
			ltUser.setUser(user);
			
			List<ApprovedMessage> msgs = corpService.approvedMsgsFromDate("A", daysBack);
			List<ApprovedMessage> msgsPending = corpService.approvedMsgsFromDate("P");
			List<ApprovedMessage> msgsRejected = corpService.approvedMsgsFromDate("R", daysBack);

			ltUser.setApprovedMsgs(msgs);
			ltUser.setPendingMsgs(msgsPending);
			ltUser.setCustomMsgs(msgsRejected);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mv;
	}
	
	@RequestMapping(value = "/messageForApproval", method = RequestMethod.GET)
	public ModelAndView messageForApproval(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("/WEB-INF/view/corp/custom_message_body.jsp", "ltUser", ltUser);
		
		try {
			Integer msgId = Integer.valueOf(request.getParameter("msgId"));		
			ApprovedMessage aMsg = corpService.getCustomMsgById(msgId);
			ltUser.setaMsg(aMsg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mv;
	}
	
	//handles Approve and Reject
	@RequestMapping(value = "/approveMessage", method = RequestMethod.POST)
	public ModelAndView approveMessage(@ModelAttribute("ltUser") LTUserForm ltUser, 
								@RequestParam(required = false) String reject, HttpServletRequest request) {
		User user = new Utility().getUserFromSecurityContext();

		ModelAndView mv = new ModelAndView("customMessageCorp", "ltUser", ltUser);
		Integer daysBack = Integer.parseInt(PropertyUtil.load().getProperty("daysBack"));

		try {
			Integer msgId = Integer.valueOf(request.getParameter("msgId"));		
			ApprovedMessage aMsg = corpService.getCustomMsgById(msgId);
			if (reject != null && reject.equals("R")) {
				aMsg.setStatus("R");
				aMsg.setComments(request.getParameter("comment"));
			} else
				aMsg.setStatus("A");
			
			aMsg.setUpdated(Calendar.getInstance().getTime());
			
			new LibertyAdminDAOManager().saveObject(aMsg);

			//get the user info for this msg to send out the email
			User adUser = new UserDAOManager().login(aMsg.getUserId());
			if (adUser == null) {
				logger.error("Could not find user with userId: " + aMsg.getUserId());
			} else {
				String emailText = "Your message <br/>" + aMsg.getMessageText() + "<br/> has been ";

				if (aMsg.getStatus().equals("A")) {
					emailText += " approved";
				}
				if (aMsg.getStatus().equals("R")) {
					emailText += " rejected with the following comments: ";
					emailText += aMsg.getComments();
				}				
				new SMSMain().sendEmail(adUser.getFirstName() + " " + adUser.getLastName(), adUser.getEmail(),
					"support@convergentmobile.com", "Msg Approval ", emailText, "HTML", null);
			}
			
			//mv.addObject("message", "Message approved");
			
			ltUser.setUser(user);
			
			List<ApprovedMessage> msgs = corpService.approvedMsgsFromDate("A", daysBack);
			List<ApprovedMessage> msgsPending = corpService.approvedMsgsFromDate("P");
			List<ApprovedMessage> msgsRejected = corpService.approvedMsgsFromDate("R", daysBack);

			ltUser.setApprovedMsgs(msgs);
			ltUser.setPendingMsgs(msgsPending);
			ltUser.setCustomMsgs(msgsRejected);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mv;
	}
	
	//For an entity, create the custom msgs for at the Entity level so that it accessible to all their offices
	//The entity_id field in approved_message will contain the entityId for an entity and officeId for an office
	@RequestMapping(value = "/createCustomMessage", method = RequestMethod.POST)
	public @ResponseBody String createCustomMessage(@ModelAttribute("ltUser") LTUserForm ltUser, BindingResult bindingResult, HttpServletRequest request) {
		User user = new Utility().getUserFromSecurityContext();

		logger.debug("msg: " + ltUser.getSendSearchCityString());
		Long userId = user.getUserId();
				
		String type = "Off";
		ApprovedMessage aMsg = new ApprovedMessage();
		
		try {
			aMsg.setSiteId(siteId);
			aMsg.setMessageText(ltUser.getSendSearchCityString());
			aMsg.setLanguage("EN");
			aMsg.setStatus("P");
			aMsg.setCreated(Calendar.getInstance().getTime());
			
			CustomFields cfields = mktgService.getCustomFields(userId);
			String loc = cfields.getLocation() != null ? cfields.getLocation() : "US";
			aMsg.setLocation(loc);
			
			//check if it was for an Office or Entity
			//we should never have Corporate here as that is a different path
			if (user.getRoleActions().get(0).getRoleType().equals("Office")) {
				aMsg.setOfficeId(this.getOfficeId(userId));
				aMsg.setUserId(userId);			
			}
			
			if (user.getRoleActions().get(0).getRoleType().equals("Entity")) {
				Long uid = Long.valueOf(ltUser.getSearchOfficeIdString());
				String entityId = this.getEntityId(uid);
				if (entityId != null)
					aMsg.setEntityId(entityId);
				else
					aMsg.setOfficeId(this.getOfficeId(uid));
				
				aMsg.setUserId(uid);
				type = "Ent";
			}
			
			if (user.getRoleActions().get(0).getRoleType().equals("AD")) {
				Long uid = Long.valueOf(ltUser.getSearchOfficeIdString());
				String entityId = this.getEntityId(uid);
				aMsg.setEntityId(entityId);			
				aMsg.setUserId(uid);
				type = "Ent";
			}

			/*
			if (! user.getRoleActions().get(0).getRoleType().equals("Corporate")) {
				Long uid = Long.valueOf(ltUser.getSearchOfficeIdString());
				String entityId = this.getEntityId(uid);
				if (entityId != null)
					aMsg.setEntityId(entityId);
				else
					aMsg.setOfficeId(this.getOfficeId(uid));
				
				aMsg.setUserId(uid);	//set it to the selected Office or Entity userId			
			}
			*/

			new LibertyAdminDAOManager().saveObject(aMsg);
			
			//ltUser.setPendingMsgs(mktgService.getPendingMessages(this.getOfficeId(userId)));	
			
			ltUser.setPendingMsgs(mktgService.approvedMsgsFromDate(user.getUserId(), type, "P"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String approverEmail = PropertyUtil.load().getProperty("textApproverEmail");
		if (approverEmail == null) {
			logger.error("No email found for text approver");
			return bundle.getString("error.null.approver");
		}
		
		//String emailBody = "You have a custom message created by " + user.getUserAccountNumber() + " for approval\n" + ltUser.getSendSearchCityString();
		String emailBody = "You have a custom message for approval:<br/>" + ltUser.getSendSearchCityString();
		emailBody += "<br/>https://www.libertytax.net/_layouts/uribuilder/urihandler.ashx?Application=US411Connect";

		try {
			new SMSMain().sendEmail(user.getUserAccountNumber(), approverEmail,
					"support@convergentmobile.com", "Custom Msg Approval ",
					emailBody, "HTML", null);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return bundle.getString("error.admsg.approval");
	}

	@RequestMapping(value = "/confirmationMessage", method = RequestMethod.GET)
	public ModelAndView confirmationMessage(@ModelAttribute("ltUser") LTUserForm ltUser, 
								@RequestParam(required = false) Long userId, @RequestParam(required = false) String officeId,
								HttpServletRequest request) {
		return this.confirmationMessageP(ltUser, userId, officeId, request);
	}
	
	//You will be able to set these only for keywords with status = P
	@RequestMapping(value = "/confirmationMessage", method = RequestMethod.POST)
	public ModelAndView confirmationMessageP(@ModelAttribute("ltUser") LTUserForm ltUser, 
								@RequestParam(required = false) Long userId, @RequestParam(required = false) String officeId,
								HttpServletRequest request) {
		User user = new Utility().getUserFromSecurityContext();
		ModelAndView errorMV = new ModelAndView("error", "ltUser", ltUser);
		ModelAndView mv = new ModelAndView("confirmationMessage", "ltUser", ltUser);
		
		try {
			ltUser.setUser(user);	
			
			if (ltUser.getSearchOfficeIdString() != null && ltUser.getSearchOfficeIdString().length() > 0)
				userId = Long.valueOf(ltUser.getSearchOfficeIdString());
			
			if (userId == null)
				userId = user.getUserId();
			else //set the officeId
				ltUser.setSearchOfficeIdString(userId.toString());
			
			LTCategory_3 catg = new LTSMarketingServiceImpl().getProfile(userId);
			
			/*
			if (catg == null) {
				logger.error("No profile found for userId: " + user.getUserId());
				errorMV = this.startingMV(ltUser);
				errorMV.addObject("error", message.getMessage("error.entityKeyword.notset", new Object[] {}, locale));				
				return errorMV;
			}
			*/
			ltUser.setCategory(catg);
			
			List<UserProfileVO> sites = null;
			
			if (user.getRoleActions().get(0).getRoleType().equals("Corporate")) {
				sites = corpService.getAllSites();
			} else {
				//sites = this.getSites(); //This is populated via model attribute. Use ${sites} in jsp instead of ${ltUser.sites}
			}
			
			//ltUser.setSites(sites);
			
			ltUser.setApprovedMsgs(mktgService.getCorporateMessages(siteId));
			
			//get the custom approved msgs based on office or entity
			if (officeId != null) { //will be null when called from the dashboard menu as no Office would have been selected
				String entType = "Off";
				if (officeId.equals("Entity"))
					entType = "Ent";
				
				ltUser.setCustomMsgs(mktgService.approvedMsgsFromDate(userId, entType, "A"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mv;
	}
	
	@RequestMapping(value = "/saveConfirmationMessage", method = RequestMethod.POST)
	public @ResponseBody String saveConfirmationMessage(@ModelAttribute("ltUser") LTUserForm ltUser, 
							HttpServletRequest request) {
		User user = new Utility().getUserFromSecurityContext();
		
		try {
			ltUser.setUser(user);	

			Long userId = Long.valueOf(ltUser.getSearchOfficeIdString());
			
			LTCategory_3 catg = new LTSMarketingServiceImpl().getProfile(userId);
			if (catg == null) {
				logger.error("No profile found");
				return "Error saving message: No profile found";
			}
			
			if (ltUser.getCurrentPage().equals("ini")) {
				catg.setInitialMessage(ltUser.getCategory().getInitialMessage());
				catg.setIncludePhoneIni(ltUser.getCategory().getIncludePhoneIni());
			}
			if (ltUser.getCurrentPage().equals("rpt")) {	
				catg.setAutoResponse(ltUser.getCategory().getAutoResponse());
				catg.setIncludePhoneRpt(ltUser.getCategory().getIncludePhoneRpt());
			}
			
			mktgService.saveCategory(catg);
			return "Message Saved";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Error saving message: " + e.getMessage();
		}
	}
	
	@RequestMapping(value = "/loginSSO", method = RequestMethod.GET)
	public String loginSSO(@ModelAttribute("ltUser") LTUserForm ltUser, @RequestParam("role") String roleP,
								@RequestParam("id") String idP, @RequestParam("fingerprint") String fp,
								BindingResult bindingResult, HttpServletRequest request,
								final RedirectAttributes redirectAttributes) {	
		ModelAndView errorMV = new ModelAndView("error", "ltUser", ltUser);
		ModelAndView dashView = null;
		
		User user = new Utility().getUserFromSecurityContext();
		try {
			logger.debug("role: " + user.getSecRole());
			List<RoleAction> roleActions = user.getRoleActions();
		
			if (roleActions.get(0).getRoleType().equals("Office")) {
				dashView = new ModelAndView("dashboard_office", "ltUser", ltUser);
				return "dashboardOffice";
			}
			if (roleActions.get(0).getRoleType().equals("Entity")) {
				dashView = new ModelAndView("dashboard_entity", "ltUser", ltUser);
				return "dashboardEntity";
			}		
			if (roleActions.get(0).getRoleType().equals("AD")) {
				dashView = new ModelAndView("dashboard_ad", "ltUser", ltUser);
				return "dashboardAD";
			}				
			if (roleActions.get(0).getRoleType().equals("Corporate")) {
				dashView = new ModelAndView("dashboard_corp", "ltUser", ltUser);
				return "dashboardCorp";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	@RequestMapping(value = "/loginSSO1", method = RequestMethod.GET)
	public String loginSSO1(@ModelAttribute("ltUser") LTUserForm ltUser, @RequestParam("role") String roleP,
								@RequestParam("id") String idP, @RequestParam("fingerprint") String fp,
								BindingResult bindingResult, HttpServletRequest request,
								final RedirectAttributes redirectAttributes) {		
		ModelAndView errorMV = new ModelAndView("error", "ltUser", ltUser);
		ModelAndView dashView = null;
		
		User user = new Utility().getUserFromSecurityContext();
		
		if (user == null) {
			logger.error("Null user");
		}
		
		try {
			InputDecoder inpDec = new InputDecoder();
			
			String role = URLDecoder.decode(inpDec.getPlainText(request.getParameter("role")), "UTF-8");
			String eId = inpDec.getPlainText(request.getParameter("id"));

			if (! inpDec.fpMatch(role, eId, fp)) {
				logger.error("FP mismatch");
				throw new Exception("FP mismatch");
			}
			
			logger.debug("login role, eId, siteid = " + role + ", " + eId + ", " + siteId);
		
			//get the role actions for this user
			List<RoleAction> roleActions = dao.getRoleActions(role);
			if (roleActions == null || roleActions.size() == 0) {
				errorMV.addObject("error", "No role actions found");
				return "error";
			}
			
			//save this for logout
			request.getSession().setAttribute("userRole", role);
						
			//check if this is a Corporate user
			if (roleActions.get(0).getRoleType().equals("Corporate")) {
				user = new User();
				user.setUserId(0L); //set this so that ReportAction.setup does not barf
				dashView = new ModelAndView("dashboard_corp", "ltUser", ltUser);
			} else {		
				//user = new LTUserDAOManager().loginLT(role, eId); //user hierarchy is set in loginLT
				user = new LTUserDAOManager().loginLT(roleActions.get(0).getRoleType(), eId);				
			}

			//set the siteId
			user.setSiteId(siteId);
			logger.debug("siteId = " + siteId);	
			
			user.setUserAccountNumber(eId); //set this to the id we get from LT
					
			//create the keyword directory if it does not exist
			//this is to avoid a race condition in the preview popup
			String outDir = PropertyUtil.load().getProperty("xhtmlFilePath") + "/" + user.getKeyword();			
			if (! new File(outDir).exists() && ! new File(outDir).mkdir()) {
				logger.error("Could not create dir: " + outDir);
				return "error";			
			}
			
			user.setRoleActions(roleActions);
			user.setSecRole(roleActions.get(0).getRoleType());
			
			logger.debug("roleAction: " + roleActions.get(0));
						
			//Set this as the Parent User. The child User will be the user for a specific keyword
			request.getSession().setAttribute("PUser", user);	
			
			if (roleActions.get(0).getRoleType().equals("Office")) {
				dashView = new ModelAndView("dashboard_office", "ltUser", ltUser);
				return "dashboardOffice";
			}
			if (roleActions.get(0).getRoleType().equals("Entity")) {
				dashView = new ModelAndView("dashboard_entity", "ltUser", ltUser);
				return "dashboardEntity";
			}
		
			return null;
		} catch (Exception e) {
			logger.error("login:" + e);
			e.printStackTrace();
			errorMV.addObject("error", e.getMessage());
			return "error";		
		}
	}
	
	@RequestMapping(value = "/changeKeyword", method = RequestMethod.GET)
	public ModelAndView changeKeywordG(@ModelAttribute("ltUser") LTUserForm ltUser, 
								@RequestParam String keyword, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView("/WEB-INF/view/change_keyword_body.jsp", "ltUser", ltUser);
		
		ltUser.setSearchKeywordString(keyword);
		
		return mv;
	}
	
	@RequestMapping(value = "/changeKeyword", method = RequestMethod.POST)
	public @ResponseBody String changeKeyword(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView("/WEB-INF/view/change_keyword_body.jsp", "ltUser", ltUser);
		
		String currKeyword = ltUser.getSearchKeywordString();
		String keyword = ltUser.getSendSearchKeywordString();
		
		try {
				List<KeywordApplication> kwlist = new KeywordDAOManager().checkKWAvail(keyword, PropertyUtil.load().getProperty("shortcode"));
				if (kwlist != null && ! kwlist.isEmpty()) {
					mv.addObject("error", message.getMessage("error.keyword.unavail", new Object[] {keyword}, locale));
					logger.error("Keyword " + keyword + " is not available");
					return message.getMessage("error.keyword.unavail", new Object[] {keyword}, locale);
				}
			
				mktgService.changeKW(currKeyword, keyword);
				
				mv.addObject("error", message.getMessage("ok.resetKeyword", new Object[] {}, locale));
				return message.getMessage("ok.resetKeyword", new Object[] {}, locale);
		} catch (Exception e) {
			mv.addObject("error", message.getMessage("error.resetKeyword", new Object[] {}, locale));				
			e.printStackTrace();
			return message.getMessage("error.resetKeyword", new Object[] {e.getMessage()}, locale);			
		}		
	}
	
	@RequestMapping(value = "/allocateKeyword", method = RequestMethod.GET)
	public ModelAndView allocateKeywordG(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView("/WEB-INF/view/entity/allocate_keyword.jsp", "ltUser", ltUser);
		ModelAndView errorMV = new ModelAndView("error", "ltUser", ltUser);
		ModelAndView mve = new ModelAndView("confirmationMessage", "ltUser", ltUser);

		String entOffId = request.getParameter("entOffId");
		Boolean isOff = Boolean.valueOf(request.getParameter("off"));
				
		ltUser.setCurrentPage("Off");
		
		/*
		//Make sure that the Entity keyword has been allocated
		String entKwStatus = null;
		for (UserProfileVO site : this.getSites())
			if (site.getCustomField4().equals("LIB_F"))
				entKwStatus = site.getCustomField3();
		
		if (isOff && entKwStatus.equals("R")) {
			logger.error("Must allocate Entity keyword first");
			mve.addObject("error", message.getMessage("error.entityKeyword.notset", new Object[] {}, locale));				
			return mve;
		}
		*/
		
		if (isOff)
			ltUser.setSearchOfficeIdString(entOffId);
		else {
			ltUser.setSearchEntityIdString(entOffId);
			ltUser.setCurrentPage("Ent");
		}
		
		return mv;
	}
	
	@RequestMapping(value = "/allocateKeyword", method = RequestMethod.POST)
	public @ResponseBody String allocateKeyword(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView("/WEB-INF/view/entity/allocate_keyword.jsp", "ltUser", ltUser);
		KeywordDAOManager kwDAO = new KeywordDAOManager();
		KeywordApplication kwApp = null;
		
		String shortcode = PropertyUtil.load().getProperty("shortcode");
		String mobilePhone = new SMSDelivery().normalizePhoneNumber(ltUser.getSearchCityString());
		String keyword = ltUser.getSearchKeywordString().toUpperCase();
		String entOffId = null;
		String mode = "Off";
		User thisUser = null;
		List<TargetUserList> tulist = null;
		
		//check if the keyword is available
		List<KeywordApplication> tmpkw = kwDAO.checkKWAvail(keyword, shortcode);				
		if (tmpkw != null) {
			mv.addObject("error", message.getMessage("error.keyword.unavail", new Object[] {keyword}, locale));
			logger.error("Keyword " + keyword + " is not available");
			return "Error";
		}
		
		mv.addObject("message", message.getMessage("error.keyword.avail", new Object[] {keyword}, locale));
		logger.debug("keyword: " + keyword);
		
		//Get the user based on office/entity and update the name of the default TargetUserList
		if (ltUser.getSearchEntityIdString() != null) {
			entOffId = ltUser.getSearchEntityIdString();
			mode = "Ent";
			thisUser = new LibertyAdminDAOManager().getUserByEntityId(entOffId);
			tulist = thisUser.getTargetUserLists();
			for (TargetUserList tul : tulist)
				if (tul.getListName().equals("F"+entOffId) || tul.getListName().equals("AD"+entOffId))
					tul.setListName(keyword);			
		} else {
			entOffId = ltUser.getSearchOfficeIdString();
			thisUser = new LibertyAdminDAOManager().getUserByOfficeId(entOffId);
			tulist = thisUser.getTargetUserLists();
			for (TargetUserList tul : tulist)
				if (tul.getListName().equals("LIB"+entOffId))
					tul.setListName(keyword);			
		}
									
		kwApp = kwDAO.getKeywordById(entOffId, mode);
		
		kwApp.setMobilePhone(mobilePhone);
		kwApp.setKeyword(keyword);
		kwApp.setCustomerField1(entOffId);
		kwApp.setStatus("P");
		kwApp.setSiteId(siteId);
		kwApp.setShortcode(shortcode);
		kwDAO.save(kwApp);	
		
		thisUser.setKeyword(keyword);					
		new LTUserDAOManager().saveUser(thisUser);
		
		ltUser.setSites(getSites());

		//return this.dashboardEntity(ltUser, request);
		return "OK";
	}
	
	@RequestMapping(value = "/sendMessage", method = RequestMethod.GET)
	public ModelAndView sendMessageG(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView("send_message", "ltUser", ltUser);
		ModelAndView errorMV = new ModelAndView("error", "ltUser", ltUser);

		User user = new Utility().getUserFromSecurityContext();
		List<UserProfileVO> sites = null;
		
		try {
			if (user.getRoleActions().get(0).getRoleType().equals("Corporate"))
				sites = corpService.getAllSites();
			else {
				sites = mktgService.getSites(user.getUserId());
				
				CustomFields cfields = dao.getCustomFields(user.getUserId());
				String loc = cfields.getLocation() != null ? cfields.getLocation() : "US";
				user.setBillingCountry(loc);				
			}
			
			if (sites == null) {
				logger.error("No sites found");
				errorMV.addObject("error", "No sites found");
				return errorMV;			
			}			
			
			ltUser.setSites(sites);
			ltUser.setUser(user);			
			
			//set CA states for CA zees/offices
			if (user != null && user.getBillingCountry() != null && user.getBillingCountry().equals("CA")) {
				List<ValueObject> state_codes = dao.getCAStatesLT();
				request.getSession().setAttribute("state_codes", state_codes);
				ltUser.setApprovedMsgs(mktgService.getCorporateMessagesCA(siteId));
			} else {			
				ltUser.setApprovedMsgs(mktgService.getCorporateMessages(siteId));
				ltUser.setApprovedMsgsSP(mktgService.getCorporateMessages(siteId, "SP"));
				ltUser.getApprovedMsgs().addAll(ltUser.getApprovedMsgsSP()); //just add it for now
			}
			
			String entType = "Off";
			if (user.getRoleActions().get(0).getRoleType().equals("Corporate"))
				entType = "Corp";
			if (user.getRoleActions().get(0).getRoleType().equals("Entity"))
				entType = "Ent";
			
			//get the custom messages
			ltUser.setCustomMsgs(mktgService.approvedMsgsFromDate(user.getUserId(), entType, "A"));				
			
			//get the info for the first office
			LTCategory_3 catg = mktgService.getProfile(ltUser.getSites().get(0).getUserId());
			if (catg == null) {
				logger.error("No profile found for userId: " + user.getUserId());
			}
			logger.debug("dashboardEntity end 4: " + Calendar.getInstance().getTimeInMillis());

			ltUser.setCategory(catg);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mv;
	}
	
	@RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
	public @ResponseBody String sendMessage(@ModelAttribute("ltUser") LTUserForm ltUser, BindingResult bindingResult, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("send_message", "ltUser", ltUser);

		User user = new Utility().getUserFromSecurityContext();
		Long userId = user.getUserId();
		
		List<String> listIds = ltUser.getListIds();
		String msg = ltUser.getSendSearchCityString();
		List<String> officeIds = ltUser.getOfficeIds();
		
		try {
			ltUser.setSendNow(ltUser.getNowSched().equals("Y") ? true : false);
			List<UserProfileVO> sites = mktgService.getSites(userId);
			String keyword = null;
			
			//for office role
			if (user.getRoleActions().get(0).getRoleType().equals("Office")) {
				keyword = sites.get(0).getKeyword();
			}
			
			if (user.getRoleActions().get(0).getRoleType().equals("Entity")) {
				//If only one office is selected, use the keyword for that office
				//Else, use the entity keyword
				if (officeIds.size() > 1) {
					keyword = this.getKeyword(userId, null);
				} else {
					keyword = this.getKeyword(userId, officeIds.get(0));
				}
			}

			//check the msg count quota
			if (! new LTSMessageServiceImpl().checkQuota(listIds, userId, msg.length())) {
				logger.error("Msg quota exceeded for user: " + userId);
				mv.addObject("error1", message.getMessage("schedule.error", 
							new Object[] {"\nYou have exceeded your message quota. Please contact customer service."}, locale));
				return message.getMessage("schedule.error", 
						new Object[] {"\nYou have exceeded your message quota. Please contact customer service."}, locale);
			}
			
			// create a campaign
			String cName = ltUser.getSendSearchKeywordString();
			Long dt = Calendar.getInstance().getTimeInMillis();
			if (cName == null) {
				cName = "Campaign-" + dt.toString();
			}
			
			Campaign campaign = new Campaign();
			campaign.setCampaignId(UUID.randomUUID().toString());
			campaign.setUserId(userId);
			campaign.setName(cName);
			campaign.setKeyword(keyword);
			
			String shortcode = PropertyUtil.load().getProperty("shortcode");
			campaign.setShortcode(shortcode);
			
			//campaign.setMultiList(mListIds);
			campaign.setListIds(listIds);
			campaign.setListId("Multi");
			
			String msgText = new CategoryBase().createMessage(keyword + "\n" + ltUser.getSendSearchCityString(), campaign);

			Long uid = this.getUserId(keyword);
			LTCategory_3 catg = null;
			if (uid == null) {
				logger.error("No userId found for keyword: " + keyword);
			} else {
				catg = mktgService.getProfile(uid);
			}
			
			ltUser.setCategory(catg);
			
			if (ltUser.isIncludePhone()) { //get the office phone
				//get the info for the first office
					if (catg != null) {				
						String phone = catg.getPhone();
						if (phone != null && phone.length() > 0)
							msgText += " " + phone;
					}
			}
			
			// get the link if checked
			if (ltUser.isIncludeLink()) {
				String burl = PropertyUtil.load().getProperty("baseURL");
				burl = burl.substring(0, burl.lastIndexOf("/"));
				String linkUrl = burl + "/mwp/" + userId;
				msgText += "\n" + linkUrl;
			}
			
			if (ltUser.isIncludeLink()) { //do this only if link is to be included
				//Include the default link for this store
				String storeLink = PropertyUtil.load().getProperty("corpMobileURL");
				//if (user.getUserLevel() != null && user.getUserLevel().length() > 1)
				//if it an Office role
				if (user.getRoleActions().get(0).getRoleType().equals("Office"))
					storeLink = PropertyUtil.load().getProperty("mobileURLBase") + user.getUserAccountNumber();
				//if Entity has selected an office to send from, then use the office link
				if (! user.getRoleActions().get(0).getRoleType().equals("Corporate")) {
					if (! user.getKeyword().equals(user.getKeyword())) {
						CustomFields cf = new LTUserDAOManager().getCustomFields(userId);
						storeLink = PropertyUtil.load().getProperty("mobileURLBase") + cf.getOfficeId();
					}
				}
				
				msgText += "\n" + storeLink;
			}
			
			campaign.setMessageText(msgText);
			campaign.setRawMessageText(ltUser.getSendSearchCityString());
			
			new LTSMessageServiceImpl().sendMessageLT(campaign, ltUser);
			
			if (ltUser.isSendNow())
				return message.getMessage("success.msg.send", new Object[] {}, locale);
			else
				return message.getMessage("schedule.ok", new Object[] {}, locale);
		} catch (NoSuchMessageException e) {
			e.printStackTrace();
			return message.getMessage("error.msg.send", new Object[] {e.getMessage()}, locale);
		} catch (LTException e) {
			e.printStackTrace();
			return message.getMessage("error.msg.send", new Object[] {e.getMessage()}, locale);
		} catch (Exception e) {
			e.printStackTrace();
			return message.getMessage("error.msg.send", new Object[] {e.getMessage()}, locale);
		}
	}
	
	@RequestMapping(value = "/deleteNumber", method = RequestMethod.POST)
	public @ResponseBody String deleteNumber(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) throws Exception {
		String listId = request.getParameter("listId");
		String number = request.getParameter("number");
				
		try {
			new TargetUserListDao().deleteNumber(listId, number);
			return message.getMessage("ok.number.delete", new Object[] {}, locale);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return message.getMessage("error.number.delete", new Object[] {number}, locale);
	}
	
	@RequestMapping(value = "/viewListData1", method = RequestMethod.GET)
	public @ResponseBody List<String> viewListData1(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView("send_message", "ltUser", ltUser);

		String listId = request.getParameter("listId");
		List<String> listData = null;
		
		try {
			listData = new LTSMessageServiceImpl().getListData(listId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return listData;
	}
	
	@RequestMapping(value = "/viewListData", method = RequestMethod.GET)
	public ModelAndView viewListData(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView("/WEB-INF/view/view_list_data.jsp", "ltUser", ltUser);

		String listId = request.getParameter("listId");
		List<String> listData = null;
		
		try {
			listData = new LTSMessageServiceImpl().getListData(listId);
			ltUser.setListIds(listData);
			ltUser.setSearchDMAString(listId); //save the listId
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return mv;
	}
	
	@RequestMapping(value = "/corpSearch", method = RequestMethod.GET)
	public ModelAndView corpSearchG(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView("advanced_search", "ltUser", ltUser);
		
		User user = new Utility().getUserFromSecurityContext();
		ltUser.setUser(user);
		
		return mv;
	}
	
	@RequestMapping(value = "/corpSearch", method = RequestMethod.POST)
	public ModelAndView corpSearch(@ModelAttribute("ltUser") LTUserForm ltUser, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView("advanced_search", "ltUser", ltUser);

		User user = new Utility().getUserFromSecurityContext();
		
		String searchKeywordStr = ltUser.getSearchKeywordString();
		String searchOfficeIdString = ltUser.getSearchOfficeIdString();
		String searchCityString = ltUser.getSearchCityString();
		String searchEntityIdString = ltUser.getSearchEntityIdString();
		String searchStateString = ltUser.getSearchStateString();
		String searchDMAString = ltUser.getSearchDMAString();

		List<UserProfileVO> keywordLists = corpService.searchKeyword(siteId, searchKeywordStr, 
									searchOfficeIdString, searchCityString, searchEntityIdString, searchStateString, searchDMAString);	
		ltUser.setProfiles(keywordLists);
		ltUser.setUser(user);
		
		return mv;
	}

	@RequestMapping(value = "/quickSearch", method = RequestMethod.GET)
	public @ResponseBody List<UserProfileVO> quickSearch(@ModelAttribute("ltUser") LTUserForm ltUser,
										@RequestParam("keyword") String searchKeywordStr, @RequestParam("entityId") String searchEntityIdString, 
										@RequestParam("officeId") String searchOfficeIdString,
										HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView("dashboard_corp", "ltUser", ltUser);

		List<UserProfileVO> keywordLists = corpService.searchKeyword(siteId, searchKeywordStr, 
									searchOfficeIdString, "", searchEntityIdString, "", "");	
		
		return keywordLists;
	}
	
	@RequestMapping(value = "/createCorpMessage", method = RequestMethod.POST)
	public @ResponseBody String createCorpMessage(@ModelAttribute("ltUser") LTUserForm ltUser) throws Exception {
		Date now = Calendar.getInstance().getTime();
		ApprovedMessage newMsg = new ApprovedMessage();
		newMsg.setSiteId(siteId);
		newMsg.setMessageText(ltUser.getAdNewMsg());
		newMsg.setStatus("A");
		newMsg.setLanguage("EN");
		newMsg.setLocation("US");
		newMsg.setCreated(now);
		newMsg.setUpdated(now);
		
		try {
			corpService.createCorpMessage(newMsg);	
			return "Message created";
		} catch (Exception e) {		
			return e.getMessage();
		}
	}
	
	@RequestMapping(value = "/getReports", method = RequestMethod.GET)
	public ModelAndView getReports(@ModelAttribute("ltUser") LTUserForm ltUser, BindingResult bindingResult) throws Exception {
		ModelAndView mv = new ModelAndView("reports", "ltUser", ltUser);
		User user = new Utility().getUserFromSecurityContext();
		ltUser.setUser(user);

		List<UserProfileVO> sites = null;

		if (user.getRoleActions().get(0).getRoleType().equals("Corporate"))
			sites = corpService.getAllSites();
		else if (user.getRoleActions().get(0).getRoleType().equals("AD"))
			sites = mktgService.getSitesAD(user.getUserId());
		else 
			sites = mktgService.getSites(user.getUserId());

		ltUser.setSites(sites);

		if (ltUser.getSites() == null) { //No sites or none with status = 'P'
			logger.error("No sites found");
			mv.addObject("error", message.getMessage("error.nosites", new Object[] {}, locale));
			ltUser.setReports(null);
			return mv;
		}
				
		List<LTReport> reports = this.getAllReports();
		ltUser.setReports(reports);
		
		for (LTReport rpt : reports) {
			logger.debug("report: " + rpt.getReportId());
			for (ReportParams rp : rpt.getParams())
				logger.debug("param: " + rp.getParamName());
		}
		
		if (ltUser.getSearchKeywordString() == null)
			ltUser.setSearchKeywordString(this.getAllReports().get(0).getReportId().toString()); //default to the first report

		logger.debug("kwstr: " + ltUser.getSearchKeywordString());
		
		return mv;
	}
	
	@RequestMapping(value = "/runReportsPaged", method = RequestMethod.POST)
	public ModelAndView runReportsPaged(@ModelAttribute("ltUser") LTUserForm ltUser, BindingResult bindingResult,
										@RequestParam(value = "page", required = false) String whichPage,
										HttpServletRequest request) throws Exception {	
		User user = new Utility().getUserFromSecurityContext();
		ltUser.setUser(user);
		
		int page = 1;
	    int recordsPerPage = Integer.valueOf(PropertyUtil.load().getProperty("recordsPerPage", "1000"));
	   		
		if (whichPage != null)
		   page = Integer.valueOf(whichPage);
	   
		ModelAndView mv =  new ModelAndView("reports", "ltUser", ltUser);
		
		Map<String, Object> params = new HashMap<String, Object>();
				
		Date fromDate = null;
		Date toDate = null;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

		//check if we have dates
		if (ltUser.getSearchCityString() != null && ltUser.getSearchCityString().length() > 0) {
			fromDate = sdf.parse(ltUser.getSearchCityString());
		}
		if (ltUser.getSearchStateString() != null && ltUser.getSearchStateString().length() > 0) {
			toDate = sdf.parse(ltUser.getSearchStateString());
		}		
		
		if (fromDate != null && toDate != null) {			
	 		//check that to_date >= from_date
	 		if (toDate.compareTo(fromDate) < 0) {
				mv.addObject("error", "Start Date cannot be later than End Date");
				return mv;
	 		} else {
	 			params.put("fromDate", sdf.format(fromDate));
	 			params.put("toDate", sdf.format(toDate));
	 		}
		}
		
		if (fromDate != null) {			
	 		params.put("fromDate", sdf.format(fromDate));
		}
		
		if (fromDate != null && toDate == null) { //if no toDate specified, use today
			toDate = Calendar.getInstance().getTime();
		}
		
		if (toDate != null) {			
	 		params.put("toDate", sdf.format(toDate));
		}
		
		Integer reportType = null;
		
		/*
		for (LTReport report : ltUser.getReports()) {
			if (Integer.valueOf(ltUser.getSearchKeywordString()).equals(report.getReportId())) {
				reportType = report.getReportType();
				if (report.getParams() == null || report.getParams().isEmpty())
					continue;
				for (ReportParams rp : report.getParams()) {
					params.put(rp.getParamName(), rp.getParamValue());
					logger.debug("p/v: " + rp.getParamName() + ", " + rp.getParamValue());
				}
			}
		}
		*/

		//Only the select report will be non-null
		for (LTReport report : ltUser.getReports()) {
			if (report.getReportId() != null) {
				reportType = report.getReportType();
				if (report.getParams() == null || report.getParams().isEmpty())
					continue;
				for (ReportParams rp : report.getParams()) {
					params.put(rp.getParamName(), rp.getParamValue());
					logger.debug("p/v: " + rp.getParamName() + ", " + rp.getParamValue());
				}
			}
		}
		
		if (user.getRoleActions().get(0).getRoleType().equals("Entity")) {			
		}
		
		LTGenerateReportData grd = new LTGenerateReportData(params, reportType);
		List<ReportData> reportRows = grd.runReport(params, (page-1)*recordsPerPage,
 													recordsPerPage, ltUser.getSortColumn(), ltUser.getSortOrder());
 		
		int noOfRecords = 0;
		int noOfPages = 0;
		
		if (! reportRows.isEmpty()) {
			noOfRecords = Integer.valueOf(reportRows.get(reportRows.size()-1).getColumn1());
			noOfPages = (int) Math.ceil(noOfRecords * 1.0 / recordsPerPage);
		}
        
        logger.debug("noOfRecords, noOfPages: " + noOfRecords + ", " + noOfPages);

        if (! reportRows.isEmpty()) {
        	reportRows.remove(reportRows.size() - 1);
        }
        
        ltUser.setReportColumnHeaders(grd.getReportColumnHeaders());
        ltUser.setReportRows(reportRows);
 		
 		logger.debug("colHeaders: " + grd.getReportColumnHeaders().size());

        request.setAttribute("noOfPages", noOfPages);
        request.setAttribute("currentPage", page);
                
        int currentPageStart = page - 5;
        if (currentPageStart < 1)
        	currentPageStart = 1;
        
        int currentPageEnd = page + 4;
        if (currentPageStart == 1)
        	currentPageEnd = 10;
        
        if (currentPageEnd > noOfPages)
        	currentPageEnd = noOfPages;
        
        if (noOfPages <= 10) {
        	currentPageStart = 1;
        	currentPageEnd = noOfPages;
        }
        
        request.setAttribute("currentPageStart", currentPageStart);
        request.setAttribute("currentPageEnd", currentPageEnd);
        
        //set the sites
		List<UserProfileVO> sites = null;

		if (user.getRoleActions().get(0).getRoleType().equals("Corporate"))
			sites = corpService.getAllSites();
		else
			sites = mktgService.getSites(user.getUserId());

		ltUser.setSites(sites);
		
		return mv;
	}

	@RequestMapping(value = "/getScheduledJobs", method = RequestMethod.GET)
	public ModelAndView getScheduledJobs(@ModelAttribute("ltUser") LTUserForm ltUser) throws Exception {		
		Map<String, Object> params = new HashMap<String, Object>();
		User user = new Utility().getUserFromSecurityContext();
		
		List<String> officeIds = ltUser.getOfficeIds();
		//get the userIds from the officeIds
		List<UserProfileVO> sites = null;
		if (user.getRoleActions().get(0).getRoleType().equals("Corporate"))
			sites = corpService.getAllSites();
		else {
			sites = mktgService.getSites(user.getUserId());
			
			CustomFields cfields = dao.getCustomFields(user.getUserId());
			String loc = cfields.getLocation() != null ? cfields.getLocation() : "US";
			user.setBillingCountry(loc);				
		}		
		String uIds = "";
		
		//For Corp user, since we could have a multi-office campaign, we will show all scheduled msgs - userId = 0
		if (user.getRoleActions().get(0).getRoleType().equals("Corporate")) {
			uIds = "0";
		} else {
			for (String offId : officeIds) {
				if (offId.equals("Entity")) { //if Entity is selected, use the entity's userId		
					uIds += (uIds.length() > 0) ? "," + user.getUserId() : user.getUserId();				
				} else {
					for (UserProfileVO site : sites) {
						if (site.getCustomField2() != null && site.getCustomField2().equals(offId)) {
							uIds += (uIds.length() > 0) ? "," + site.getUserId() : site.getUserId();
							break;
						}
					}
				}
			}
		}
		
		params.put("userIds", uIds);
		List<ReportData> reportRows = mktgService.getScheduledTriggers(params, 0, 0, null, null);
        ltUser.setReportRows(reportRows);
		
		return new ModelAndView("/WEB-INF/view/scheduled_jobs.jsp", "ltUser", ltUser);
	}
	
	@RequestMapping(value = "/deleteJob", method = RequestMethod.POST)
	public ModelAndView deleteJob(@ModelAttribute("ltUser") LTUserForm ltUser,
									@RequestParam String triggerName, @RequestParam String triggerGroup,
									HttpServletRequest request) throws Exception {
		new JobScheduler().unScheduleJob(triggerName, triggerGroup);

		return this.getScheduledJobs(ltUser);
	}
	
    @RequestMapping(value = "/uploadFile", method = RequestMethod.GET)
    public ModelAndView uploadFile(@ModelAttribute("ltUser") LTUserForm ltUser) {
    	ModelAndView mv = new ModelAndView("/WEB-INF/view/entity/upload_file.jsp", "ltUser", ltUser);
		User user = new Utility().getUserFromSecurityContext();
		ltUser.setUser(user);
		
    	return mv;
    }
    
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public ModelAndView uploadFileHandler(@ModelAttribute("ltUser") LTUserForm ltUser,
    										 @RequestParam("file") MultipartFile file) {
    	ModelAndView mv = new ModelAndView("/WEB-INF/view/entity/upload_file.jsp", "ltUser", ltUser);
		User user = new Utility().getUserFromSecurityContext();

		String fType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
		String fName = ltUser.getSearchDMAString();
		
		if (!fType.equals("xls") && !fType.equals("xlsx") && !fType.equals("txt")) {
			logger.error("Invalid file type: " + fType);
			mv.addObject("error", message.getMessage("error.invalidFileType", new Object[] {fType}, locale));
			return mv;
		}
		
		 //Save the file of target users
		 String fpath = PropertyUtil.load().getProperty("targetFilePath") + user.getUserId() + "/";
	      
	      if (file == null || file.isEmpty()) {
				logger.debug("Null targetUserFile");
				mv.addObject("error", message.getMessage("error.null.targetUserFile", new Object[] {}, locale));
				return mv;
	      }
			
	      try {
		      Long userId = Long.valueOf(ltUser.getSearchOfficeIdString());
		      User thisUser = mktgService.getUser(userId);	    	  
	    	  this.saveListToDB(thisUser, file, fName, "Marketing");
		      mv.addObject("error", message.getMessage("ok.save.targetUserFile", new Object[] {}, locale));	    	  
	      } catch (Exception e) {
	    	 logger.error("Error saving list: " + e.getMessage());
	    	 mv.addObject("error", message.getMessage("error.save.targetUserFile", new Object[] {e.getMessage()}, locale));
	    	 e.printStackTrace();
	      }

		return mv;
    }
	
	private String saveListToDB(User user, MultipartFile fFile, String listName, String listType) throws Exception {
		   if (fFile == null || fFile.isEmpty()) {
			   throw new Exception("Null form file");
		   }
		   
		   BufferedReader br = null;
		   try {
			   TargetUserListDao tulDao = new TargetUserListDao();
			   //create the list
			   TargetUserList tuList = new TargetUserList();
			   String listId = UUID.randomUUID().toString();
			   tuList.setListId(listId);
			   tuList.setUserId(user.getUserId());
			   tuList.setListName(listName);
			   tuList.setListType(listType);
			   //tulDao.save(tuList);
		   
			   List<TargetListData> tListData = new ArrayList<TargetListData>();

			   String fName = fFile.getOriginalFilename();

			   int listCount = 0;
			   if (fName.indexOf(".xls") > 0 || fName.indexOf(".xlsx") > 0) {
				   tListData = getListDataFromXLS(listId, fFile);
				   listCount = tListData.size();
			   } else {				   
				  br = new BufferedReader(new InputStreamReader(fFile.getInputStream()));
				  //if there are additional fields, the format is Mobile Number, Last Name, FirstName, Address1, Address2 - comma delimited
				  String line = null;
				  String pNum = null;
				  String[] fields = null;
				  while ((line = br.readLine()) != null) {
					  fields = line.split(",");
					  pNum = fields[0];
					  if (pNum == null || pNum.length() <= 0)
						  continue;
					  pNum = normalizePhoneNumber(pNum);
					  TargetListData tld = new TargetListData(listId, pNum);
					  if (fields.length == 1)
						  tListData.add(tld);
					  else {
						  if (fields.length == 2) {
							  tld.setLastName(fields[1]);
						  }
						  
						  if (fields.length == 3) {
							  tld.setLastName(fields[1]);
							  tld.setFirstName(fields[2]);
						  }
						  
						  if (fields.length == 4) {
							  tld.setLastName(fields[1]);
							  tld.setFirstName(fields[2]);  
							  tld.setAddress1(fields[3]);
						  }
						  
						  if (fields.length == 5) {
							  tld.setLastName(fields[1]);
							  tld.setFirstName(fields[2]);  
							  tld.setAddress1(fields[3]);
							  tld.setAddress2(fields[4]);
						  }
						  
						  tListData.add(tld);
					  }
					  listCount++;
					  logger.debug("pNum: " + pNum);
				  }	        
			   }
			   //check if this exceeds the max list limit
			   Integer maxListLimit = Integer.valueOf(PropertyUtil.load().getProperty("MAX_LIST_LIMIT"));
			   if (listCount > maxListLimit) {
				   throw new US411Exception("List limit exceeded");
			   }
			   
			   tulDao.saveListData(tListData);
			   
			   //Since tul is now a part of the User, save the user
			   List<TargetUserList> tuListstmp = user.getTargetUserLists();
			   List<TargetUserList> tuLists = new ArrayList<TargetUserList>();
			   for (TargetUserList tul : tuListstmp) { //skip the All list
				   if (tul.getListName().equals("All"))
					   continue;
					if (tul.getListName().indexOf("(opt-ins)") > 0) {
						logger.error("Found opt-ins in the list name: " + tul.getListName());
						tul.setListName(user.getKeyword());
					}				   
				   tuLists.add(tul);
			   }
			   tuLists.add(tuList);
			   user.setTargetUserLists(tuLists);
			   new UserDAOManager().saveUser(user);
			   
			   /*
			   //add the All list back
				TargetUserList allTul = new TargetUserList();
				allTul.setListId("All");
				allTul.setUserId(user.getUserId());
				allTul.setListName("All");
				user.getTargetUserLists().add(0, allTul);
			   */
			   
			  return listId;
		   } finally {
			  if (br != null)
				  br.close();
		  }		   
	}

	//read the data from a xls & xlsx file using POI
	private List<TargetListData> getListDataFromXLS(String listId, MultipartFile infile) {
		List<TargetListData> tListData = new ArrayList<TargetListData>();
		try {
			Sheet sheet = (Sheet) WorkbookFactory.create(infile.getInputStream()).getSheetAt(0);

			DataFormatter df = new DataFormatter();
			Iterator<Row> rowIterator = sheet.rowIterator();
			
			while (rowIterator.hasNext()) {
				Row row = (Row)rowIterator.next();
				if (df.formatCellValue(row.getCell(0)).startsWith("#"))  //header row - skip it
					continue;
				
				TargetListData tld = new TargetListData();
				tld.setListId(listId);

				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					if (cell == null) //Process only the rows with some data
						continue;
					
					if (cell.getColumnIndex() == 0) {
						String pNum = normalizePhoneNumber(df.formatCellValue(cell));
						tld.setMobilePhone(pNum);
					}
					if (cell.getColumnIndex() == 1) {
						tld.setLastName(df.formatCellValue(cell));
					}
					if (cell.getColumnIndex() == 2) {
						tld.setFirstName(df.formatCellValue(cell));
					}
					if (cell.getColumnIndex() == 3) {
						tld.setAddress1(df.formatCellValue(cell));
					}
					if (cell.getColumnIndex() == 4) {
						tld.setAddress2(df.formatCellValue(cell));
					}					
				}
				tListData.add(tld);					
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tListData;
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public String logout(HttpServletRequest request) throws Exception {	
		
		logger.debug("********* LogOutAction starts ***************");
		User user = new Utility().getUserFromSecurityContext();

		//String secRole = (String) request.getSession().getAttribute("userRole");
		String secRole = user.getSecRole();

		HttpSession hs=request.getSession(true);

		//this is to ensure that the login page uses http and not https
		String logoutURL = PropertyUtil.load().getProperty("logoutURL", PropertyUtil.load().getProperty("baseURL"));
		if (user != null && user.getUserType() != null && user.getUserType().equals("A")) {
			logoutURL = PropertyUtil.load().getProperty("adminBaseURL");
		}
		
		Enumeration e=hs.getAttributeNames();
		
		while(e.hasMoreElements()){
			String tempString=(String)e.nextElement();
			if(hs.getAttribute(tempString)!=null)
				hs.removeAttribute(tempString);
		}

		if(hs!=null){
			hs.invalidate() ;
			hs = null;
		}	
		logger.debug("********* LogOutAction ends ***************");
		
		//return mapping.findForward("success");
				
		if (secRole.equals("AD")) {
			logoutURL = PropertyUtil.load().getProperty("libraLiteUrl");
		}
		if (secRole.equals("Entity") || secRole.equals("Marketing") || secRole.equals("Manager")
				|| secRole.equals("Office") || secRole.equals("OfficeMarketing")) {
			logoutURL = PropertyUtil.load().getProperty("zeeNetUrl");
		} 		
		if (secRole.equals("Marketing Texter") || secRole.equals("Operations")) {
			logoutURL = PropertyUtil.load().getProperty("libraUrl");
		}
		
		return "redirect:" + logoutURL;
	}	
	
	private String startingPage() throws Exception {
		User user = new Utility().getUserFromSecurityContext();
		logger.debug("role: " + user.getSecRole());
		List<RoleAction> roleActions = user.getRoleActions();
			
		if (roleActions.get(0).getRoleType().equals("Office")) {
			return "dashboardOffice";
		}
		if (roleActions.get(0).getRoleType().equals("Entity")) {
			return "dashboardEntity";
		}		
		if (roleActions.get(0).getRoleType().equals("Corporate")) {
			return "dashboardCorp";
		}
		
		return null;
	}
	
	public String normalizePhoneNumber(String phoneNumber) {
		if (phoneNumber == null)
			return null;
		
		Pattern p = Pattern.compile("\\s+|\\.|-| |\\(|\\)");

		Matcher matcher = p.matcher(phoneNumber);
		String tmp = matcher.replaceAll("");
		if (tmp.length() < 11)
			tmp = "1" + tmp;

		return tmp;
	}
	
	private ModelAndView startingMV(@ModelAttribute("ltUser") LTUserForm ltUser) throws Exception {
		User user = new Utility().getUserFromSecurityContext();
		logger.debug("role: " + user.getSecRole());
		List<RoleAction> roleActions = user.getRoleActions();
			
		if (roleActions.get(0).getRoleType().equals("Office")) {
			return new ModelAndView("dashboard_office", "ltUser", ltUser);
		}
		if (roleActions.get(0).getRoleType().equals("Entity")) {
			return new ModelAndView("dashboard_entity", "ltUser", ltUser);
		}		
		if (roleActions.get(0).getRoleType().equals("Corporate")) {
			return new ModelAndView("dashboard_corp", "ltUser", ltUser);
		}
		
		return null;
	}
}
