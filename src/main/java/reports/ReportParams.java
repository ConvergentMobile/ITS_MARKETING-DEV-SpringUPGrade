package reports;

import java.io.Serializable;
import java.util.Map;

public class ReportParams implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected Integer reportId;
	protected String paramName;
	protected String paramLabel;
	protected Object paramValue;
	protected String paramLOV; //contains the name of the Map holding the lov (Label/Value)
	
	public Integer getReportId() {
		return reportId;
	}
	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public String getParamLabel() {
		return paramLabel;
	}
	public void setParamLabel(String paramLabel) {
		this.paramLabel = paramLabel;
	}
	public Object getParamValue() {
		return paramValue;
	}
	public void setParamValue(Object paramValue) {
		this.paramValue = paramValue;
	}
	public String getParamLOV() {
		return paramLOV;
	}
	public void setParamLOV(String paramLOV) {
		this.paramLOV = paramLOV;
	}

}
