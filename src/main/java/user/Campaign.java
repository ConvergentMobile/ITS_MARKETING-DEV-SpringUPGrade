package user;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

public class Campaign implements Serializable {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(Campaign.class);

	private String campaignId;
	private Long userId;
	private String name;
	private String description;
	private Date startDate;
	private Date endDate;
	private String messageText;
	private String listId;
	private String keyword;
	private String rawMessageText; //for copying message text from old campaigns, save just the raw message without keywords and links
	private String shortcode;
	private String customerCampaignId;
	private List<TargetUserList> multiList; //used to handle Liberty type cases where a campaign can include multiple lists. In this case, listId = Multi
	private Object[] targetNumbers; //used to send a msg to specific numbers
	private String location;
	private List<String> listIds;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	
	public Campaign() {
		
	}
	
	public Campaign(Long userId, String name,
			String description, Date startDate, Date endDate, String messageText, String listId,
			String keyword) {
		super();
		this.userId = userId;
		this.name = name;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.messageText = messageText;
		this.listId = listId;
		this.keyword = keyword;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public String getListId() {
		return listId;
	}

	public void setListId(String listId) {
		this.listId = listId;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getStartDateAsString() {
		return (startDate != null) ? sdf.format(startDate) : "(Click Calendar)";
	}
	
	public String getEndDateAsString() {
		return (endDate != null) ? sdf.format(endDate) : "(Click Calendar)";
	}	
	
	public String getRawMessageText() {
		return rawMessageText;
	}

	//escape for Javascript
	public String getRawMessageTextJS() {
		return StringEscapeUtils.escapeJavaScript(rawMessageText);
	}
	
	public void setRawMessageText(String rawMessageText) {
		this.rawMessageText = rawMessageText;
	}

	public String getShortcode() {
		return shortcode;
	}

	public void setShortcode(String shortcode) {
		this.shortcode = shortcode;
	}

	public String getCustomerCampaignId() {
		return customerCampaignId;
	}

	public void setCustomerCampaignId(String customerCampaignId) {
		this.customerCampaignId = customerCampaignId;
	}

	public void setEndDateAsString(String campaignEndDate) throws Exception {
		try {
			if (campaignEndDate == null || campaignEndDate.length() <= 0 || campaignEndDate.equals("(Click Calendar)"))
				this.endDate = null;
			else
				this.endDate = sdf.parse(campaignEndDate);
		} catch (ParseException e) {
			logger.error("Campaign: invalid date format");
			throw new Exception("Campaign: invalid date format" + e);
		}
	}

	public List<TargetUserList> getMultiList() {
		return multiList;
	}

	public void setMultiList(List<TargetUserList> multiList) {
		this.multiList = multiList;
	}

	public Object[] getTargetNumbers() {
		return targetNumbers;
	}

	public void setTargetNumbers(Object[] targetNumbers) {
		this.targetNumbers = targetNumbers;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<String> getListIds() {
		return listIds;
	}

	public void setListIds(List<String> listIds) {
		this.listIds = listIds;
	}

}
