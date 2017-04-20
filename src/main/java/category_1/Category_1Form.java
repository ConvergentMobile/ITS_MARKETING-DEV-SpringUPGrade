package category_1;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMapping;

import user.CategoryBase;
import user.CategoryForm;
import user.User;

public class Category_1Form extends CategoryForm {
	private static final long serialVersionUID = 1L;

	Logger logger = Logger.getLogger(Category_1Form.class);

	private Category_1 category_1;

	public Category_1Form() {
		super();
		this.categoryId = 1;
	}

	public CategoryBase getCategory() {
		return category_1;
	}
	
	public Category_1 getCategory_1() {
		return category_1;
	}

	public void setCategory_1(Category_1 category_1) {
		this.category_1 = category_1;
	}
	
	public String populateForm(HttpServletRequest request, String mode, Integer type) throws Exception {
		logger.debug("Category_1Form:populateForm");
		User user = (User)request.getSession().getAttribute("User");
		category_1 = new Category_1(user.getUserId(), user.getCategoryId()).get(request, mode, type);
		request.getSession().setAttribute("category", category_1);

		category_1.init(request.getSession());
		
		return "category_1";
	}
	
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		logger.debug("Category_1Form:reset");
        this.category_1 = (Category_1)request.getSession().getAttribute("category");
        if (category_1 == null) {
        	logger.error("reset: Null category_1");
        	return;
        }
	}
}
