package reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import dao.LTReportDAOManager;

public class LTGenerateReportData {
	protected static final Logger logger = Logger.getLogger(LTGenerateReportData.class);
	protected Map params;
	protected Integer reportId;
	protected List<String> reportColumnHeaders;
	protected Integer reportType;
	protected LTReportDAOManager rptDAO = new LTReportDAOManager();
	protected List<ReportData> reportRows;
	
	protected Map<String, String> summaryRow = new HashMap<String, String>();
	
	public LTGenerateReportData(Map params, Integer reportType) {
		this.params = params;
		this.reportType = reportType;
	}

	public List<ReportData> getReportRows() {
		return reportRows;
	}

	public void setReportRows(List<ReportData> reportRows) {
		this.reportRows = reportRows;
	}

	public Map getParams() {
		return params;
	}

	public void setParams(Map params) {
		this.params = params;
	}

	public Integer getReportId() {
		return reportId;
	}

	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}

	public List<String> getReportColumnHeaders() {
		return reportColumnHeaders;
	}

	public void setReportColumnHeaders(List<String> reportColumnHeaders) {
		this.reportColumnHeaders = reportColumnHeaders;
	}

	public Integer getReportType() {
		return reportType;
	}

	public void setReportType(Integer reportType) {
		this.reportType = reportType;
	}

	public Map<String, String> getSummaryRow() {
		return summaryRow;
	}

	public void setSummaryRow(Map<String, String> summaryRow) {
		this.summaryRow = summaryRow;
	}

	public List<ReportData> runReport(Map params,  int offset, int numRecords, String sortField, String sortOrder) throws Exception {		
		List<String> reportColHeaders = new ArrayList<String>();
    	int col = 0;

        try {
        	switch(reportType) {
        	case 1:
        		reportRows = rptDAO.getLTKeywordSummary(params, offset, numRecords, sortField, sortOrder);
	        	col = 0;
				reportColHeaders.add(col++, "Keyword");
				reportColHeaders.add(col++, "Campaign");
				reportColHeaders.add(col++, "Date");				
				reportColHeaders.add(col++, "Message");				
				reportColHeaders.add(col++, "Count");
				this.setReportColumnHeaders(reportColHeaders);
	        	return reportRows;
        	case 3:
        		reportRows = rptDAO.getByCampaignLT(params, offset, numRecords, sortField, sortOrder);
        		col = 0;
				reportColHeaders.add(col++, "Campaign Name");
				reportColHeaders.add(col++, "Keyword");
				reportColHeaders.add(col++, " Timestamp");      
				reportColHeaders.add(col++, "Phone");      
				reportColHeaders.add(col++, "Status");        				
				this.setReportColumnHeaders(reportColHeaders);				
	        	return reportRows;	 
        	case 7:
        		reportRows = rptDAO.getScheduledTriggers(params, offset, numRecords, sortField, sortOrder);
        		col = 0;
				reportColHeaders.add(col++, "Campaign Name");
				reportColHeaders.add(col++, "Message");        		
				reportColHeaders.add(col++, "Delivery Date");      
				this.setReportColumnHeaders(reportColHeaders);	        		
	        	return reportRows;		     
        	case 8: //msg quota
        		reportRows = rptDAO.getMsgQuota(params, offset, numRecords, sortField, sortOrder);
        		col = 0;
				reportColHeaders.add(col++, "Entity Id");
				reportColHeaders.add(col++, "Messages Allowed");        		
				reportColHeaders.add(col++, "Messages Used");      
				this.setReportColumnHeaders(reportColHeaders);	        		
	        	return reportRows;		
        	case 9: //list of optins
        		reportRows = rptDAO.getOptins(params, offset, numRecords, sortField, sortOrder);
        		col = 0;
				reportColHeaders.add(col++, "Keyword");
				reportColHeaders.add(col++, "Mobile Phone");        		
				reportColHeaders.add(col++, "Firstname");   
				reportColHeaders.add(col++, "Lastname");      
				reportColHeaders.add(col++, "Timestamp");      
				this.setReportColumnHeaders(reportColHeaders);	        		
	        	return reportRows;		  
        	case 10: //campaign summary
        		reportRows = rptDAO.getCampaignSummary(params, offset, numRecords, sortField, sortOrder);
        		col = 0;
				reportColHeaders.add(col++, "Campaign Name");
				reportColHeaders.add(col++, "Date");        		
				reportColHeaders.add(col++, "Message");   
				reportColHeaders.add(col++, "Count");      
				this.setReportColumnHeaders(reportColHeaders);	        		
	        	return reportRows;			
        	case 11: //campaign detail
        		reportRows = rptDAO.getCampaignDetail(params, offset, numRecords, sortField, sortOrder);
        		col = 0;       		
				reportColHeaders.add(col++, "Name");   
				reportColHeaders.add(col++, "Mobile Phone");      
				reportColHeaders.add(col++, "Status");
				reportColHeaders.add(col++, "Date"); 				
				this.setReportColumnHeaders(reportColHeaders);	    
			
				this.summaryRow.put("Campaign Name", reportRows.get(0).getColumn5());
				this.summaryRow.put("Message Text", reportRows.get(0).getColumn7());
				
	        	return reportRows;	   
        	case 12: //list of optouts
        		reportRows = rptDAO.getOptouts(params, offset, numRecords, sortField, sortOrder);
        		col = 0;
				reportColHeaders.add(col++, "Keyword");
				reportColHeaders.add(col++, "Mobile Phone");        		     
				reportColHeaders.add(col++, "Timestamp");   
				reportColHeaders.add(col++, "Name");   
				this.setReportColumnHeaders(reportColHeaders);	        		
	        	return reportRows;		
        	case 13: //SAF campaign detail
        		reportRows = rptDAO.getSAFCampaignDetail(params, offset, numRecords, sortField, sortOrder);
        		col = 0;       		
				reportColHeaders.add(col++, "Name");   
				reportColHeaders.add(col++, "Mobile Phone");      
				reportColHeaders.add(col++, "Status");
				reportColHeaders.add(col++, "Date"); 				
				this.setReportColumnHeaders(reportColHeaders);	    
			
				this.summaryRow.put("Campaign Name", reportRows.get(0).getColumn5());
				//this.summaryRow.put("Message Text", reportRows.get(0).getColumn7());
				
	        	return reportRows;	  	        	
        	default:
        		logger.error("Unknown report");
        		break;
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        	throw new Exception(e.getMessage());
        }
        
		return null;
	}
}
