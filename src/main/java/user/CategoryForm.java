package user;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class CategoryForm extends ActionForm {
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(CategoryForm.class);

	private String currentPage; //keep track of the current page so you can return to it after save	
	protected Integer categoryId;	
	protected String timezone;
	private String previewFile;
	protected Boolean isAddOffer; //if Add Offer was clicked

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}
	
	public CategoryBase getCategory() throws Exception {
		throw new Exception("Base class getCategory - not implemented");
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getPreviewFile() {
		return previewFile;
	}

	public void setPreviewFile(String previewFile) {
		this.previewFile = previewFile;
	}

	public Boolean getIsAddOffer() {
		return isAddOffer;
	}

	public void setIsAddOffer(Boolean isAddOffer) {
		this.isAddOffer = isAddOffer;
	}

	public void reset(ActionMapping mapping, HttpServletRequest request) {
		CategoryBase catg = (CategoryBase)request.getSession().getAttribute("category");
		logger.debug("CategoryForm:reset:categoryId = " + catg.getCategoryId());
		this.categoryId = catg.getCategoryId();
	}
	
	public String populateForm(HttpServletRequest request, String mode, Integer type)  throws Exception {
		logger.debug("Base class populateForm:categoryId = " + categoryId);
				
		return CategoryFactory.getFormInstance(categoryId).populateForm(request, mode, type);
	}
}
