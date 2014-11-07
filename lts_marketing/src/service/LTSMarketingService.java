package service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import keyword.KeywordApplication;
import liberty.CustomFields;

import reports.LTReport;
import reports.ReportData;
import subclass.LTCategory_3;
import user.User;

import admin_user.UserProfileVO;

import category_3.Category_3;

import dao.LTUserDAOManager;
import data.ApprovedMessage;

public interface LTSMarketingService {
	public void loginSSO(String roleP, String idP, String fp, LTUserDAOManager userDAO, HttpServletRequest request);
	public Map<String, String> geAlltOffices(Long userId);
	
	public List<UserProfileVO> getSites(Long userId);
	public List<UserProfileVO> getAllSites(Long userId);
	
	public List<UserProfileVO> getSites(Long userId, String sortColumn, String sortOrder);
	public List<UserProfileVO> getAllSites(Long userId, String sortColumn, String sortOrder);
	
	public List<UserProfileVO> getSitesAD(Long userId) throws Exception;
	public List<UserProfileVO> getAllSitesAD(Long userId) throws Exception;

	public User getUser(Long userId) throws Exception;

	public LTCategory_3 getProfile(Long userId);
	public LTCategory_3 getProfileByFromSession(Long userId);

	public List<ApprovedMessage> getPendingMessages(String eid);
	public List<ApprovedMessage> getCorporateMessages(Integer siteId);
	public List<ApprovedMessage> getCorporateMessages(Integer siteId, String lang);
	public List<ApprovedMessage> getCustomMessages(Integer siteId, String eid);
	
	public List<ApprovedMessage> getCorporateMessagesCA(Integer siteId);

	public List<ApprovedMessage> approvedMsgsFromDate(Long userId, String entType, String status, Integer days);		
	public List<ApprovedMessage> approvedMsgsFromDate(Long userId, String entType, String status);		

	public List<LTReport> getReports();
	
	public List<ReportData> getScheduledTriggers(Map params, int offset, int numRecords, String sortField, String sortOrder) throws Exception;

	public void changeKW(String oldKeyword, String keyword, Integer siteId) throws Exception;

	public void saveCategory(LTCategory_3 catg) throws Exception;
	
	public KeywordApplication getKeywordByUserId(Long userId) throws Exception;

	public CustomFields getCustomFields(Long userId) throws Exception;
}
