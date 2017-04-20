package user;

import java.io.Serializable;

import org.apache.log4j.Logger;

//used for mobile search
public class SearchAttribute implements Serializable {
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(SearchAttribute.class);
	
	private String id;
	private String searchKeywords;
	
	public SearchAttribute() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSearchKeywords() {
		return searchKeywords;
	}

	public void setSearchKeywords(String searchKeywords) {
		this.searchKeywords = searchKeywords;
	}
	
}
