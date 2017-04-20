package liberty;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "OfficeHour")
public class OfficeHour {
	protected String officeId;
	protected Integer dayOrder;
	protected String phoneNumber;
	protected String openTime;
	protected String closeTime;
	protected String apptOnly;
	protected Integer reportId;
	
	@XmlElement(name = "OfficeID")
	public String getOfficeId() {
		return officeId;
	}
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
	
	@XmlElement(name = "DayOrder")
	public Integer getDayOrder() {
		return dayOrder;
	}
	public void setDayOrder(Integer dayOrder) {
		this.dayOrder = dayOrder;
	}
	
	@XmlElement(name = "PhoneNumber")
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	@XmlElement(name = "OpenTime")
	public String getOpenTime() {
		return openTime;
	}
	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}
	
	@XmlElement(name = "CloseTime")
	public String getCloseTime() {
		return closeTime;
	}
	public void setCloseTime(String closeTime) {
		this.closeTime = closeTime;
	}
	
	@XmlElement(name = "ApptOnly")
	public String getApptOnly() {
		return apptOnly;
	}	
	public void setApptOnly(String apptOnly) {
		this.apptOnly = apptOnly;
	}

	@XmlElement(name = "ReportID")
	public Integer getReportId() {
		return reportId;
	}
	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}
	
	public String getDay() {
		switch (dayOrder) {
		case 1:
			return "Sun";
		case 2:
			return "Mon";
		case 3:
			return "Tue";
		case 4:
			return "Wed";
		case 5:
			return "Thu";
		case 6:
			return "Fri";
		case 7:
			return "Sat";		
		}
		return null;
	}
	
	public List<OfficeHour> findByOfficeId(List<OfficeHour> tlist, String id) {
		List<OfficeHour> res = new ArrayList<OfficeHour>();
		for (OfficeHour oh : tlist)
			if (oh.getOfficeId().equals(id))
				res.add(oh);
		
		return res;
	}
	
	//get a list of unique officeIds from a list of officeHours
	public List<String> getOfficeIds(List<OfficeHour> tlist) {		
		List<String> officeIds = new ArrayList<String>();
		
		for (OfficeHour oh : tlist) {
			if (! officeIds.contains(oh.getOfficeId())) {
				officeIds.add(oh.getOfficeId());
			}
		}
		
		return officeIds;
	}
}
