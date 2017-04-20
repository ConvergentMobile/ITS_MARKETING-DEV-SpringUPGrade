package user;

import java.io.Serializable;
import java.util.Map;

public class UserProfile implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long profileId;
	private Category category;
	private Map<String, Object> fieldValues; // e.g. Business Name, Tom's Restaurant
	private Long userId;
	
	public UserProfile() {
		
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

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Map<String, Object> getFieldValues() {
		return fieldValues;
	}

	public void setFieldValues(Map<String, Object> fieldValues) {
		this.fieldValues = fieldValues;
	}
	
	public Object getFValue(String key) {
		return fieldValues.get(key);
	}
	
	public void setFValue(String key, Object value) {
		fieldValues.put(key, value);
	}
	
}
