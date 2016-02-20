package data;

import java.io.Serializable;
import java.util.Date;

public class SAF implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected Long userId;
	protected String officeId;
	protected String entityId;
	protected String keyword;
	//protected Boolean includePhone;
	protected String includePhone;
	protected String safMessage;
	protected Date lastUpdated;
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getOfficeId() {
		return officeId;
	}
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getIncludePhone() {
		return includePhone;
	}
	public void setIncludePhone(String includePhone) {
		this.includePhone = includePhone;
	}
	public String getSafMessage() {
		return safMessage;
	}
	public void setSafMessage(String safMessage) {
		this.safMessage = safMessage;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
}
