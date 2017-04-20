package user;

import java.io.Serializable;

//class that describes the various Categories
public class BusinessCategory implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer categoryId;
	private String name;
	private String[] xsltFiles;
	
	public BusinessCategory(Integer categoryId, String name, String[] xsltFiles) {
		super();
		this.categoryId = categoryId;
		this.name = name;
		this.xsltFiles = xsltFiles;
	}
	public Integer getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String[] getXsltFiles() {
		return xsltFiles;
	}
	public void setXsltFiles(String[] xsltFiles) {
		this.xsltFiles = xsltFiles;
	}
}
