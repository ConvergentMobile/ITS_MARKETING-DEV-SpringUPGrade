package user;

import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts.action.*;

import admin_user.UserProfileVO;

public class ReportForm extends ActionForm {
	private static Logger logger = Logger.getLogger(ReportForm.class);
	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

	private Date fromDate;
	private Date toDate;
	private Long[] userIds;
	private String reportName;
	private Integer reportId;
	private int reportType;
	private List<UserProfileVO> profiles;
	private Boolean isMulti;
	
	/*
	public String getFromDate() {
		return (fromDate != null) ? sdf.format(fromDate) : null;
	}
	
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	
	public void setFromDate(String fromDate) {
		try {
			if (fromDate != null)
				this.fromDate = sdf.parse(fromDate);
		} catch (ParseException e) {
			logger.error("ReportForm: invalid date format");
		}
	}
	
	public String getToDate() {
		return (toDate != null) ? sdf.format(toDate) : null;
	}
	
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public void setToDate(String toDate) {
		try {
			if (toDate != null)
				this.toDate = sdf.parse(toDate);
		} catch (ParseException e) {
			logger.error("ReportForm: invalid date format");
		}
	}
	*/

	public String getFromDateAsString() {
		return (fromDate != null) ? sdf.format(fromDate) : "(Click Calendar)";
	}
	
	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Boolean getIsMulti() {
		return isMulti;
	}

	public void setIsMulti(Boolean isMulti) {
		this.isMulti = isMulti;
	}

	public void setFromDateAsString(String fromDate) throws Exception {
		try {
			if (fromDate == null || fromDate.length() <= 0 || fromDate.equals("(Click Calendar)"))
				this.fromDate = null;
			else
				this.fromDate = sdf.parse(fromDate);
		} catch (ParseException e) {
			logger.error("Report: invalid date format");
			throw new Exception("Offer: invalid date format" + e);
		}
	}
	
	public String getToDateAsString() {
		return (toDate != null) ? sdf.format(toDate) : "(Click Calendar)";
	}
	
	public void setToDateAsString(String toDate) throws Exception {
		try {
			if (toDate == null || toDate.length() <= 0 || toDate.equals("(Click Calendar)"))
				this.toDate = null;
			else
				this.toDate = sdf.parse(toDate);
		} catch (ParseException e) {
			logger.error("Offer: invalid date format");
			throw new Exception("Offer: invalid date format" + e);
		}
	}	
	
	public Long[] getUserIds() {
		return userIds;
	}

	public void setUserIds(Long[] userIds) {
		this.userIds = userIds;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public Integer getReportId() {
		return reportId;
	}

	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}

	public int getReportType() {
		return reportType;
	}

	public void setReportType(int reportType) {
		this.reportType = reportType;
	}

	public List<UserProfileVO> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<UserProfileVO> profiles) {
		this.profiles = profiles;
	}

	public void reset(ActionMapping mapping, HttpServletRequest request) {
		/*
		try {
			this.fromDate = sdf.parse(sdf.format(Calendar.getInstance().getTime()));
			this.toDate = sdf.parse(sdf.format(Calendar.getInstance().getTime()));
//			this.toDate = sdf.parse("1/1/2900");
		} catch (ParseException e) {
			logger.error("ReportForm - " + e);
		}
		*/
	}
}
