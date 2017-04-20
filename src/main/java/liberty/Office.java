package liberty;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Office")
public class Office {
	protected String officeId;
	protected String territoryId;
	protected String officeName;
	protected String phoneNumber;
	protected String address;
	protected String city;
	protected String state;
	protected String zip;
	protected String officeEmail;
	protected Integer officeStatus;
	private Integer regionId;
	protected String address2;
	protected String altPhoneNumber;
	protected Boolean hispanicMktgId;
	protected String creditCardName;
	protected String storeUrl;

	@XmlElement(name = "OfficeID")
	public String getOfficeId() {
		return officeId;
	}
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
	
	@XmlElement(name = "TerritoryID")
	public String getTerritoryId() {
		return territoryId;
	}
	public void setTerritoryId(String territoryId) {
		this.territoryId = territoryId;
	}
	
	@XmlElement(name = "OfficeName")
	public String getOfficeName() {
		return officeName;
	}
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	
	@XmlElement(name = "PhoneNumber")
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	@XmlElement(name = "Address")
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	@XmlElement(name = "City")
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	@XmlElement(name = "StateProvince")
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	@XmlElement(name = "ZipPostalCode")
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	
	@XmlElement(name = "OfficeEmail")
	public String getOfficeEmail() {
		return officeEmail;
	}
	public void setOfficeEmail(String officeEmail) {
		this.officeEmail = officeEmail;
	}
	
	@XmlElement(name = "OfficeStatus")
	public Integer getOfficeStatus() {
		return officeStatus;
	}
	
	public void setOfficeStatus(Integer officeStatus) {
		this.officeStatus = officeStatus;
	}

	@XmlElement(name = "RegionID")
	public Integer getRegionId() {
		return regionId;
	}
	public void setRegionId(Integer regionId) {
		this.regionId = regionId;
	}
	
	@XmlElement(name = "AddressLine2")
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	
	@XmlElement(name = "AltPhoneNumber")
	public String getAltPhoneNumber() {
		return altPhoneNumber;
	}
	public void setAltPhoneNumber(String altPhoneNumber) {
		this.altPhoneNumber = altPhoneNumber;
	}
	
	@XmlElement(name = "HispanicMarketingID")
	public Boolean getHispanicMktgId() {
		return hispanicMktgId;
	}
	public void setHispanicMktgId(Boolean hispanicMktgId) {
		this.hispanicMktgId = hispanicMktgId;
	}
	
	@XmlElement(name = "CreditCardName")
	public String getCreditCardName() {
		return creditCardName;
	}
	public void setCreditCardName(String creditCardName) {
		this.creditCardName = creditCardName;
	}
		
	@XmlElement(name = "StoreFrontUrl")
	public String getStoreUrl() {
		return storeUrl;
	}
	public void setStoreUrl(String storeUrl) {
		this.storeUrl = storeUrl;
	}
	//utility functions
	public List<Office> findByTerritoryId(List<Office> tlist, String id) {
		List<Office> res = new ArrayList<Office>();

		for (Office t : tlist)
			if (t.getTerritoryId().equals(id.toUpperCase()))
				res.add(t);
		
		return res;
	}
	
	public Office findByOfficeId(List<Office> tlist, String id) {
		for (Office t : tlist)
			if (t.getOfficeId().equals(id))
				return t;
		
		return null;
	}
}
