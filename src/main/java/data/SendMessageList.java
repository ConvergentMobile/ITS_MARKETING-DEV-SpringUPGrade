package data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import user.TargetUserList;

public class SendMessageList implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected String keyword;
	protected List<TargetUserList> tuList = new ArrayList<TargetUserList>();
	
	public SendMessageList() {
		
	}

	public SendMessageList(String keyword, List<TargetUserList> tuList) {
		super();
		this.keyword = keyword;
		this.tuList = tuList;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public List<TargetUserList> getTuList() {
		return tuList;
	}

	public void setTuList(List<TargetUserList> tuList) {
		this.tuList = tuList;
	}

}
