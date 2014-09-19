package data;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

public class ApprovedMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	protected Integer messageId;
	protected Long userId;
	protected String messageName;
	protected String messageText;
	protected Integer siteId;
	protected String language;
	protected String status;
	protected String comments; //used when a msg has been rejected
	protected String entityId;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")	
	protected Date lastUpdated;
	protected String officeId;
	protected String location;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")	
	protected Date created;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")	
	protected Date updated;
	
	public ApprovedMessage() {		
	}

	public Integer getMessageId() {
		return messageId;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getMessageName() {
		return messageName;
	}

	public void setMessageName(String messageName) {
		this.messageName = messageName;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public Integer getSiteId() {
		return siteId;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}	

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}
	
	public String getLastUpdatedAsStr() {
		return new SimpleDateFormat("MM/dd/yyyy").format(lastUpdated);
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public ApprovedMessage get(Integer msgId, List<ApprovedMessage>msgList) throws Exception {
		for (ApprovedMessage aMsg : msgList)
			if (aMsg.getMessageId().equals(msgId))
				return aMsg;
		
		return null;
	}
}
