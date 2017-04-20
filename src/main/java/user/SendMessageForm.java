package user;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.upload.FormFile;
import org.apache.struts.util.LabelValueBean;

import reports_graphs.ReportData;

public class SendMessageForm extends ActionForm {
	private static final long serialVersionUID = 1L;

	Logger logger = Logger.getLogger(SendMessageForm.class);
	
	private Long userId;
	private String message;
	private FormFile targetFile;
	private String listName;
	private String tuList;
	private String phoneNumber;
	private String schedDate;
	private String schedTime;
	private boolean sendNow;
	private boolean includeLink;
	private String campaignName;
	private String campaignEndDate;
	private List<TargetUserList> targetUserLists; //all the lists for this user
	private TargetUserList targetUserList; //selected list
	 
	private String currentPage; //keep track of the current page so you can return to it after save
	private String smpreviewFile; //used by NN for preview from the send_message page

	private Integer repeatDayCount;
	private Integer repeatMonthCount;
	private Integer numberOccurrencesDays;
	private Integer numberOccurrencesMonths;
	
	private List<Campaign> campaigns;
	
	private String externalLink; //link to user defined mobile web pages
	
	private List<ReportData> schedDeliveries; //list of all (future) scheduled deliveries
	
	private List<String> listNumbers; //all numbers in a given list - for appt/ops msgs
	private String[] selectedNumbers; //numbers selected
	
	private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}	
	public FormFile getTargetFile() {
		return targetFile;
	}
	public void setTargetFile(FormFile targetFile) {
		this.targetFile = targetFile;
	}

	public String getListName() {
		return listName;
	}
	public void setListName(String listName) {
		this.listName = listName;
	}
	
	public String getTuList() {
		return tuList;
	}
	public void setTuList(String tuList) {
		this.tuList = tuList;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
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
	public List<TargetUserList> getTargetUserLists() {
		return targetUserLists;
	}
	public void setTargetUserLists(List<TargetUserList> targetUserLists) {
		this.targetUserLists = targetUserLists;
	}
	public TargetUserList getTargetUserList() {
		return targetUserList;
	}
	public void setTargetUserList(TargetUserList targetUserList) {
		this.targetUserList = targetUserList;
	}
	public void setTargetUserList(String listName) {
		this.targetUserList = new TargetUserList();
		this.targetUserList.setListName(listName);
	}
	
	public String getCampaignName() {
		return campaignName;
	}
	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}
	
	public String getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}
	
	public String getCampaignEndDate() {
		return campaignEndDate;
	}
	public void setCampaignEndDate(String campaignEndDate) {
		this.campaignEndDate = campaignEndDate;
	}
	
	public String getSmpreviewFile() {
		return smpreviewFile;
	}
	public void setSmpreviewFile(String smpreviewFile) {
		this.smpreviewFile = smpreviewFile;
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
	public List<Campaign> getCampaigns() {
		return campaigns;
	}
	public void setCampaigns(List<Campaign> campaigns) {
		this.campaigns = campaigns;
	}
	
	public String getExternalLink() {
		return externalLink;
	}
	public void setExternalLink(String externalLink) {
		this.externalLink = externalLink;
	}
		
	public List<ReportData> getSchedDeliveries() {
		return schedDeliveries;
	}
	public void setSchedDeliveries(List<ReportData> schedDeliveries) {
		this.schedDeliveries = schedDeliveries;
	}
	
	public String[] getSelectedNumbers() {
		return selectedNumbers;
	}
	public void setSelectedNumbers(String[] selectedNumbers) {
		this.selectedNumbers = selectedNumbers;
	}
	public List<String> getListNumbers() {
		return listNumbers;
	}
	public void setListNumbers(List<String> listNumbers) {
		this.listNumbers = listNumbers;
	}
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		logger.debug("SendMessageForm: reset");
		List<LabelValueBean> approvedMsgs = (List<LabelValueBean>)request.getSession().getAttribute("approvedMsgs");
		//logger.debug("approvedMsg size: " + approvedMsgs.size());
		
		this.campaignName = null;
		this.message = null;
		this.targetFile = null;
		this.listName = null;
		this.sendNow = false; //set to true in SendMessageAction
		this.includeLink = false;
		this.schedDate = null;
		this.schedTime = null;
		this.phoneNumber = null;
		this.campaignEndDate = "(Click Calendar)";
		this.smpreviewFile = null;
		this.repeatDayCount = 0;
		this.repeatMonthCount = 0;
		this.numberOccurrencesDays = 0;
		this.numberOccurrencesMonths = 0;
		this.externalLink = null;
		this.selectedNumbers = null;
		this.listNumbers = null;
				
		if (this.targetUserLists == null)
			this.targetUserLists = new ArrayList<TargetUserList>();
		
		if (this.schedDate == null || this.schedDate.length() <= 0)
			this.schedDate = "(Click Calendar)";
		
		if (this.campaigns == null)
			try {
				User user = (User)request.getSession().getAttribute("User");
				this.campaigns = new UserDAOManager().getCampaign(user.getUserId());
				if (this.campaigns == null)
					this.campaigns = new ArrayList<Campaign>();
				request.getSession().setAttribute("campaigns", campaigns);
			} catch (Exception e) {
				logger.error("Error getting campaigns: " + e);
			}
	}
}
