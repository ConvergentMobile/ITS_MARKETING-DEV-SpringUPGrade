package keyword;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;

import user.User;
import user.UserDAOManager;
import util.PropertyUtil;

public class KeywordForm extends ActionForm {
	private static final long serialVersionUID = 1L;

	protected static Logger logger = Logger.getLogger(KeywordForm.class);
	
	protected static final String SHORTCODE = "US411";
	protected static final Integer GENERAL_BUSINESS = 3;
	
	protected int MAX_KEYWORDS = 3;
	
	protected User user;
	protected String password1; //for re-rentering password
	protected String keyword1; //for re-entering keyword
	protected String email1; //for re-entering email
	protected String ccNumber;
	protected String ccType;
	protected String ccExpirationMon;
	protected String ccExpirationYear;
	protected String ccSecCode;
	protected String[] keywords; //can specify more than one
	protected String promoCode;
	protected String mobilePhone;
	protected String repName;
	protected Boolean acceptTerms;
	protected String businessName;
	
	protected KeywordApplication keywordApplication;

	private static List<LabelValueBean> busCategories = new ArrayList<LabelValueBean>();
	protected String businessCategoryName; //for display
	protected String keywordSearchString;
	protected String busCatSearchString;
	
	private static Properties props = new Properties();
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public String getShortcode() {
		return this.SHORTCODE;
	}

	public String getPassword1() {
		return password1;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
	}

	public String getKeyword1() {
		return keyword1;
	}

	public void setKeyword1(String keyword1) {
		this.keyword1 = keyword1;
	}
	
	public String getEmail1() {
		return email1;
	}

	public void setEmail1(String email1) {
		this.email1 = email1;
	}

	public String getCcType() {
		return ccType;
	}

	public void setCcType(String ccType) {
		this.ccType = ccType;
	}

	public String getCcNumber() {
		return ccNumber;
	}

	public void setCcNumber(String ccNumber) {
		this.ccNumber = ccNumber;
	}

	public String getCcExpirationMon() {
		return ccExpirationMon;
	}

	public void setCcExpirationMon(String ccExpirationMon) {
		this.ccExpirationMon = ccExpirationMon;
	}

	public String getCcExpirationYear() {
		return ccExpirationYear;
	}

	public void setCcExpirationYear(String ccExpirationYear) {
		this.ccExpirationYear = ccExpirationYear;
	}

	public String getCcSecCode() {
		return ccSecCode;
	}

	public void setCcSecCode(String ccSecCode) {
		this.ccSecCode = ccSecCode;
	}	

	public String[] getKeywords() {
		return keywords;
	}
	
	public String getKeywords(int idx) {
		return this.keywords[idx];
	}

	public void setKeywords(String keyword, int idx) {
		this.keywords[idx] = keyword;
	}

	public void setKeywords(String[] keywords) {
		this.keywords = keywords;
	}

	public KeywordApplication getKeywordApplication() {
		return keywordApplication;
	}

	public void setKeywordApplication(KeywordApplication keywordApplication) {
		this.keywordApplication = keywordApplication;
	}

	public static Properties getProps() {
		return props;
	}

	public static void setProps(Properties props) {
		KeywordForm.props = props;
	}

	public String getPromoCode() {
		return promoCode;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getRepName() {
		return repName;
	}

	public void setRepName(String repName) {
		this.repName = repName;
	}

	public Boolean getAcceptTerms() {
		return acceptTerms;
	}

	public void setAcceptTerms(Boolean acceptTerms) {
		this.acceptTerms = acceptTerms;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	
	public static List<LabelValueBean> getBusCategories() {
		return busCategories;
	}

	public static void setBusCategories(List<LabelValueBean> busCategories) {
		KeywordForm.busCategories = busCategories;
	}

	public String getKeywordSearchString() {
		return keywordSearchString;
	}

	public void setKeywordSearchString(String keywordSearchString) {
		this.keywordSearchString = keywordSearchString;
	}

	public String getBusCatSearchString() {
		return busCatSearchString;
	}

	public void setBusCatSearchString(String busCatSearchString) {
		this.busCatSearchString = busCatSearchString;
	}

	public String getBusinessCategoryName() {
		return businessCategoryName;
	}

	public void setBusinessCategoryName(String businessCategoryName) {
		this.businessCategoryName = businessCategoryName;
	}

	public void reset(ActionMapping mapping, HttpServletRequest request) {
		logger.debug("KeywordForm: reset");
		
		//reset everything
		this.password1 = null;
		this.keyword1 = null;
		this.email1 = null;
		this.ccNumber = null;
		this.ccType = null;
		this.ccExpirationMon = null;
		this.ccExpirationYear = null;
		this.ccSecCode = null;
		this.keywords = null;
		this.promoCode = null;
		this.mobilePhone = null;
		this.keywordApplication = null;
		this.repName = null;
		this.acceptTerms = false;
		this.businessName = null;
		this.keywordSearchString = null;
		this.busCatSearchString = null;
		this.businessCategoryName = null;
		this.user = null;
		
		try {
			//props.load(this.getClass().getResourceAsStream("/MessageResources.properties"));
			props = PropertyUtil.load();
		} catch (Exception e1) {
			logger.error("KeywordForm:reset - " + e1);
		}
		
		User u = (User)request.getSession().getAttribute("User");
		if (u == null) {
			logger.info("KeywordForm:reset-null user");
			u = new User();
			u.setSiteId(Integer.parseInt(props.getProperty("siteId")));
			request.getSession().setAttribute("User", u);
		} else {
			logger.info("KeywordForm:reset-got a user");
		}

		if (u.getCategoryId() == null)
			u.setCategoryId(GENERAL_BUSINESS); //default it

		this.user = u;
		
		logger.debug("siteId = " + this.user.getSiteId());
		
		if (this.keywordApplication == null)
			this.keywordApplication = new KeywordApplication();
		
		this.keywords = new String[MAX_KEYWORDS];
			
		logger.debug("keywords size: " + this.keywords.length);
		
		this.keywordApplication.setShortcode(SHORTCODE);
		
		try {
			List<LabelValueBean> categories = new UserDAOManager().getCategoriesOnly(this.user.getSiteId());
			request.getSession().setAttribute("categories", categories);
			for (LabelValueBean lvb : categories)
				logger.debug("category: " + lvb.getLabel() + " -- " + lvb.getValue());
			List<LabelValueBean> state_codes = new UserDAOManager().getStates();
			request.getSession().setAttribute("state_codes", state_codes);
			if (busCategories == null || busCategories.size() <= 0) {
				busCategories = new KeywordDAOManager().getBusCategories();
				request.getSession().setAttribute("busCategories", busCategories);
			}
		} catch (Exception e) {
			logger.error("Error in getting LOVs: " + e);
		}		
	}
}
