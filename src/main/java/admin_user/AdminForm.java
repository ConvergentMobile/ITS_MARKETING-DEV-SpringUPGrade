package admin_user;

import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import keyword.KeywordForm;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;

import user.User;
import util.PropertyUtil;

public class AdminForm extends KeywordForm {
	private static final long serialVersionUID = 1L;

	Logger logger = Logger.getLogger(AdminForm.class);
	
	private List<UserProfileVO> profiles;
	private static Properties props = new Properties();

	public AdminForm() {
		
	}

	public List<UserProfileVO> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<UserProfileVO> profiles) {
		this.profiles = profiles;
	}

	public void reset(ActionMapping mapping, HttpServletRequest request) {
		super.reset(mapping, request);
				
		logger.debug("AdminForm:reset");
		
		user.setKeyword(null);
		
		//this is all in the super.reset - 11/1/12
		/*
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
			request.getSession().setAttribute("User", u);
		} else {
			logger.info("KeywordForm:reset-got a user");
		}
		
		this.user = u;
		*/		
	}
}
