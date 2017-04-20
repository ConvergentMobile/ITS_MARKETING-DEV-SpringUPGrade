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
import org.hibernate.query.Query;
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
		String sql = "from LTReport as r where r.siteId = ? order by sortOrder";

		try {
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql);
			List<LTReport> reports = q.setParameter(0, siteId)
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
			Query q = session.createNativeQuery(sql,ValueObject.class)
					.addScalar("field1", new IntegerType())
					.addScalar("field2", new StringType())
					.addScalar("field3", new StringType());
					//.setResultTransformer(Transformers.aliasToBean(ValueObject.class));
			
			List<ValueObject> reportList = q.setParameter(0, reportId).list();
			
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
			Query q = session.createNativeQuery(sql,ReportData.class)
					.addScalar("custom_field_1", new StringType())					
					.addScalar("custom_field_2", new StringType())
					.addScalar("keyword", new StringType())
					.addScalar("optins", new StringType())	
					.addScalar("msgcnt", new StringType());										
					//.setResultTransformer(Transformers.aliasToBean(ReportData.class));						
			
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
			Query q = session.createNativeQuery(sql,ReportData.class)
					.addScalar("custom_field_1", new StringType())					
					.addScalar("custom_field_2", new StringType())
					.addScalar("keyword", new StringType())
					.addScalar("optins", new StringType())	
					.addScalar("msgcnt", new StringType());										
					//.setResultTransformer(Transformers.aliasToBean(ReportData.class));						
			
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
					+ " and u.user_id = m.customer_id_1";
		
		Vector<Object> args = new Vector<Object>();
		
		if (params.get("fromDate") != null) {
			sql += " and m.last_updated >= ? and m.last_updated <= ?";			
			args.add(params.get("fromDate"));
			args.add(params.get("toDate") + " 24:00:00");
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
			Query q = session.createNativeQuery(sql,ReportData.class)
					.addScalar("column1", new StringType())
					.addScalar("column2", new StringType())		
					.addScalar("column3", new StringType())									
					.addScalar("column4", new StringType())									
					.addScalar("column5", new StringType());								
					//.setResultTransformer(Transformers.aliasToBean(ReportData.class));	
			
			int pos = 0;
			for (Object arg : args) {
				q.setParameter(pos++, arg.toString());
			}
			
			List<ReportData> reportDataList = q.list();

			String sql1 = "select found_rows() column1";
			q = session.createNativeQuery(sql1,ReportData.class)
					.addScalar("column1", new StringType());														
					//.setResultTransformer(Transformers.aliasToBean(ReportData.class));			

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
			dateClause = " and m.last_updated >= ? and m.last_updated <= ?";
			args.add(params.get("fromDate"));
			args.add(params.get("toDate") + " 24:00:00");		
			args.add(params.get("fromDate"));
			args.add(params.get("toDate") + " 24:00:00");				
		}
		
		String sql = new StringBuffer(sqla).append(dateClause).append(groupBy)
							.append(" UNION ").append(sqlb).append(dateClause).append(groupBy)
							.append(orderBy).toString();
		
		sql += " limit " + offset + ", " + numRecords;

		logger.debug("sql: " + sql);
		logger.debug("uids: " + params.get("userIds"));
							
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql,ReportData.class)
					.addScalar("column1", new StringType())						
					.addScalar("column2", new StringType())
					.addScalar("column3", new StringType())										
					.addScalar("column4", new StringType())	
					.addScalar("column5", new StringType());	
					//.setResultTransformer(Transformers.aliasToBean(ReportData.class));	
			
			int pos = 0;
			for (Object arg : args) {
				q.setParameter(pos++, arg.toString());
			}
			
			List<ReportData> reportDataList = q.list();

			String sql1 = "select found_rows() column1";
			q = session.createNativeQuery(sql1,ReportData.class)
					.addScalar("column1", new StringType());														
					//.setResultTransformer(Transformers.aliasToBean(ReportData.class));			

			List<ReportData> reportDataList1 = q.list();

			reportDataList.addAll(reportDataList1);
			
			return reportDataList;
		} finally {
			close();
		}
	}	
	
	public List<ReportData> getScheduledTriggers(Map params,  int offset, int numRecords, String sortField, String sortOrder) throws Exception {
		String sql = "SELECT SQL_CALC_FOUND_ROWS trigger_name column1, next_fire_time column2, trigger_group column3, job_data obj1"
					+ " FROM " + PropertyUtil.load().getProperty("quartz_db") + ".QRTZ_TRIGGERS qt"
					+ " where qt.trigger_group in (?)";
		
		Vector<Object> args = new Vector<Object>();
		args.add(params.get("userIds"));
		
		String orderBy = " order by column2"; 
		
		sql += orderBy;
	
		//sql += " limit " + offset + ", " + numRecords;

		logger.debug("sql: " + sql);

		Calendar cal = Calendar.getInstance();
		List<ReportData> rdList1 = new ArrayList<ReportData>();

		try {
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql,ReportData.class)
					.addScalar("column1", new StringType())					
					.addScalar("column2", new StringType())
					.addScalar("column3", new StringType())	
					.addScalar("obj1", new BlobType());									
					//.setResultTransformer(Transformers.aliasToBean(ReportData.class));	
			
			int pos = 0;
			for (Object arg : args) {
				q.setParameter(pos++, arg.toString());
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
			
			String sql1 = "select found_rows() column1";
			q = session.createNativeQuery(sql1,ReportData.class)
					.addScalar("column1", new StringType());													
					//.setResultTransformer(Transformers.aliasToBean(ReportData.class));			

			List<ReportData> reportDataList1 = q.list();

			rdList1.addAll(reportDataList1);
		} finally  {
			close();
		}
		return rdList1;
	}	
	
	//total quota of all msgs sent for Entity
	public List<ReportData> getMsgQuota(Map params, int offset, int numRecords, String sortField, String sortOrder) throws Exception {
		String sql = "select mc.entity_id column1, messages_allowed column2, messages_used column3"
					+ " from liberty_message_count mc, user_customer_defined ucd"
					+ " where ucd.user_id = ?"
					+ " and ucd.custom_field_1 = mc.entity_id";
		
		logger.debug("sql: " + sql);

		String userId = params.get("userIds").toString();
		
		List<ReportData> reportDataList = new ArrayList<ReportData>();
		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql,ReportData.class)
					.addScalar("column1", new StringType())					
					.addScalar("column2", new StringType())	
					.addScalar("column3", new StringType());										
					//.setResultTransformer(Transformers.aliasToBean(ReportData.class));						
			
			reportDataList = q.setParameter(0, userId)
								.list();
			
			String sql1 = "select found_rows() column1";
			q = session.createNativeQuery(sql1,ReportData.class)
					.addScalar("column1", new StringType());														
					//.setResultTransformer(Transformers.aliasToBean(ReportData.class));			

			List<ReportData> reportDataList1 = q.list();

			reportDataList.addAll(reportDataList1);			
		} finally  {
			close();
		}
		return reportDataList;
	}
	
	//optins list
	public List<ReportData> getOptins(Map params, int offset, int numRecords, String sortField, String sortOrder) throws Exception {
		String sql = "select u.keyword column1,"
					+ " tld.mobile_phone column2, tld.first_name column3, tld.last_name column4, tld.last_updated column5"
					+ " from user u, user_customer_defined ucd, target_user_list tul, target_list_data tld"
					+ " where ucd.user_id = ?"
					+ " and ucd.user_id = u.user_id"
					+ " and ucd.user_id = tul.user_id"
					+ " and tul.list_id = tld.list_id"
					+ " and tul.list_name = u.keyword";
	
		if (sortField != null && sortField.length() > 0)
			sql += " order by " + sortField;
		if (sortOrder != null && sortOrder.length() > 0)
			sql += " " + sortOrder;
		
		logger.debug("sql: " + sql);

		String userId = params.get("userIds").toString();

		List<ReportData> reportDataList = new ArrayList<ReportData>();
		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql,ReportData.class)
					.addScalar("column1", new StringType())					
					.addScalar("column2", new StringType())	
					.addScalar("column3", new StringType())	
					.addScalar("column4", new StringType())										
					.addScalar("column5", new StringType());															
					//.setResultTransformer(Transformers.aliasToBean(ReportData.class));						
			
			reportDataList = q.setParameter(0, userId)
					.list();
			
			String sql1 = "select found_rows() column1";
			q = session.createNativeQuery(sql1,ReportData.class)
					.addScalar("column1", new StringType());														
					//.setResultTransformer(Transformers.aliasToBean(ReportData.class));			

			List<ReportData> reportDataList1 = q.list();

			reportDataList.addAll(reportDataList1);
		} finally  {
			close();
		}
		return reportDataList;
	}
	
	//campaign summary
	public List<ReportData> getCampaignSummary(Map params, int offset, int numRecords, String sortField, String sortOrder) throws Exception {
		String sql = "select c.name column1, c.last_updated column2, c.message_text column3, "
					+ " (select count(*) from message_statistics_detail msd where msd.campaign_id = c.campaign_id) column4"
					+ " from campaign c"
					+ " where c.user_id = ?";
		
		if (sortField != null && sortField.length() > 0)
			sql += " order by " + sortField;
		if (sortOrder != null && sortOrder.length() > 0)
			sql += " " + sortOrder;
		
		logger.debug("sql: " + sql);

		String userId = params.get("userIds").toString();

		List<ReportData> reportDataList = new ArrayList<ReportData>();
		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql,ReportData.class)
					.addScalar("column1", new StringType())					
					.addScalar("column2", new StringType())	
					.addScalar("column3", new StringType())	
					.addScalar("column4", new StringType());										
					//.setResultTransformer(Transformers.aliasToBean(ReportData.class));						
			
			reportDataList = q.setParameter(0, userId)
					.list();
			
			String sql1 = "select found_rows() column1";
			q = session.createNativeQuery(sql1,ReportData.class)
					.addScalar("column1", new StringType());														
					//.setResultTransformer(Transformers.aliasToBean(ReportData.class));			

			List<ReportData> reportDataList1 = q.list();

			reportDataList.addAll(reportDataList1);
		} finally  {
			close();
		}
		return reportDataList;
	}
	
	//get list of campaigns for the Detail report
	public List<ValueObject> getCampaignList(Long userId) throws Exception {
		String sql = "select campaign_id field1, name field2 from campaign where user_id = ?";
		
		List<ValueObject> ret = new ArrayList<ValueObject>();
		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql,ValueObject.class)
					.addScalar("field1", new StringType())					
					.addScalar("field2", new StringType());															
					//.setResultTransformer(Transformers.aliasToBean(ValueObject.class));						
			
			ret = q.setParameter(0, userId)
					.list();
		} finally  {
			close();
		}
		
		return ret;
	}
	
	//campaign detail
	public List<ReportData> getCampaignDetail(Map params, int offset, int numRecords, String sortField, String sortOrder) throws Exception {
		String sql = "select c.name column5, c.start_date column6, c.message_text column7, "
					+ " msd.mobile_phone column2, msd.status column3, msd.last_updated column4,"
					+ " (select concat(tld.first_name, ' ', tld.last_name) from target_list_data tld, target_user_list tul, user u"
					+ " 	where tld.mobile_phone = msd.mobile_phone"
					+ " 	and tul.list_id = tld.list_id"
					+ " 	and tul.user_id = u.user_id"
					+ "		and u.keyword = c.keyword) column1"
					+ " from campaign c, message_statistics_detail msd"
					+ " where msd.campaign_id = c.campaign_id"
					+ " and c.campaign_id = ?"
					+ " order by c.start_date desc";

		if (sortField != null && sortField.length() > 0)
			sql += " order by " + sortField;
		if (sortOrder != null && sortOrder.length() > 0)
			sql += " " + sortOrder;
		
		logger.debug("sql: " + sql);

		String campaignId = params.get("campaignId").toString();

		List<ReportData> reportDataList = new ArrayList<ReportData>();
		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql,ReportData.class)
					.addScalar("column1", new StringType())					
					.addScalar("column2", new StringType())	
					.addScalar("column3", new StringType())	
					.addScalar("column4", new StringType())				
					.addScalar("column5", new StringType())										
					.addScalar("column6", new StringType())										
					.addScalar("column7", new StringType());															
					//.setResultTransformer(Transformers.aliasToBean(ReportData.class));						
			
			reportDataList = q.setParameter(0, campaignId)
					.list();
			
			String sql1 = "select found_rows() column1";
			q = session.createNativeQuery(sql1,ReportData.class)
					.addScalar("column1", new StringType());														
					//.setResultTransformer(Transformers.aliasToBean(ReportData.class));			

			List<ReportData> reportDataList1 = q.list();

			reportDataList.addAll(reportDataList1);
		} finally  {
			close();
		}
		return reportDataList;
	}
	
	//optins list
	public List<ReportData> getOptouts(Map params, int offset, int numRecords, String sortField, String sortOrder) throws Exception {
		String sql = "select o.phone_number column1, u.keyword column2, o.last_updated column3,"
					+ " (select concat(tld.first_name, ' ', tld.last_name)"
					+ " from target_user_list tul, target_list_data tld"
					+ " where tul.list_id = tld.list_id"	
					+ " and tul.user_id = u.user_id"
					+ " and (o.phone_number = tld.mobile_phone"
					+ " 	or o.phone_number = concat('1', tld.mobile_phone))) column4"
					+ " from user u, opt_out o"
					+ " where u.user_id = ?"
					+ " and o.keyword = u.keyword";

		Vector<Object> args = new Vector<Object>();
		String dateClause = "";
		
		args.add(params.get("userIds"));

		if (params.get("fromDate") != null) {
			dateClause = " and o.last_updated >= ? and o.last_updated <= ?";
			args.add(params.get("fromDate"));
			args.add(params.get("toDate") + " 24:00:00");		
			sql += dateClause;
		}
		
		if (sortField != null && sortField.length() > 0)
			sql += " order by " + sortField;
		if (sortOrder != null && sortOrder.length() > 0)
			sql += " " + sortOrder;
		
		logger.debug("sql: " + sql);

		List<ReportData> reportDataList = new ArrayList<ReportData>();
		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql,ReportData.class)
					.addScalar("column1", new StringType())					
					.addScalar("column2", new StringType())	
					.addScalar("column3", new StringType())	
					.addScalar("column4", new StringType());										
					//.setResultTransformer(Transformers.aliasToBean(ReportData.class));						
			
			int pos = 0;
			for (Object arg : args) {
				q.setParameter(pos++, arg.toString());
			}
			
			reportDataList = q.list();
			
			String sql1 = "select found_rows() column1";
			q = session.createNativeQuery(sql1,ReportData.class)
					.addScalar("column1", new StringType());														
					//.setResultTransformer(Transformers.aliasToBean(ReportData.class));			

			List<ReportData> reportDataList1 = q.list();

			reportDataList.addAll(reportDataList1);
		} finally  {
			close();
		}
		return reportDataList;
	}
	
	//SAF campaign detail
	public List<ReportData> getSAFCampaignDetail(Map params, int offset, int numRecords, String sortField, String sortOrder) throws Exception {
		String sqlOld = "select c.name column5, c.start_date column6, c.message_text column7, "
					+ " msd.mobile_phone column2, msd.status column3, msd.last_updated column4,"
					+ " (select concat(first_name, ' ', last_name) from target_list_data tld, target_user_list tul"
					+ " 	where tld.mobile_phone = msd.mobile_phone"
					+ " 	and tul.list_id = tld.list_id"
					+ " 	and tul.user_id = ?) column1"
					+ " from campaign c, message_statistics_detail msd"
					+ " where msd.campaign_id = c.campaign_id"
					+ " and c.name = 'SAF'"
					+ " and c.user_id = ?"
					+ " and c.user_id = 0"
					+ " order by c.start_date desc";
		
		String sql = "SELECT "
				+ " c.name column5, c.start_date column6, c.message_text column7, "
				+ " msd.mobile_phone column2, msd.status column3, msd.last_updated column4, concat(first_name, ' ', last_name) column1"
				+ " from liberty_saf_optins saf,  user_customer_defined ucd,"
				+ " message_statistics_detail msd, campaign c"
				+ " where ucd.user_id = ?"
				+ " and saf.entity_id = ucd.custom_field_1"
				+ " and ucd.custom_field_4 = 'LIB_F'"
				+ " and msd.mobile_phone = concat('1', saf.mobile_phone)"
				+ " and msd.customer_id_1 = 'SAF'"
				+ " and msd.campaign_id = c.campaign_id"
				+ " and c.user_id = 0"				
				+ " order by c.start_date desc";
	
		if (sortField != null && sortField.length() > 0)
			sql += " order by " + sortField;
		if (sortOrder != null && sortOrder.length() > 0)
			sql += " " + sortOrder;
		
		logger.debug("sql: " + sql);

		String userId = params.get("userIds").toString();

		List<ReportData> reportDataList = new ArrayList<ReportData>();
		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql,ReportData.class)
					.addScalar("column1", new StringType())					
					.addScalar("column2", new StringType())	
					.addScalar("column3", new StringType())	
					.addScalar("column4", new StringType())				
					.addScalar("column5", new StringType())										
					.addScalar("column6", new StringType())										
					.addScalar("column7", new StringType());															
					//.setResultTransformer(Transformers.aliasToBean(ReportData.class));						
			
			reportDataList = q.setParameter(0, userId)
					//.setParameter(1, userId)
					.list();
			
			String sql1 = "select found_rows() column1";
			q = session.createNativeQuery(sql1,ReportData.class)
					.addScalar("column1", new StringType());														
					//.setResultTransformer(Transformers.aliasToBean(ReportData.class));			

			List<ReportData> reportDataList1 = q.list();

			reportDataList.addAll(reportDataList1);
		} finally  {
			close();
		}
		return reportDataList;
	}
	
	//all the data from the InfoForm
	public List<ReportData> getInfoFormData(Map params, int offset, int numRecords, String sortField, String sortOrder) throws Exception {
		String sql = "select i.mobile_phone column1, concat(i.first_name, ' ', i.last_name) column2,"
					+ " date_format(i.last_updated, '%m/%d/%Y') column3,"
					+ " i.entity_id column4, i.office_id column5"
					+ " from info_form i, user_customer_defined ucd"
					+ " where ucd.user_id = ?"
					+ " and (ucd.custom_field_1 = i.entity_id"
					+ "		or ucd.custom_field_2 = i.office_id)";
		
		Vector<Object> args = new Vector<Object>();
		String dateClause = "";
		
		args.add(params.get("userIds"));

		if (params.get("fromDate") != null) {
			dateClause = " and i.last_updated >= ? and i.last_updated <= ?";
			args.add(params.get("fromDate"));
			args.add(params.get("toDate") + " 24:00:00");			
			sql += dateClause;
		}
		
		logger.debug("sql: " + sql);
		
		List<ReportData> reportDataList = new ArrayList<ReportData>();
		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql,ReportData.class)
					.addScalar("column1", new StringType())					
					.addScalar("column2", new StringType())	
					.addScalar("column3", new StringType())	
					.addScalar("column4", new StringType())										
					.addScalar("column5", new StringType());										
					//.setResultTransformer(Transformers.aliasToBean(ReportData.class));						
			
			int pos = 0;
			for (Object arg : args) {
				q.setParameter(pos++, arg.toString());
			}
			
			reportDataList = q.list();
			
			String sql1 = "select found_rows() column1";
			q = session.createNativeQuery(sql1,ReportData.class)
					.addScalar("column1", new StringType());														
					//.setResultTransformer(Transformers.aliasToBean(ReportData.class));			

			List<ReportData> reportDataList1 = q.list();

			reportDataList.addAll(reportDataList1);			
		} finally  {
			close();
		}
		return reportDataList;
	}
}
