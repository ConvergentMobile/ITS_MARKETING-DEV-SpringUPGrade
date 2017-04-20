package user;

import java.io.Serializable;
import java.util.List;

public class TargetUserList implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String listId;
	private Long userId;
	private String listName;
	private String listPath;
	private String listDisplayPath;
	private Boolean isSelected; 
	private String listType; //type of list - Marketing, Ops, etc.
	
	public TargetUserList() {
		
	}

	public String getListId() {
		return listId;
	}

	public void setListId(String listId) {
		this.listId = listId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	public String getListPath() {
		return listPath;
	}

	public void setListPath(String listPath) {
		this.listPath = listPath;
	}

	public String getListDisplayPath() {
		return listDisplayPath;
	}

	public void setListDisplayPath(String listDisplayPath) {
		this.listDisplayPath = listDisplayPath;
	}
	
	public Boolean getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(Boolean isSelected) {
		this.isSelected = isSelected;
	}

	public String getListType() {
		return listType;
	}

	public void setListType(String listType) {
		this.listType = listType;
	}

	public TargetUserList findById(String id, List<TargetUserList> list) {
		for (TargetUserList tul : list)
			if (id.equals(tul.getListId()))
				return tul;
		
		return null;
	}
}
