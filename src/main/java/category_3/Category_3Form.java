package category_3;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.util.LabelValueBean;

import user.CategoryBase;
import user.CategoryForm;
import user.User;
import user.UserDAOManager;

public class Category_3Form extends CategoryForm {
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(Category_3Form.class);

	protected Category_3 category_3;

	public Category_3Form() {
		super();
		this.categoryId = 3;
	}
	
	public CategoryBase getCategory() {
		return category_3;
	}
	
	public Category_3 getCategory_3() {
		return category_3;
	}

	public void setCategory_3(Category_3 category_3) {
		this.category_3 = category_3;
	}

	public String populateForm(HttpServletRequest request, String mode, Integer type) throws Exception {
		logger.debug("Category_3Form:populateForm");
		
		User user = (User)request.getSession().getAttribute("User");
		category_3 = new Category_3(user.getUserId(), user.getCategoryId()).get(request, mode, type);
		if (category_3.getTimezone() == null)
			category_3.setTimezone("US/Pacific"); //default it
		request.getSession().setAttribute("category", category_3);
		
		//get the list of cuisines - not needed as this is not a part of this category - 3/8/2012
		//List<LabelValueBean> cuisines = new UserDAOManager().getCuisine();
		//request.getSession().setAttribute("cuisines", cuisines);
		
		//get the list of hours
		List<LabelValueBean> hours = new UserDAOManager().getHours();
		request.getSession().setAttribute("hours", hours);
		
		//fonts - not needed - 3/8/2012
		//List<LabelValueBean> fonts = new UserDAOManager().getFonts();
		//request.getSession().setAttribute("fonts", fonts);
		
		return "category_3";
	}
	
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		logger.debug("Category_3Form:reset");
		super.reset(mapping, request);
        this.category_3 = (Category_3)request.getSession().getAttribute("category");
        if (category_3 == null) {
        	logger.error("reset: Null category_3");
        	return;
        }
	}
}
