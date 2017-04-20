package category_1;

import java.io.Serializable;

import org.apache.log4j.Logger;

//this class describes a menu section e.g lunch appetizers or dinner entrees
public class MenuSection implements Serializable {
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(MenuSection.class);
	
	private String id;
	private String name;
	private String contents;
	private Integer type;
	private String introText;
	private String itemLineFont; //font for the main line
	private String descLineFont; //font for the description
	
	public MenuSection() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getIntroText() {
		return introText;
	}

	public void setIntroText(String introText) {
		this.introText = introText;
	}

	public String getItemLineFont() {
		return itemLineFont;
	}

	public void setItemLineFont(String itemLineFont) {
		this.itemLineFont = itemLineFont;
	}

	public String getDescLineFont() {
		return descLineFont;
	}

	public void setDescLineFont(String descLineFont) {
		this.descLineFont = descLineFont;
	}
	
}
