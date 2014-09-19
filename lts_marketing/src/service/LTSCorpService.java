package service;

import java.util.List;

import data.ApprovedMessage;

import admin_user.UserProfileVO;

public interface LTSCorpService {
	public List<UserProfileVO> getAllSites() throws Exception;
	public List<UserProfileVO> getAllSites(String sortColumn, String sortOrder) throws Exception;

	public List<UserProfileVO> searchKeyword(Integer siteId, String searchKeywordStr, String searchOfficeIdStr, 
			String searchCityStr, String searchEntityIdStr, String searchStateStr, String searchDMAString) throws Exception;
	public List<ApprovedMessage> approvedMsgsFromDate(String status, Integer days) throws Exception;		
	public List<ApprovedMessage> approvedMsgsFromDate(String status) throws Exception;		
	public ApprovedMessage getCustomMsgById(Integer msgId) throws Exception;

	public void createCorpMessage(ApprovedMessage aMsg) throws Exception;
}
