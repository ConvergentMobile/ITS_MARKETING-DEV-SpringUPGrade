package user;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

//this should have the same number of field_i elements as the field_i columns in the category table
public class Category implements Serializable {
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(Category.class);

	private Integer categoryId;
	
	private List<Field> fields;

	public Category() {
		
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
}
