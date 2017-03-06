package service_impl;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import keyword.KeywordApplication;
import liberty.CustomFields;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import reports.LTReport;
import reports.ReportData;
import subclass.LTCategory_3;

import user.RoleAction;
import user.TargetUserList;
import user.User;
import util.InputDecoder;
import util.PropertyUtil;
import admin_user.UserProfileVO;
import category_3.Category_3;
import dao.LTReportDAOManager;
import dao.LTUserDAOManager;
import dao.LibertyAdminDAOManager;
import data.ApprovedMessage;
import data.ValueObject;

public class LTSMarketingServiceImpl {
	protected static final Logger logger = Logger.getLogger(LTSMarketingServiceImpl.class);
	Integer siteId = Integer.parseInt(PropertyUtil.load().getProperty("siteId"));
	private LTUserDAOManager dao = new LTUserDAOManager();
	private LibertyAdminDAOManager adminDao = new LibertyAdminDAOManager();
	
	public User loginSSO1(String roleP, String idP, String fp, LTUserDAOManager userDAO, HttpServletRequest request) throws Exception {
		InputDecoder inpDec = new InputDecoder();
		
		String role = URLDecoder.decode(inpDec.getPlainText(roleP), "UTF-8");
		String eId = inpDec.getPlainText(idP);
		
		logger.debug("role: " + role);
		logger.debug("eId: " + eId);
		logger.debug("fp: " + fp);
		
		if (! inpDec.fpMatch(role, eId, fp)) {
			logger.error("FP mismatch");
			throw new Exception("FP mismatch");
		}
		
		logger.debug("login role, eId, siteid = " + role + ", " + eId + ", " + siteId);
	
		//get the role actions for this user
		List<RoleAction> roleActions = dao.getRoleActions(role);
		if (roleActions == null || roleActions.size() == 0) {
			logger.error("Null roles");
			throw new Exception("No roles found");
		}
		
		//save this for logout
		request.getSession().setAttribute("userRole", role);
		//set the eid as well
		request.getSession().setAttribute("eId", eId);
		
		User user = null;
		
		//check if this is a Corporate user
		if (roleActions.get(0).getRoleType().equals("Corporate")) {
			user = new User();
			user.setUserId(0L); //set this so that ReportAction.setup does not barf
		} else {		
			//user = new LTUserDAOManager().loginLT(role, eId); //user hierarchy is set in loginLT
			user = dao.loginLT(roleActions.get(0).getRoleType(), eId);
		}

		if (user == null) {
			logger.error("login: null User");
			throw new Exception("No user found");
		}
		
		//get all the events and the offices for this entity
		//Map<String, String> offices = userDAO.getOfficesByRole(user.getUserId());
		//List<LTSEvent> events = userDAO.getEvents(siteId);
				
		//request.getSession().setAttribute("offices", offices);
		//request.getSession().setAttribute("events", events);
		
		//check if a session exists
		User u = (User) request.getSession().getAttribute("User");
		if (u != null && u.getUserId() != null) { //for debug only
			logger.debug("existing user id: " + u.getUserId());
		}
		
		logger.debug("roleAction: " + roleActions.get(0));
		
		//check if this is a Corporate user
		if (roleActions.get(0).getRoleType().equals("Corporate")) {
			user = new User();
			user.setUserId(0L); //set this so that ReportAction.setup does not barf
		} else {		
			//user = new LTUserDAOManager().loginLT(role, eId); //user hierarchy is set in loginLT
			user = new LTUserDAOManager().loginLT(roleActions.get(0).getRoleType(), eId);
		}
		
		//set the siteId
		user.setSiteId(siteId);
		logger.debug("userId = " + user.getUserId());	
		
		user.setUserAccountNumber(eId); //set this to the id we get from LT			
		user.setRoleActions(roleActions);
		user.setSecRole(roleActions.get(0).getRoleType());

		//request.getSession().setAttribute("User", user); -- should be retrieved from Principal
		
		//Set this as the Parent User. The child User will be the user for a specific keyword
		//request.getSession().setAttribute("PUser", user);		
		
		return user;
	}
	
	public List<ValueObject> getAllOffices(Long userId) throws Exception {
		return dao.getAllOffices(userId);
	}
	
	public User getUser(Long userId) throws Exception {
		return dao.getUser(userId);
	}
	
	public LTCategory_3 getProfile(Long userId) throws Exception {
		String sql = "from LTCategory_3 as p where p.userId = ?";

		LTCategory_3 catg = dao.getProfile(userId, sql);
		//set the std repeat opt-in msg
		
		String repeatMsg = "Welcome back to the Liberty Tax Service SMS program. We will be sending some more great offers soon.";
		if (catg != null && (catg.getAutoResponse() == null || catg.getAutoResponse().length() <= 0)) {
			catg.setAutoResponse(repeatMsg);
		}
					
		return catg;
	}
	
	public LTCategory_3 getProfileFromSession(Long userId) throws Exception {
		return dao.getProfileFromSession(userId);					
	}
	
	public List<ApprovedMessage> getPendingMessages(String eid) throws Exception {
		return adminDao.getPendingMsgs(eid);		
	}

	public List<ApprovedMessage> getCorporateMessages(Integer siteId) throws Exception {
		return adminDao.getApprovedMsgs(siteId);
	}
	
	public List<ApprovedMessage> getCorporateMessages(Integer siteId, String lang) throws Exception {
		return adminDao.getApprovedMsgs(siteId, lang);
	}
	
	public List<ApprovedMessage> getCorporateMessages(Integer siteId, String lang, String brandName) throws Exception {
		return adminDao.getApprovedMsgs(siteId, lang, brandName);
	}
	
	public List<ApprovedMessage> getCustomMessages(Integer siteId, String eid) throws Exception {
		List<ApprovedMessage> mlist = adminDao.getCustomMsgs(siteId, eid);
		return mlist;
	}

	public List<ApprovedMessage> getCorporateMessagesCA(Integer siteId) throws Exception {
		return adminDao.getApprovedMsgsCA(siteId, "EN");
	}
	
	public List<UserProfileVO> getSites(Long userId) throws Exception {
		return adminDao.getSites(userId);
	}

	public List<UserProfileVO> getAllSites(Long userId) throws Exception {
		return adminDao.getAllSites(userId);
	}
	
	public List<UserProfileVO> getSites(Long userId, String sortColumn, String sortOrder) throws Exception {
		return adminDao.getSites(userId, sortColumn, sortOrder);
	}
	
	public List<UserProfileVO> getAllSites(Long userId, String sortColumn, String sortOrder) throws Exception {
		return adminDao.getAllSites(userId, sortColumn, sortOrder);
	}
	
	public List<UserProfileVO> getSitesAD(Long userId) throws Exception {
		return adminDao.getSitesAD(userId);
	}
	
	public List<UserProfileVO> getAllSitesAD(Long userId) throws Exception {
		return adminDao.getAllSitesAD(userId);
	}
	
	//get approved messages from the last x days
	public List<ApprovedMessage> approvedMsgsFromDate(Long userId, String entType, String status, Integer days) throws Exception {		
		List<ApprovedMessage> mlist = adminDao.getCustomMsgs(siteId, userId, entType, status, days); 
		//if (mlist == null || mlist.isEmpty())
			return mlist;
		
		/*
		for (ApprovedMessage amsg : mlist) {
			 String s = amsg.getMessageText().replaceAll("\n", "<LF>");
			 s = s.replaceAll("\\p{Cntrl}", "<LF>"); //to get rid of any ^Ms
		     s = s.replaceAll("'", "\\\\'");
		     s = s.replaceAll("\"", "\\\\\"");
			 amsg.setMessageText(s);
			 logger.debug("msg: " + amsg.getMessageText());
		 }
		return mlist;
		*/
	}
	
	public List<ApprovedMessage> approvedMsgsFromDate(Long userId, String entType, String status) throws Exception {		
		return this.approvedMsgsFromDate(userId, entType, status, null);
	}
	
	public List<LTReport> getReports() throws Exception {
		return new LTReportDAOManager().getReports(siteId);
	}
		
	public List<ReportData> getScheduledTriggers(Map params,  int offset, int numRecords, String sortField, String sortOrder) throws Exception {
		return new LTReportDAOManager().getScheduledTriggers(params, offset, numRecords, sortField, sortOrder);
	}	
	
	public void changeKW(String oldKeyword, String keyword) throws Exception {
		adminDao.changeKW(oldKeyword, keyword, siteId);
	}
	
	public void saveCategory(LTCategory_3 catg) throws Exception {
		adminDao.saveObject(catg);
	}

	public KeywordApplication getKeywordByUserId(Long userId) throws Exception {
		return adminDao.getKeywordByUserId(userId);
	}
	
	public CustomFields getCustomFields(Long userId) throws Exception {
		return dao.getCustomFields(userId);
	}
	
	public void deleteObject(Object obj) throws Exception {
		adminDao.deleteObject(obj);
	}

	public ApprovedMessage getCustomMsgById(Integer msgId) throws Exception {
		return adminDao.getCustomMsgById(msgId);
	}
	
	public List<UserProfileVO> getSAFSites(Long userId) throws Exception {
		return adminDao.getSAFSites(userId);
	}
	
	public List<ValueObject> getSAFOffices(Long userId) throws Exception {
		return adminDao.getSAFOffices(userId);
	}
	
	public void saveDetails(Object obj) throws Exception {
		dao.saveDetails(obj);
	}
	
	public int deleteSAF(String officeId) throws Exception {
		return adminDao.deleteSAF(officeId);
	}
	
	public List<ValueObject> getUsageReport() throws Exception {
		return adminDao.getUsageReport();
	}
	
	public TargetUserList getList(String listId) throws Exception {
		return dao.getList(listId);
	}
}
