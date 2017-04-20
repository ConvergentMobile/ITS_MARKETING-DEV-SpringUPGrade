package user;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TargetListData implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected String listId;
	protected String mobilePhone;
	protected Date lastUpdated;
	protected String lastName;
	protected String firstName;
	protected String address1;
	protected String address2;
	
	public TargetListData() {
		
	}

	public TargetListData(String listId, String mobilePhone, String lastName,
			String firstName, String address1, String address2) {
		super();
		this.listId = listId;
		this.mobilePhone = mobilePhone;
		this.lastName = lastName;
		this.firstName = firstName;
		this.address1 = address1;
		this.address2 = address2;
	}

	public TargetListData(String listId, String mobilePhone) {
		this.listId = listId;
		this.mobilePhone = mobilePhone;
	}
	
	public String getListId() {
		return listId;
	}

	public void setListId(String listId) {
		this.listId = listId;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public String getLastUpdatedAsStr() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		if (lastUpdated != null)
			return sdf.format(lastUpdated);
		
		return "";
	}
	
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

}
