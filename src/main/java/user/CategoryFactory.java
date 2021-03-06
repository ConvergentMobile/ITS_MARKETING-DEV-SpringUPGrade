package user;

import org.apache.log4j.Logger;

import category_1.Category_1;
import category_1.Category_1Form;
import category_3.Category_3;
import category_3.Category_3Form;


public class CategoryFactory {
	private static Logger logger = Logger.getLogger(CategoryFactory.class);

	public static CategoryBase getInstance(Long userId, Integer categoryId) {
		CategoryBase category;
		switch (categoryId) {
			case 1:
				category = new Category_1(userId, categoryId);
				return category;
			case 3:
				category = new Category_3(userId, categoryId);
				return category;
						
			default:
				logger.error("Unknown category: " + categoryId);
				return null;
		}
	}
	
	public static CategoryForm getFormInstance(Integer categoryId) {
		CategoryForm categoryForm;
		switch (categoryId) {
			case 1:
				categoryForm = new Category_1Form();
				return categoryForm;
			case 3:
				categoryForm = new Category_3Form();
				return categoryForm;
			
			default:
				return null;
		}
	}
}