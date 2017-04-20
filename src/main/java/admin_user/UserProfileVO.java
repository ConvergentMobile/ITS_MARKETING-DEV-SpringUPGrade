package admin_user;

import java.io.Serializable;

public class UserProfileVO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long profileId;
	private Long userId;
	private Long adminId;
	private String keyword;
	private String businessName;
	private Integer siteId;
	private String customField1; //some additional fields
	private String customField2;
	private String customField3;
	private String customField4;
	private String customField5;
	
	public UserProfileVO() {
		
	}

	public UserProfileVO(Long profileId, Long userId, Integer siteId, String keyword,
			String businessName) {
		super();
		this.profileId = profileId;
		this.userId = userId;
		this.siteId = siteId;
		this.keyword = keyword;
		this.businessName = businessName;
	}
	
	public UserProfileVO(Long profileId, Long userId, Long adminId, String keyword,
			String businessName) {
		super();
		this.profileId = profileId;
		this.userId = userId;
		this.adminId = adminId;
		this.keyword = keyword;
		this.businessName = businessName;
	}

	public Long getProfileId() {
		return profileId;
	}

	public void setProfileId(Long profileId) {
		this.profileId = profileId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getAdminId() {
		return adminId;
	}

	public void setAdminId(Long adminId) {
		this.adminId = adminId;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public Integer getSiteId() {
		return siteId;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public String getCustomField1() {
		return customField1;
	}

	public void setCustomField1(String customField1) {
		this.customField1 = customField1;
	}

	public String getCustomField2() {
		return customField2;
	}

	public void setCustomField2(String customField2) {
		this.customField2 = customField2;
	}

	public String getCustomField3() {
		return customField3;
	}

	public void setCustomField3(String customField3) {
		this.customField3 = customField3;
	}

	public String getCustomField4() {
		return customField4;
	}

	public void setCustomField4(String customField4) {
		this.customField4 = customField4;
	}

	public String getCustomField5() {
		return customField5;
	}

	public void setCustomField5(String customField5) {
		this.customField5 = customField5;
	}
	
}
