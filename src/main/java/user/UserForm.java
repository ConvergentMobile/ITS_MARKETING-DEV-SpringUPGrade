package user;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;

import util.PropertyUtil;

public class UserForm extends ActionForm {
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(UserForm.class);
	
	private User user;
	private String login;
	private String password;
	private String password1;
	private String password2; //used in reset password
	private String email;
	private String keyword;
	private String keyword1; //alternate keywords
	private String keyword2;
	private String keyword3;
	private String mobilePhone; //used in N&N for creating a login
	private Integer siteId;
	
	private String catgIdStep3Page; //to hold concat of catgId and step_3_page
	
	private Map<String, Object> fieldValues = new HashMap<String, Object>();
	
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
	public String getPassword1() {
		return password1;
	}
	public void setPassword1(String password1) {
		this.password1 = password1;
	}
	
	public String getPassword2() {
		return password2;
	}
	public void setPassword2(String password2) {
		this.password2 = password2;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
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

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getKeyword1() {
		return keyword1;
	}
	public void setKeyword1(String keyword1) {
		this.keyword1 = keyword1;
	}
	public String getKeyword2() {
		return keyword2;
	}
	public void setKeyword2(String keyword2) {
		this.keyword2 = keyword2;
	}
	public String getKeyword3() {
		return keyword3;
	}
	public void setKeyword3(String keyword3) {
		this.keyword3 = keyword3;
	}	
	public String getCatgIdStep3Page() {
		return catgIdStep3Page;
	}
	public void setCatgIdStep3Page(String catgIdStep3Page) {
		this.catgIdStep3Page = catgIdStep3Page;
	}
	
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	
	public Integer getSiteId() {
		return siteId;
	}
	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		logger.debug("UserForm:reset");
		
		try {
			User u = (User)request.getSession().getAttribute("User");
			if (u == null) {
				logger.info("UserForm:reset-null user");
				u = new User();
				
				/* moved to login
				Properties props = PropertyUtil.load();				
				Integer siteId = Integer.parseInt(props.getProperty("siteId"));
				this.user.setSiteId(siteId);
				logger.debug("siteId = " + siteId);	
				*/			
				//request.getSession().setAttribute("User", u);
			} else
				logger.debug("UserForm:reset-Got User: Id = " + u.getUserId());
			
			this.user = u;
			
			Integer siteId = Integer.parseInt(PropertyUtil.load().getProperty("siteId"));

				//need to do this as we don't know at this stage if it's a new user or an existing one
				List<LabelValueBean> categories = new UserDAOManager().getCategoriesOnly(siteId);
				if (categories != null)
					request.getSession().setAttribute("categories", categories);
				else
					request.getSession().setAttribute("categories", new ArrayList<LabelValueBean>());

				List<LabelValueBean> state_codes = (List<LabelValueBean>)request.getSession().getAttribute("state_codes");
				
				if (state_codes == null || state_codes.isEmpty()) {
					state_codes = new UserDAOManager().getStates();
					request.getSession().setAttribute("state_codes", state_codes);
				}
				
				//timezones
				List<LabelValueBean> timezones = new ArrayList<LabelValueBean>();
				timezones.add(new LabelValueBean("Central", "US/Central"));
				timezones.add(new LabelValueBean("Eastern", "US/Eastern"));
				timezones.add(new LabelValueBean("Mountain", "US/Mountain"));
				timezones.add(new LabelValueBean("Pacific", "US/Pacific"));
				
				request.getSession().setAttribute("timezones", timezones);
		} catch (Exception e) {
			logger.error("Error in UserForm:reset - " + e);
			e.printStackTrace();
		}
	}
}
