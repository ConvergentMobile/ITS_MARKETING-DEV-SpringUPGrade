package data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import liberty.CustomFields;

import org.apache.log4j.Logger;

import reports.LTReport;
import reports.ReportData;
import subclass.LTCategory_3;
import user.TargetListData;
import user.TargetUserList;
import user.User;
import admin_user.UserProfileVO;
import dao.LTUserDAOManager;

public class LTUserForm {
	private static Logger logger = Logger.getLogger(LTUserForm.class);

	private User user;
	private LTCategory_3 category;
	private Integer siteId;
	private Long userId;
	
	private List<UserProfileVO> profiles;
	private String secRole;
	private List<TargetUserList> tuLists; // = new HashMap<String, List<TargetUserList>>();
	private List<SendMessageList> keywordLists1 = new ArrayList<SendMessageList>();
	private List<TargetListData> listData;
	
	//Keyword Search related
	private String searchKeywordString;
	private String searchOfficeIdString;
	private String searchCityString;
	private String searchEntityIdString;
	private String searchDMAString;
	private String searchStateString;
	
	//Send Search related
	private String sendSearchKeywordString;
	private String sendSearchOfficeIdString;
	private String sendSearchCityString;
	private String sendSearchEntityIdString;
	private String sendSearchStateString;
	private String sendSearchDMAString;
	
	private List<String> officeIds;
	private List<String> listIds; //listIds selected by user to send a msg to
	private List<CustomFields> reservedKeywords;
	
	private String currentPage;
	private Boolean sendAll; //the All option for Send
	
	//AD related
	private String adNewMsg;
	private List<ApprovedMessage> pendingMsgsAD;
	private List<ApprovedMessage> approvedMsgsAD;

	private List<ApprovedMessage> customMsgs;
	private List<ApprovedMessage> approvedMsgs;
	private List<ApprovedMessage> approvedMsgsSP;
	private List<ApprovedMessage> pendingMsgs;
	private List<ValueObject> hours;
	private List<UserProfileVO> sites;
	
	//Msg Send
	private String schedDate;
	private String schedTime;
	private boolean sendNow;
	private boolean includeLink;
	private String nowSched;
	private boolean includePhone;
	
	private Integer repeatDayCount;
	private Integer repeatMonthCount;
	private Integer numberOccurrencesDays;
	private Integer numberOccurrencesMonths;
	private String repeatPeriod;
	
	private ApprovedMessage aMsg;
	
	//reports
	protected List<LTReport> reports = null;
	protected List<ValueObject> reportData = null;
	protected List<String> reportColumnHeaders;
	protected List<ReportData> reportRows = null;
	protected Map<String, String> summaryRow;
	
	//to keep track of the sorting
	protected String sortColumn;
	protected String sortOrder;
	
	public LTUserForm() {
		init();
	}

	public LTCategory_3 getCategory() {
		return category;
	}

	public void setCategory(LTCategory_3 category) {
		this.category = category;
	}

	public List<UserProfileVO> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<UserProfileVO> profiles) {
		this.profiles = profiles;
	}

	public String getSecRole() {
		return secRole;
	}

	public void setSecRole(String secRole) {
		this.secRole = secRole;
	}
	
	public List<TargetUserList> getTuLists() {
		return tuLists;
	}

	public void setTuLists(List<TargetUserList> tuLists) {
		this.tuLists = tuLists;
	}

	public String getRepeatPeriod() {
		return repeatPeriod;
	}

	public void setRepeatPeriod(String repeatPeriod) {
		this.repeatPeriod = repeatPeriod;
	}

	//get an entry based on listId
	public TargetUserList getByListId(String listId, Map<String, List<TargetUserList>> keywordLists) {
		for (Map.Entry<String, List<TargetUserList>> entry: keywordLists.entrySet()) {
			for (TargetUserList tul : entry.getValue()) {
				logger.debug("ids are: " + listId + ", " + tul.getListId());
				if (tul.getListId().equals(listId))
					return tul;
			}
		}
		return null;
	}
	
	public String getSearchKeywordString() {
		return searchKeywordString;
	}

	public void setSearchKeywordString(String searchKeywordString) {
		this.searchKeywordString = searchKeywordString;
	}

	public String getSearchOfficeIdString() {
		return searchOfficeIdString;
	}

	public void setSearchOfficeIdString(String searchOfficeIdString) {
		this.searchOfficeIdString = searchOfficeIdString;
	}

	public String getSearchCityString() {
		return searchCityString;
	}

	public void setSearchCityString(String searchCityString) {
		this.searchCityString = searchCityString;
	}

	public String getSearchStateString() {
		return searchStateString;
	}

	public void setSearchStateString(String searchStateString) {
		this.searchStateString = searchStateString;
	}

	public List<String> getListIds() {
		return listIds;
	}

	public void setListIds(List<String> listIds) {
		this.listIds = listIds;
	}

	public List<CustomFields> getReservedKeywords() {
		return reservedKeywords;
	}

	public void setReservedKeywords(List<CustomFields> reservedKeywords) {
		this.reservedKeywords = reservedKeywords;
	}

	public String getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}

	public Boolean getSendAll() {
		return sendAll;
	}

	public void setSendAll(Boolean sendAll) {
		this.sendAll = sendAll;
	}

	public String getSendSearchKeywordString() {
		return sendSearchKeywordString;
	}

	public void setSendSearchKeywordString(String sendSearchKeywordString) {
		this.sendSearchKeywordString = sendSearchKeywordString;
	}

	public String getSendSearchOfficeIdString() {
		return sendSearchOfficeIdString;
	}

	public void setSendSearchOfficeIdString(String sendSearchOfficeIdString) {
		this.sendSearchOfficeIdString = sendSearchOfficeIdString;
	}

	public String getSendSearchCityString() {
		return sendSearchCityString;
	}

	public void setSendSearchCityString(String sendSearchCityString) {
		this.sendSearchCityString = sendSearchCityString;
	}

	public String getSearchEntityIdString() {
		return searchEntityIdString;
	}

	public void setSearchEntityIdString(String searchEntityIdString) {
		this.searchEntityIdString = searchEntityIdString;
	}

	public String getSearchDMAString() {
		return searchDMAString;
	}

	public void setSearchDMAString(String searchDMAString) {
		this.searchDMAString = searchDMAString;
	}

	public String getSendSearchEntityIdString() {
		return sendSearchEntityIdString;
	}

	public void setSendSearchEntityIdString(String sendSearchEntityIdString) {
		this.sendSearchEntityIdString = sendSearchEntityIdString;
	}

	public String getSendSearchStateString() {
		return sendSearchStateString;
	}

	public void setSendSearchStateString(String sendSearchStateString) {
		this.sendSearchStateString = sendSearchStateString;
	}

	public String getSendSearchDMAString() {
		return sendSearchDMAString;
	}

	public void setSendSearchDMAString(String sendSearchDMAString) {
		this.sendSearchDMAString = sendSearchDMAString;
	}

	public String getAdNewMsg() {
		return adNewMsg;
	}

	public void setAdNewMsg(String adNewMsg) {
		this.adNewMsg = adNewMsg;
	}

	public List<ApprovedMessage> getPendingMsgsAD() {
		return pendingMsgsAD;
	}

	public void setPendingMsgsAD(List<ApprovedMessage> pendingMsgsAD) {
		this.pendingMsgsAD = pendingMsgsAD;
	}

	public List<SendMessageList> getKeywordLists1() {
		return keywordLists1;
	}

	public void setKeywordLists1(List<SendMessageList> keywordLists1) {
		this.keywordLists1 = keywordLists1;
	}

	public List<TargetListData> getListData() {
		return listData;
	}

	public void setListData(List<TargetListData> listData) {
		this.listData = listData;
	}

	public List<ApprovedMessage> getApprovedMsgs() {
		return approvedMsgs;
	}

	public void setApprovedMsgs(List<ApprovedMessage> approvedMsgs) {
		this.approvedMsgs = approvedMsgs;
	}

	public List<ApprovedMessage> getApprovedMsgsSP() {
		return approvedMsgsSP;
	}

	public void setApprovedMsgsSP(List<ApprovedMessage> approvedMsgsSP) {
		this.approvedMsgsSP = approvedMsgsSP;
	}

	public List<ValueObject> getHours() {
		return hours;
	}

	public void setHours(List<ValueObject> hours) {
		this.hours = hours;
	}

	public List<UserProfileVO> getSites() {
		return sites;
	}

	public void setSites(List<UserProfileVO> sites) {
		this.sites = sites;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getSiteId() {
		return siteId;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public List<ApprovedMessage> getApprovedMsgsAD() {
		return approvedMsgsAD;
	}

	public void setApprovedMsgsAD(List<ApprovedMessage> approvedMsgsAD) {
		this.approvedMsgsAD = approvedMsgsAD;
	}

	public String getSchedDate() {
		return schedDate;
	}

	public void setSchedDate(String schedDate) {
		this.schedDate = schedDate;
	}

	public String getSchedTime() {
		return schedTime;
	}

	public void setSchedTime(String schedTime) {
		this.schedTime = schedTime;
	}

	public boolean isSendNow() {
		return sendNow;
	}

	public void setSendNow(boolean sendNow) {
		this.sendNow = sendNow;
	}

	public boolean isIncludeLink() {
		return includeLink;
	}

	public void setIncludeLink(boolean includeLink) {
		this.includeLink = includeLink;
	}

	public boolean isIncludePhone() {
		return includePhone;
	}

	public void setIncludePhone(boolean includePhone) {
		this.includePhone = includePhone;
	}

	public Integer getRepeatDayCount() {
		return repeatDayCount;
	}

	public void setRepeatDayCount(Integer repeatDayCount) {
		this.repeatDayCount = repeatDayCount;
	}

	public Integer getRepeatMonthCount() {
		return repeatMonthCount;
	}

	public void setRepeatMonthCount(Integer repeatMonthCount) {
		this.repeatMonthCount = repeatMonthCount;
	}

	public Integer getNumberOccurrencesDays() {
		return numberOccurrencesDays;
	}

	public void setNumberOccurrencesDays(Integer numberOccurrencesDays) {
		this.numberOccurrencesDays = numberOccurrencesDays;
	}

	public Integer getNumberOccurrencesMonths() {
		return numberOccurrencesMonths;
	}

	public void setNumberOccurrencesMonths(Integer numberOccurrencesMonths) {
		this.numberOccurrencesMonths = numberOccurrencesMonths;
	}

	public List<ApprovedMessage> getPendingMsgs() {
		return pendingMsgs;
	}

	public void setPendingMsgs(List<ApprovedMessage> pendingMsgs) {
		this.pendingMsgs = pendingMsgs;
	}

	public List<ApprovedMessage> getCustomMsgs() {
		return customMsgs;
	}

	public void setCustomMsgs(List<ApprovedMessage> customMsgs) {
		this.customMsgs = customMsgs;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public List<String> getOfficeIds() {
		return officeIds;
	}

	public void setOfficeIds(List<String> officeIds) {
		this.officeIds = officeIds;
	}

	public ApprovedMessage getaMsg() {
		return aMsg;
	}

	public void setaMsg(ApprovedMessage aMsg) {
		this.aMsg = aMsg;
	}

	public List<LTReport> getReports() {
		return reports;
	}

	public void setReports(List<LTReport> reports) {
		this.reports = reports;
	}

	public List<ValueObject> getReportData() {
		return reportData;
	}

	public void setReportData(List<ValueObject> reportData) {
		this.reportData = reportData;
	}

	public List<String> getReportColumnHeaders() {
		return reportColumnHeaders;
	}

	public void setReportColumnHeaders(List<String> reportColumnHeaders) {
		this.reportColumnHeaders = reportColumnHeaders;
	}

	public List<ReportData> getReportRows() {
		return reportRows;
	}

	public void setReportRows(List<ReportData> reportRows) {
		this.reportRows = reportRows;
	}

	public String getSortColumn() {
		return sortColumn;
	}

	public void setSortColumn(String sortColumn) {
		this.sortColumn = sortColumn;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getNowSched() {
		return nowSched;
	}

	public void setNowSched(String nowSched) {
		this.nowSched = nowSched;
	}

	public Map<String, String> getSummaryRow() {
		return summaryRow;
	}

	public void setSummaryRow(Map<String, String> summaryRow) {
		this.summaryRow = summaryRow;
	}

	public void init() {		
		this.sendAll = false;
		
		/*
		this.pendingMsgsAD = ListUtils.lazyList(new ArrayList(),
		        new org.apache.commons.collections.Factory() {
		            public Object create() {
		                return new ApprovedMessage();
		            }
		        });
		*/

		try {
			if (hours == null) {
				hours = new LTUserDAOManager().getHoursLT();
			}
			/*
			Integer siteId = Integer.parseInt(PropertyUtil.load().getProperty("siteId"));
			if (approvedMsgs == null || approvedMsgs.size() <= 0) {
				if (user != null && user.getBillingCountry().equals("CA")) {
					approvedMsgs = new LibertyAdminDAOManager().getApprovedMsgsCA(siteId, "EN");
				} else {
					approvedMsgs = new LibertyAdminDAOManager().getApprovedMsgs(siteId);
					approvedMsgsSP = new LibertyAdminDAOManager().getApprovedMsgs(siteId, "SP");
				}
			}
			
			if (this.reservedKeywords == null || this.reservedKeywords.size() <= 0) {
				if (user != null)
					this.reservedKeywords = new LibertyAdminDAOManager().getReservedKeywords(user.getUserId());
			}
			*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
