package user;

import java.io.Serializable;
import java.util.List;

public class TargetUserListMulti implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String multiListId;
	private String campaignId;
	private String listId;
	private Long userId;
	
	public TargetUserListMulti() {
		
	}

	public String getListId() {
		return listId;
	}

	public void setListId(String listId) {
		this.listId = listId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getMultiListId() {
		return multiListId;
	}

	public void setMultiListId(String multiListId) {
		this.multiListId = multiListId;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

}
