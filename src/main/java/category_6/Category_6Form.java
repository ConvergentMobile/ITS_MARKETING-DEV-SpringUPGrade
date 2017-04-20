package category_6;

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

public class Category_6Form extends CategoryForm {
	private static final long serialVersionUID = 1L;

	Logger logger = Logger.getLogger(Category_6Form.class);

	private Category_6 category_6;

	public Category_6Form() {
		super();
		this.categoryId = 6;
	}
	
	public CategoryBase getCategory() {
		return category_6;
	}
	
	public Category_6 getCategory_6() {
		return category_6;
	}

	public void setCategory_6(Category_6 category_6) {
		this.category_6 = category_6;
	}
	
	public String populateForm(HttpServletRequest request, String mode, Integer type) throws Exception {
		logger.debug("Category_6Form:populateForm");
		User user = (User)request.getSession().getAttribute("User");
		Integer subCatg = user.getSubCategoryId();
		if (subCatg != null) {
			if (subCatg == 1) {
				category_6 = new Category_6_1(user.getUserId(), user.getCategoryId()).get(request, mode, type);
				logger.debug("HERE - subCat: " + category_6.subCategoryId);
			}
		} else {
			category_6 = new Category_6(user.getUserId(), user.getCategoryId()).get(request, mode, type);
		}
		
		request.getSession().setAttribute("category", category_6);
		
		//get the list of hours
		List<LabelValueBean> hours = new UserDAOManager().getHours();
		request.getSession().setAttribute("hours", hours);
		
		String ret = "category_6";
		if (user.getSubCategoryId() != null)
			ret += "_" + user.getSubCategoryId();
		
		return ret;
	}
	
	public String preview(CategoryForm cForm, HttpServletRequest request) throws Exception {
		category_6 = (Category_6)request.getSession().getAttribute("category");
		category_6.setPreviewFile(category_6.preview(request));
		request.getSession().setAttribute("category", category_6);
		return category_6.getPreviewFile();
	}
	
	public void reset(ActionMapping mapping, HttpServletRequest request) {
		logger.debug("Category_6Form:reset");
        this.category_6 = (Category_6)request.getSession().getAttribute("category");
        if (category_6 == null) {
        	logger.error("reset: Null category_6");
        	return;
        }
	}
}
