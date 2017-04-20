package util;

import java.io.Serializable;

//class to save errored msgs
public class US411Message implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected Long msgId;
	protected String shortcode;
	protected String keyword;
	protected String campaignId;
	protected String msgText;
	protected String mobilePhone;
	protected String status;
	protected Long userId;
	
	public US411Message() {
		
	}
	
	public US411Message(String shortcode, String keyword, String campaignId,
			String msgText, String mobilePhone, String status, Long userId) {
		super();
		this.shortcode = shortcode;
		this.keyword = keyword;
		this.campaignId = campaignId;
		this.msgText = msgText;
		this.mobilePhone = mobilePhone;
		this.status = status;
		this.userId = userId;
	}

	public Long getMsgId() {
		return msgId;
	}

	public void setMsgId(Long msgId) {
		this.msgId = msgId;
	}

	public String getShortcode() {
		return shortcode;
	}

	public void setShortcode(String shortcode) {
		this.shortcode = shortcode;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public String getMsgText() {
		return msgText;
	}

	public void setMsgText(String msgText) {
		this.msgText = msgText;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
