package service_impl;

import java.util.List;

import dao.LibertyAdminDAOManager;
import data.ApprovedMessage;

import admin_user.UserProfileVO;
import service.LTSCorpService;
import util.PropertyUtil;

public class LTSCorpServiceImpl implements LTSCorpService {
	Integer siteId = Integer.parseInt(PropertyUtil.load().getProperty("siteId"));

	public LibertyAdminDAOManager dao = new LibertyAdminDAOManager();
	
	public List<UserProfileVO> getAllSites() throws Exception {
		return dao.getSites();
	}

	public List<UserProfileVO> getAllSites(	String sortColumn, String sortOrder) throws Exception {
		return dao.getSites(sortColumn, sortOrder);
	}

	public List<UserProfileVO> searchKeyword(Integer siteId, String searchKeywordStr, String searchOfficeIdStr, 
			String searchCityStr, String searchEntityIdStr, String searchStateStr, String searchDMAString) throws Exception {	
		return dao.searchKeyword(siteId, searchKeywordStr, searchOfficeIdStr, searchCityStr, searchEntityIdStr, searchStateStr, searchDMAString);
	}
	
	public List<ApprovedMessage> approvedMsgsFromDate(String status, Integer days) throws Exception {
		return new LibertyAdminDAOManager().getCustomMsgs(siteId, status, days);
	}

	public List<ApprovedMessage> approvedMsgsFromDate(String status) throws Exception {
		return new LibertyAdminDAOManager().getCustomMsgs(siteId, status, null);
	}
	
	public ApprovedMessage getCustomMsgById(Integer msgId) throws Exception {
		return new LibertyAdminDAOManager().getCustomMsgById(msgId);
	}
	
	public void createCorpMessage(ApprovedMessage aMsg) throws Exception {
		dao.saveObject(aMsg);
	}

}
