package category_1;

import java.io.Serializable;

import org.apache.log4j.Logger;

public class MenuItem implements Serializable {
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(MenuItem.class);
	
	private Long menuItemId;
	private String name;
	private String price;
	private String description;
	private Integer itemType;
	
	private Category_1 parent;
	
	public MenuItem() {
		
	}
	
	public MenuItem(String name, String price, String description, Integer itemType) {
		super();
		this.name = name;
		this.price = price;
		this.description = description;
		this.itemType = itemType;
	}
	
	public Integer getItemType() {
		return itemType;
	}

	public void setItemType(Integer itemType) {
		this.itemType = itemType;
	}

	public Long getMenuItemId() {
		return menuItemId;
	}

	public void setMenuItemId(Long menuItemId) {
		this.menuItemId = menuItemId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Category_1 getParent() {
		return parent;
	}

	public void setParent(Category_1 parent) {
		this.parent = parent;
	}

}
