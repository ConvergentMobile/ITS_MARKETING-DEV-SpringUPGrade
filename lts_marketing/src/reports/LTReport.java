package reports;

import java.util.List;

import user.Report;

public class LTReport extends Report {
	List<ReportParams> params;

	public List<ReportParams> getParams() {
		return params;
	}

	public void setParams(List<ReportParams> params) {
		this.params = params;
	}
}
