package keyword;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

public class KeywordApplication implements Serializable {
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(KeywordApplication.class);
	
	protected long kwApplId;
	protected String email;
	protected String mobilePhone;
	protected String keyword;
	protected String confirmation;
	protected String shortcode;
	protected Integer categoryId;
	protected String status;
	protected String businessName;
	protected String promoCode;
	protected String repName; //account rep info
	protected String repEmail;
	protected String repMobilePhone;
	protected Boolean acceptTerms;
	protected String customerField1; //customer specific fields
	protected String customerField2;
	protected String customerField3;
	protected String customerField4;
	protected String customerField5;
	
	protected Integer businessCategoryId;
	
	protected Integer siteId;
	protected Date lastUpdated;
	
	protected SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	
	public KeywordApplication() {
		
	}

	public long getKwApplId() {
		return kwApplId;
	}

	public void setKwApplId(long kwApplId) {
		this.kwApplId = kwApplId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getConfirmation() {
		return confirmation;
	}

	public void setConfirmation(String confirmation) {
		this.confirmation = confirmation;
	}

	public String getShortcode() {
		return shortcode;
	}

	public void setShortcode(String shortcode) {
		this.shortcode = shortcode;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getPromoCode() {
		return promoCode;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	public String getRepName() {
		return repName;
	}

	public void setRepName(String repName) {
		this.repName = repName;
	}

	public String getRepEmail() {
		return repEmail;
	}

	public void setRepEmail(String repEmail) {
		this.repEmail = repEmail;
	}

	public String getRepMobilePhone() {
		return repMobilePhone;
	}

	public void setRepMobilePhone(String repMobilePhone) {
		this.repMobilePhone = repMobilePhone;
	}

	public String getCustomerField1() {
		return customerField1;
	}

	public Boolean getAcceptTerms() {
		return acceptTerms;
	}

	public void setAcceptTerms(Boolean acceptTerms) {
		this.acceptTerms = acceptTerms;
	}

	public void setCustomerField1(String customerField1) {
		this.customerField1 = customerField1;
	}

	public String getCustomerField2() {
		return customerField2;
	}

	public void setCustomerField2(String customerField2) {
		this.customerField2 = customerField2;
	}

	public String getCustomerField3() {
		return customerField3;
	}

	public void setCustomerField3(String customerField3) {
		this.customerField3 = customerField3;
	}

	public Integer getSiteId() {
		return siteId;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public Integer getBusinessCategoryId() {
		return businessCategoryId;
	}

	public void setBusinessCategoryId(Integer businessCategoryId) {
		this.businessCategoryId = businessCategoryId;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public String getLastUpdatedAsStr() {
		return sdf.format(lastUpdated);
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getCustomerField4() {
		return customerField4;
	}

	public void setCustomerField4(String customerField4) {
		this.customerField4 = customerField4;
	}

	public String getCustomerField5() {
		return customerField5;
	}

	public void setCustomerField5(String customerField5) {
		this.customerField5 = customerField5;
	}
	
}
 