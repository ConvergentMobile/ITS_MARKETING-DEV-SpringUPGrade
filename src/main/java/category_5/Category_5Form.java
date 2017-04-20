package category_5;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;

import user.CategoryBase;
import user.CategoryForm;
import user.User;
import user.UserDAOManager;

public class Category_5Form extends CategoryForm {
	private static final long serialVersionUID = 1L;

	Logger logger = Logger.getLogger(Category_5Form.class);

	private Category_5 category;

	public Category_5Form() {
		super();
		this.categoryId = 5;
	}

	public CategoryBase getCategory() {
		return category;
	}
	
	public Category_5 getCategory_5() {
		return category;
	}

	public void setCategory(Category_5 category) {
		this.category = category;
	}
	
	public String populateForm(HttpServletRequest request, String mode, Integer type) throws Exception {
		logger.debug("Category_5Form:populateForm");
		User user = (User)request.getSession().getAttribute("User");
		category = new Category_5(user.getUserId(), user.getCategoryId()).get(request, mode, type);
		request.getSession().setAttribute("category", category);
		
		//get the list of hours
		List<LabelValueBean> hours = new UserDAOManager().getHours();
		request.getSession().setAttribute("hours", hours);
		
		return "category_5";
	}
	
	public String preview(CategoryForm cForm, HttpServletRequest request) throws Exception {
		category = (Category_5)request.getSession().getAttribute("category");
		category.setPreviewFile(category.preview(request));
		request.getSession().setAttribute("category", category);
		return category.getPreviewFile();
	}
	
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		logger.debug("Category_5Form:reset");
        this.category = (Category_5)request.getSession().getAttribute("category");
        if (category == null) {
        	logger.error("reset: Null category");
        	return;
        }
	}
}
