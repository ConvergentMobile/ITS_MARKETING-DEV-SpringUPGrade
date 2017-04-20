package user;

import java.util.List;

public class Report {
	protected Integer reportId;
	protected String name;
	protected String description;
	protected Integer sortOrder;
	protected Integer reportType;
	protected Boolean isMulti; //report can be run for multiple keywords
	protected Integer siteId;
	
	public Report() {	
	}

	public Integer getReportId() {
		return reportId;
	}

	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Integer getReportType() {
		return reportType;
	}

	public void setReportType(Integer reportType) {
		this.reportType = reportType;
	}

	public Boolean getIsMulti() {
		return isMulti;
	}

	public void setIsMulti(Boolean isMulti) {
		this.isMulti = isMulti;
	}

	public Integer getSiteId() {
		return siteId;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	//utility function to get Report from a list, given reportType
	public Report findReport(List<Report> reports, int reportId) {
		for (Report report : reports)
			if (report.getReportId() == reportId)
				return report;
		return null;
	}
}
