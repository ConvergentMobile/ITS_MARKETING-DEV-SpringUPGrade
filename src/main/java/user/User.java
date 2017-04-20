package user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import survey.Survey;

import category_1.Event;

public class User implements Serializable {
	protected static final long serialVersionUID = 1L;
	
	protected Long userId;
	protected Integer categoryId;
	protected String login;
	protected String password;
	protected String status;
	protected String email;
	protected String keyword;
	protected String userType;
	protected Long adminProfileId; //if this is part of a master profile eg livedeal
	protected Integer siteId; 
	
	protected String firstName;
	protected String lastName;
	protected String billingAddress1;
	protected String billingAddress2;
	protected String billingCity;
	protected String billingState;
	protected String billingCountry;
	protected String billingZip;
	protected Double amount;
	protected String paymentTxId;
	protected String arbPaymentTxId;
	protected Double arbAmount;
	protected String hotspotFile; //path to the hotspot img file
		
	protected List<TargetUserList> targetUserLists = new ArrayList<TargetUserList>();
	protected String userAccountNumber; //used to tie multiple keywords for one user
	
	protected Integer subCategoryId;	
	protected Category category;
	
	protected Integer businessCategoryId;
	
	protected int MAX_TARGET_USER_LISTS = 5;
	
	protected int MAX_SURVEYS = 1;
	protected List<Survey> surveyList = new ArrayList<Survey>();
	
	protected String secRole;
	protected List<RoleAction> roleActions = new ArrayList<RoleAction>(); //actions allowed for a role

	protected String userLevel; //use for LT hierarchy - will hold the officeId
	
	public User() {
		
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public Long getAdminProfileId() {
		return adminProfileId;
	}

	public void setAdminProfileId(Long adminProfileId) {
		this.adminProfileId = adminProfileId;
	}

	public Integer getSiteId() {
		return siteId;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getBillingAddress1() {
		return billingAddress1;
	}

	public void setBillingAddress1(String billingAddress1) {
		this.billingAddress1 = billingAddress1;
	}

	public String getBillingAddress2() {
		return billingAddress2;
	}

	public void setBillingAddress2(String billingAddress2) {
		this.billingAddress2 = billingAddress2;
	}

	public String getBillingCity() {
		return billingCity;
	}

	public void setBillingCity(String billingCity) {
		this.billingCity = billingCity;
	}

	public String getBillingState() {
		return billingState;
	}

	public void setBillingState(String billingState) {
		this.billingState = billingState;
	}

	public String getBillingCountry() {
		return billingCountry;
	}

	public void setBillingCountry(String billingCountry) {
		this.billingCountry = billingCountry;
	}

	public String getBillingZip() {
		return billingZip;
	}

	public void setBillingZip(String billingZip) {
		this.billingZip = billingZip;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getPaymentTxId() {
		return paymentTxId;
	}

	public void setPaymentTxId(String paymentTxId) {
		this.paymentTxId = paymentTxId;
	}

	public String getHotspotFile() {
		return hotspotFile;
	}

	public void setHotspotFile(String hotspotFile) {
		this.hotspotFile = hotspotFile;
	}

	public List<TargetUserList> getTargetUserLists() {
		return targetUserLists;
	}

	public void setTargetUserLists(List<TargetUserList> targetUserLists) {
		this.targetUserLists = targetUserLists;
	}

	public Integer getSubCategoryId() {
		return subCategoryId;
	}

	public void setSubCategoryId(Integer subCategoryId) {
		this.subCategoryId = subCategoryId;
	}

	public String getArbPaymentTxId() {
		return arbPaymentTxId;
	}

	public void setArbPaymentTxId(String arbPaymentTxId) {
		this.arbPaymentTxId = arbPaymentTxId;
	}

	public Double getArbAmount() {
		return arbAmount;
	}

	public void setArbAmount(Double arbAmount) {
		this.arbAmount = arbAmount;
	}

	public String getUserAccountNumber() {
		return userAccountNumber;
	}

	public void setUserAccountNumber(String userAccountNumber) {
		this.userAccountNumber = userAccountNumber;
	}

	public List<Survey> getSurveyList() {
		return surveyList;
	}

	public void setSurveyList(List<Survey> surveyList) {
		this.surveyList = surveyList;
	}

	public Integer getBusinessCategoryId() {
		return businessCategoryId;
	}

	public void setBusinessCategoryId(Integer businessCategoryId) {
		this.businessCategoryId = businessCategoryId;
	}

	public List<RoleAction> getRoleActions() {
		return roleActions;
	}

	public void setRoleActions(List<RoleAction> roleActions) {
		this.roleActions = roleActions;
	}

	public String getSecRole() {
		return secRole;
	}

	public void setSecRole(String secRole) {
		this.secRole = secRole;
	}

	public String getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(String userLevel) {
		this.userLevel = userLevel;
	}

	public List<TargetUserList> getTargetUserLists(String listType) {
		List<TargetUserList> tulist = new ArrayList<TargetUserList>();

		if (listType == null) {
			for (TargetUserList tul1 : targetUserLists)
				if (tul1.getListType() == null || tul1.getListType().equals("Marketing"))
					tulist.add(tul1);

			return tulist;
		}
		
		for (TargetUserList tul : targetUserLists)
			if (tul.getListType() != null && tul.getListType().equals(listType))
				tulist.add(tul);
		
		return tulist;
	}
}
