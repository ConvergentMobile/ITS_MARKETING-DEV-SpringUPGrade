package dao;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import mdp_common.DBStuff;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BlobType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.jfree.data.category.DefaultCategoryDataset;
import org.quartz.JobDataMap;

import reports.LTReport;
import reports.ReportData;
import reports_graphs.ReportDAOManager;
import user.Campaign;
import util.PropertyUtil;

import common.HibernateUtil;

import data.ValueObject;

public class LTReportDAOManager extends ReportDAOManager {
	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = Logger.getLogger(LTReportDAOManager.class);

	public List<LTReport> getReports(Integer siteId) throws Exception {
		String sql = "from LTReport as r where r.siteId = ?";

		try {
			session = HibernateUtil.currentSession();
			Query q = session.createQuery(sql);
			List<LTReport> reports = q.setInteger(0, siteId)
								.list();

			return reports;
		} finally {
			close();
		}
	}
	
	public List<ValueObject> getReportParams(Integer reportId) throws Exception {
		String sql = "select rp.report_id field1, rp.param_name field2, rp.param_label field3"
					+ " from report_params rp"
					+ " where r.report_id = ?";
				
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql)
					.addScalar("field1", new IntegerType())
					.addScalar("field2", new StringType())
					.addScalar("field3", new StringType())
					.setResultTransformer(Transformers.aliasToBean(ValueObject.class));
			
			List<ValueObject> reportList = q.setInteger(0, reportId).list();
			
			return reportList;
		} finally  {
			close();
		}
	}
	
	public List<ReportData> getKeywordMsgCount() throws Exception {
		String sql = "{call keyword_msg_count_sp()}";
		
		logger.debug("sql: " + sql);

		List<ReportData> reportDataList = null;
		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql)
					.addScalar("custom_field_1", new StringType())					
					.addScalar("custom_field_2", new StringType())
					.addScalar("keyword", new StringType())
					.addScalar("optins", new StringType())	
					.addScalar("msgcnt", new StringType())										
					.setResultTransformer(Transformers.aliasToBean(ReportData.class));						
			
			reportDataList = q.list();
		} finally  {
			close();
		}
		return reportDataList;
	}	
	
	public List<ReportData> getKeywordMsgCountAD(Long userId) throws Exception {
		String sql = "{call keyword_msg_count_ad_sp(?)}";
		
		logger.debug("sql: " + sql);

		List<ReportData> reportDataList = new ArrayList<ReportData>();
		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql)
					.addScalar("custom_field_1", new StringType())					
					.addScalar("custom_field_2", new StringType())
					.addScalar("keyword", new StringType())
					.addScalar("optins", new StringType())	
					.addScalar("msgcnt", new StringType())										
					.setResultTransformer(Transformers.aliasToBean(ReportData.class));						
			
			reportDataList = q.list();
		} finally  {
			close();
		}
		return reportDataList;
	}

	public List<ReportData> getByCampaignLT(Map params, int offset, int numRecords, String sortField, String sortOrder) throws Exception {
		String userIds = (String) params.get("userIds");
		
		String sql = "SELECT c.campaign_id column1, c.name column2, c.start_date column3, c.keyword column4,"
					+ " c.message_text column5, count(m.mobile_phone) column6"
					+ " FROM campaign c, message_statistics_detail m"
					+ " where c.campaign_id = m.campaign_id"
					+ " and c.user_id in (" + userIds + ")"
					+ " and c.list_id != 'Multi'"
					+ " group by column4, column1"
					
					+ " UNION "
					
					+ " SELECT c.campaign_id column1, c.name column2, c.start_date column3, c.keyword column4,"
					+ " c.message_text column5, count(m.mobile_phone) column6"
					+ " FROM campaign c, message_statistics_detail m, campaign_multi_list cml"
					+ " where c.campaign_id = m.campaign_id"
					+ " and c.list_id = 'Multi'"
					+ " and m.customer_id_1 in (" + userIds + ")"
					+ " and cml.campaign_id = c.campaign_id"
					+ " group by column4, column1";		
		
		sql = "SELECT SQL_CALC_FOUND_ROWS c.name column1, c.keyword column2, m.last_updated column3,"
					+ " m.mobile_phone column4, m.status column5"
					+ " FROM campaign c, message_statistics_detail m, user u"
					+ " where c.campaign_id = m.campaign_id"
					+ " and u.user_id in (" + userIds + ")"
					+ " and u.keyword = m.keyword";
		
		Vector<Object> args = new Vector<Object>();
		
		if (params.get("fromDate") != null) {
			sql += " and m.last_updated between ? and ?";
			args.add(params.get("fromDate"));
			args.add(params.get("toDate"));
		}
	
		//String groupBy = " group by kw, cid"; 
		
		//String orderBy = " order by kw, cid";
		String orderBy = " order by column2, column1, column3, column4 desc";
		
		//sql += groupBy;
		sql += orderBy;
		
		sql += " limit " + offset + ", " + numRecords;

		logger.debug("sql: " + sql);
		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql)
					.addScalar("column1", new StringType())
					.addScalar("column2", new StringType())		
					.addScalar("column3", new StringType())									
					.addScalar("column4", new StringType())									
					.addScalar("column5", new StringType())									
					.setResultTransformer(Transformers.aliasToBean(ReportData.class));	
			
			int pos = 0;
			for (Object arg : args) {
				q.setString(pos++, arg.toString());
			}
			
			List<ReportData> reportDataList = q.list();

			String sql1 = "select found_rows() column1";
			q = session.createSQLQuery(sql1)
					.addScalar("column1", new StringType())														
					.setResultTransformer(Transformers.aliasToBean(ReportData.class));			

			List<ReportData> reportDataList1 = q.list();

			reportDataList.addAll(reportDataList1);
			
			return reportDataList;
		} finally  {
			close();
		}
	}	
	
	public List<ReportData> getLTKeywordSummary(Map params,  int offset, int numRecords, String sortField, String sortOrder) throws Exception {
		String groupBy = " group by column1, cid";
		String orderBy = " order by column1, column4";
		String dateClause = "";
		
		String userIds = (String) params.get("userIds");
		
		String sqla = "SELECT SQL_CALC_FOUND_ROWS count(*) column5, m.campaign_id cid,"
				//+ " if (status = 'Incoming', 'Incoming', 'Outgoing') status, c.name"
				+ " c.name column2, m.keyword column1, c.start_date column3, c.message_text column4"
				+ " FROM user u, message_statistics_detail m"
				+ " left join campaign c on m.campaign_id = c.campaign_id"
				+ " WHERE m.keyword = u.keyword"
				+ " and m.campaign_id != '0'" //exclude inbounds
				+ " and u.user_id in (" + userIds + ")"
				+ " and u.user_id = c.user_id";
					
		/*
		String sqlb = " SELECT count(*) column1, m.campaign_id cid,"
				+ " c.name column2, m.keyword column3"
				+ " FROM campaign_multi_list cml, message_statistics_detail m"
				+ " left join campaign c on m.campaign_id = c.campaign_id"
				+ " WHERE  m.customer_id_1 in (" + userIds + ")"
				+ " and cml.campaign_id = m.campaign_id"
				+ " and cml.campaign_id = c.campaign_id";
		*/

		String sqlb = " SELECT count(*) column5, m.campaign_id cid,"
				+ " c.name column2, m.keyword column1, c.start_date column3, c.message_text column4"
				+ " FROM campaign c, message_statistics_detail m"
				+ " WHERE  m.customer_id_1 in (" + userIds + ")"
				+ " and c.campaign_id = m.campaign_id";
		
		Vector<Object> args = new Vector<Object>();
		//args.add(params.get("userIds"));
		//args.add(params.get("userIds"));

		if (params.get("fromDate") != null) {
			dateClause = " and m.last_updated between ? and ?";
			args.add(params.get("fromDate"));
			args.add(params.get("toDate"));		
			args.add(params.get("fromDate"));
			args.add(params.get("toDate"));				
		}
		
		String sql = new StringBuffer(sqla).append(dateClause).append(groupBy)
							.append(" UNION ").append(sqlb).append(dateClause).append(groupBy)
							.append(orderBy).toString();
		
		sql += " limit " + offset + ", " + numRecords;

		logger.debug("sql: " + sql);
		logger.debug("uids: " + params.get("userIds"));
							
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql)
					.addScalar("column1", new StringType())						
					.addScalar("column2", new StringType())
					.addScalar("column3", new StringType())										
					.addScalar("column4", new StringType())	
					.addScalar("column5", new StringType())	
					.setResultTransformer(Transformers.aliasToBean(ReportData.class));	
			
			int pos = 0;
			for (Object arg : args) {
				q.setString(pos++, arg.toString());
			}
			
			List<ReportData> reportDataList = q.list();

			String sql1 = "select found_rows() column1";
			q = session.createSQLQuery(sql1)
					.addScalar("column1", new StringType())														
					.setResultTransformer(Transformers.aliasToBean(ReportData.class));			

			List<ReportData> reportDataList1 = q.list();

			reportDataList.addAll(reportDataList1);
			
			return reportDataList;
		} finally {
			close();
		}
	}	
	
	public List<ReportData> getScheduledTriggers(Map params,  int offset, int numRecords, String sortField, String sortOrder) throws Exception {
		String sql = "SELECT trigger_name column1, next_fire_time column2, trigger_group column3, job_data obj1"
					+ " FROM " + PropertyUtil.load().getProperty("quartz_db") + ".QRTZ_TRIGGERS qt"
					+ " where qt.trigger_group in (?)";
		
		Vector<Object> args = new Vector<Object>();
		args.add(params.get("userIds"));
		
		String orderBy = " order by column2"; 
		
		sql += orderBy;
		
		logger.debug("sql: " + sql);

		Calendar cal = Calendar.getInstance();
		List<ReportData> rdList1 = new ArrayList<ReportData>();

		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql)
					.addScalar("column1", new StringType())					
					.addScalar("column2", new StringType())
					.addScalar("column3", new StringType())	
					.addScalar("obj1", new BlobType())									
					.setResultTransformer(Transformers.aliasToBean(ReportData.class));	
			
			int pos = 0;
			for (Object arg : args) {
				q.setString(pos++, arg.toString());
			}			
	
			List<ReportData> reportDataList = q.list();
			
			for (ReportData rd : reportDataList) {
			    InputStream is = ((Blob)rd.getObj1()).getBinaryStream();
			    ObjectInputStream ois = new ObjectInputStream(is);
			    Object obj = ois.readObject(); 
				JobDataMap jdmap = (JobDataMap) obj;
				Campaign camp = (Campaign) jdmap.get("campaign");
				cal.setTimeInMillis(Long.valueOf(rd.getColumn2()));		
				ReportData rd1 = new ReportData();
				rd1.setColumn1(camp.getName());
				rd1.setColumn2(camp.getMessageText());
				rd1.setColumn3(cal.getTime().toString());
				rd1.setColumn4(rd.getColumn1());				
				rd1.setColumn5(rd.getColumn3());
				rdList1.add(rd1);
			}
		} finally  {
			close();
		}
		return rdList1;
	}		
}
