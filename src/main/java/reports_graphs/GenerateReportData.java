package reports_graphs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mdp_common.DBStuff;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.DatasetProducer;
import de.laures.cewolf.links.CategoryItemLinkGenerator;
import de.laures.cewolf.tooltips.CategoryToolTipGenerator;

public class GenerateReportData implements DatasetProducer, CategoryToolTipGenerator, CategoryItemLinkGenerator, Serializable {
	protected static final long serialVersionUID = 1L;
	protected static final Logger logger = Logger.getLogger(GenerateReportData.class);
	protected Session session = null;
	protected DBStuff dbs = null;
	protected Map params;
	protected Integer reportId;
	protected List<ReportData> reportData;
	protected List<String> reportColumnHeaders;
	protected ReportDAOManager dao;
	protected int reportType;
	
	public GenerateReportData() {
		
	}
	
	public GenerateReportData(Map params, Integer reportType) {
		this.params = params;
		this.reportType = reportType;
		this.dao = new ReportDAOManager();
	}

	public Map getParams() {
		return params;
	}

	public void setParams(Map params) {
		this.params = params;
	}

	public int getReportType() {
		return reportType;
	}

	public void setReportType(int reportType) {
		this.reportType = reportType;
	}

	public List<ReportData> getReportData() {
		return reportData;
	}

	public void setReportData(List<ReportData> reportData) {
		this.reportData = reportData;
	}

	public List<String> getReportColumnHeaders() {
		return reportColumnHeaders;
	}

	public void setReportColumnHeaders(List<String> reportColumnHeaders) {
		this.reportColumnHeaders = reportColumnHeaders;
	}

	@Override
	public String getProducerId() {
		// TODO Auto-generated method stub
		return "ReportDataProducer";
	}

	@Override
	public boolean hasExpired(Map params, Date since) {
		// TODO Auto-generated method stub
        logger.debug(getClass().getName() + "hasExpired()");
		return (System.currentTimeMillis() - since.getTime())  > 5000;
	}

	@Override
	public Object produceDataset(Map params1) throws DatasetProduceException {
		//this.params = params1;
		
        DefaultCategoryDataset cDataset = new DefaultCategoryDataset(){
			protected void finalize() throws Throwable {
				super.finalize();
				logger.debug(this +" finalized.");
			}
        };
        
        DefaultPieDataset pDataset = new DefaultPieDataset(){
			protected void finalize() throws Throwable {
				super.finalize();
				logger.debug(this +" finalized.");
			}
        };
        
		List<String> reportColHeaders = new ArrayList<String>();

        try {
        	switch(reportType) {
        	case 1:
	        	reportData = dao.getKeywordSummary(params, cDataset);
				reportColHeaders.add(0, "Keyword");
				reportColHeaders.add(1, "Campaign");
				reportColHeaders.add(2, "Count");
				this.setReportColumnHeaders(reportColHeaders);
	        	return cDataset;
        	case 2:
	        	reportData = dao.getCountByDayOfWeek(params, cDataset);
				reportColHeaders.add(0, "Day");
				reportColHeaders.add(1, "Incoming");
				reportColHeaders.add(2, "Outgoing");	
				this.setReportColumnHeaders(reportColHeaders);				
	        	return cDataset;	
        	case 3:
        		reportData = dao.getByCampaign(params, cDataset);
				reportColHeaders.add(0, "Campaign Name");
				reportColHeaders.add(1, "Keyword");
				reportColHeaders.add(2, "Start Date");      
				reportColHeaders.add(3, "Message");        		
				reportColHeaders.add(4, "Count");        
				this.setReportColumnHeaders(reportColHeaders);				
        		return cDataset;	        	
        	case 4:
        		reportData = dao.getCountByAreaCode(params, cDataset);
				reportColHeaders.add(0, "Area Code");
				reportColHeaders.add(1, "Incoming");
				reportColHeaders.add(2, "Outgoing");  
				this.setReportColumnHeaders(reportColHeaders);				
        		return cDataset;
        	case 5:
        		reportData = dao.getCountByHour(params, cDataset);
				reportColHeaders.add(0, "Hour");
				reportColHeaders.add(1, "Incoming");
				reportColHeaders.add(2, "Outgoing");     
				this.setReportColumnHeaders(reportColHeaders);				
        		return cDataset;   
        	case 6:
        		reportData = dao.getByOffer(params, cDataset);
				reportColHeaders.add(0, "Name");
				reportColHeaders.add(1, "Expiration Date");
				reportColHeaders.add(2, "Code");     
				reportColHeaders.add(3, "Description"); 
				this.setReportColumnHeaders(reportColHeaders);				
        		return cDataset;        	
        	case 7:
        		reportData = dao.getScheduledTriggers(params, cDataset);
        		return cDataset;    
        	case 8:
	        	reportData = dao.getMsgCount(params, cDataset);
	        	return cDataset;	
        	case 9:
	        	reportData = dao.getOptIns(params, cDataset);
	        	return cDataset;	
        	case 10:
	        	reportData = dao.getKWActivity(params, cDataset);
	        	return cDataset;	        	
        	default:
        		logger.error("Unknown report");
        		break;
        	}
        } catch (Exception e) {
        	throw new DatasetProduceException(e.getMessage());
        }
        
		return pDataset;
	}

	@Override
	public String generateToolTip(CategoryDataset arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateLink(Object arg0, int arg1, Object arg2) {
		// TODO Auto-generated method stub
		return null;
	}
}
