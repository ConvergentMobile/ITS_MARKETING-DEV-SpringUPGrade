package reports_graphs;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import mdp_common.DBStuff;

import org.apache.log4j.Logger;
import org.apache.struts.util.LabelValueBean;
import org.apache.struts.util.MessageResources;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.quartz.JobDataMap;

import user.Campaign;
import util.PropertyUtil;

import common.HibernateUtil;

import org.hibernate.internal.SessionImpl;
import java.sql.Connection;

public class ReportDAOManager implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(ReportDAOManager.class);
	
	protected DBStuff dbs = null;
	protected Session session = null;
	Transaction tx = null;
	//MessageResources messages = MessageResources.getMessageResources("MessageResources");
	
	public void close() {
		try {
			HibernateUtil.closeSession();
		} catch (Exception e) {
			logger.error("Exception closing session: " + e.toString());
		}
	}
	
	public List<ReportData> getKeywordSummary(Map params, DefaultCategoryDataset dataset) throws Exception {
		/*
		String sql = " SELECT count(*) cnt, m.status, c.name"
					+ " FROM user u, message_statistics_detail m"
					+ " left join campaign c on m.campaign_id = c.campaign_id"
					+ " WHERE m.customer_id_2 = u.keyword"
					+ " and u.user_id in (?)";
		*/
	
		String groupBy = " group by kw, cid";
		String orderBy = " order by kw, cid";
		
		String userIds = (String) params.get("userIds");
		
		String sql = "SELECT count(*) cnt, m.campaign_id cid,"
				//+ " if (status = 'Incoming', 'Incoming', 'Outgoing') status, c.name"
				+ " c.name, m.keyword kw"
				+ " FROM user u, message_statistics_detail m"
				+ " left join campaign c on m.campaign_id = c.campaign_id"
				+ " WHERE m.keyword = u.keyword"
				+ " and m.campaign_id != '0'" //exclude inbounds
				+ " and u.user_id in (" + userIds + ")"
				+ " and u.user_id = c.user_id"
				+ groupBy
				
				+ " UNION"
				
				+ " SELECT count(*) cnt, m.campaign_id cid,"
				+ " c.name, m.keyword kw"
				+ " FROM campaign_multi_list cml, message_statistics_detail m"
				+ " left join campaign c on m.campaign_id = c.campaign_id"
				+ " WHERE  m.customer_id_1 in (" + userIds + ")"
				+ " and cml.campaign_id = m.campaign_id"
				+ " and cml.campaign_id = c.campaign_id"
				+ groupBy;
		
		Vector<Object> args = new Vector<Object>();
		//args.add(params.get("userIds"));
		//args.add(params.get("userIds"));

		if (params.get("fromDate") != null) {
			sql += " and m.last_updated between ? and ?";
			args.add(params.get("fromDate"));
			args.add(params.get("toDate"));		
		}
		
		sql += orderBy;
		
		logger.debug("sql: " + sql);
		logger.debug("uids: " + params.get("userIds"));
					
		List<ReportData> reportDataList = new ArrayList<ReportData>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, args.toArray());

			while (rs.next()) {			
				reportDataList.add(new ReportData(rs.getString("name"), rs.getString("cnt"), rs.getString("kw")));
				String cname = rs.getString("name") == null ? "" : rs.getString(1);
				String kw = rs.getString(3) == null ? "" : rs.getString(3);
				dataset.addValue(rs.getDouble(1), "series1", kw);
			}

			return reportDataList;
		} finally {
			dbs.close();
			close();
		}
	}
	
	public List<ReportData> getKeywordSummary1(Map params, DefaultPieDataset dataset) throws Exception {
		/*
		String sql = " SELECT count(*) cnt, m.status, c.name"
					+ " FROM user u, message_statistics_detail m"
					+ " left join campaign c on m.campaign_id = c.campaign_id"
					+ " WHERE m.customer_id_2 = u.keyword"
					+ " and u.user_id in (?)";
		*/
		
		String sql = "SELECT count(*) cnt, m.campaign_id,"
					//+ " if (status = 'Incoming', 'Incoming', 'Outgoing') status, c.name"
					+ " c.name, m.keyword"
					+ " FROM user u, message_statistics_detail m"
					+ " left join campaign c on m.campaign_id = c.campaign_id"
					+ " WHERE m.keyword = u.keyword"
					+ " and m.campaign_id != '0'" //exclude inbounds
					+ " and u.user_id in (?)";
		
		String groupBy = " group by m.keyword, m.campaign_id";
		String orderBy = " order by m.keyword, m.campaign_id";
		
		Vector<Object> args = new Vector<Object>();
		args.add(params.get("userIds"));

		if (params.get("fromDate") != null) {
			sql += " and m.last_updated between ? and ?";
			args.add(params.get("fromDate"));
			args.add(params.get("toDate"));		
		}
		
		sql += groupBy;
		sql += orderBy;
		
		logger.debug("sql: " + sql);
		
		List<ReportData> reportDataList = new ArrayList<ReportData>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, args.toArray());

			while (rs.next()) {			
				reportDataList.add(new ReportData(rs.getString("name"), rs.getString("cnt"), rs.getString("keyword")));
				String cname = rs.getString("name");
				if (cname == null)
					cname = "";
				dataset.setValue(cname, rs.getInt("cnt"));
			}

			return reportDataList;
		} finally {
			dbs.close();
			close();
		}
	}
	
	public List<ReportData> getCountByPhoneNumber(Map params, DefaultCategoryDataset dataset) throws Exception {
		String userIds = (String) params.get("userIds");
		
		String sql = "SELECT mobile_phone, count(*) cnt, m.keyword kw FROM message_statistics_detail m, user u"
					+ " where u.user_id in (" + userIds + ")"
					+ " and u.keyword = m.keyword"
					+ " UNION "
					+ " SELECT mobile_phone, count(*) cnt, m.keyword kw FROM message_statistics_detail m, campaign_multi_list cml"
					+ " where m.customer_id_1 in (" + userIds + ")"
					+ " and cml.campaign_id = m.campaign_id";					
		
		String groupBy = " group by kw, mobile_phone";
		String orderBy = " order by cnt";
		
		Vector<Object> args = new Vector<Object>();
		
		if (params.get("fromDate") != null) {
			sql += " and m.last_updated between ? and ?";
			args.add(params.get("fromDate"));
			args.add(params.get("toDate"));		
		}
					
		sql += groupBy;
		sql += orderBy;
		
		logger.debug("sql: " + sql);

		List<ReportData> reportDataList = new ArrayList<ReportData>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, args.toArray());
			while (rs.next()) {
				reportDataList.add(new ReportData(rs.getString("mobile_phone"), rs.getString("cnt"), rs.getString("keyword")));				
				dataset.addValue(rs.getDouble(2), "series1", rs.getString(1));
			}
		} finally  {
			dbs.close();
			close();
		}
		return reportDataList;
	}	

	public List<ReportData> getCountByAreaCode(Map params, DefaultCategoryDataset dataset) throws Exception {
		/*
		String sql = "SELECT substring(mobile_phone,2,3) area_code,"
					+ " if (status = 'Incoming', 'Incoming', 'Outgoing') dir, count(*) cnt"
					+ " FROM message_statistics_detail m, user u"
					+ " where u.user_id in (?)"
					+ " and u.keyword = m.keyword";
		
		String groupBy = " group by area_code, dir";
		String orderBy = " order by area_code";
		*/
		
		String userIds = (String) params.get("userIds");

		Vector<Object> args = new Vector<Object>();
		args.add(userIds);
		args.add(userIds);
		
		String dateClause = "";
		if (params.get("fromDate") != null) {
			dateClause = " and m.last_updated between ? and ?";
			args.add(params.get("fromDate"));
			args.add(params.get("toDate"));
		}
				
		String dropSql = "drop table if exists temp1";
		String tempSql = "create temporary table temp1"
						+ " select distinct substring(mobile_phone,2,3) area_code"
						+ " from message_statistics_detail m1, user u where (u.keyword = m1.keyword or m1.customer_id_1 = ?)"
						+ " and u.user_id = " + userIds;
		
		String sql = " select area_code,"
			+ " (select count(*)"
			+ " from message_statistics_detail m, user u"
			//+ " where u.user_id in (" + userIds + ")"
			+ " where u.user_id = " + userIds
			+ " and (u.keyword = m.keyword or m.customer_id_1 = ?)"
			+ " and substring(mobile_phone,2,3) = area_code"
			+ " and status = 'Incoming'"
			+ dateClause
			+ " group by area_code) Incoming,"		

			+ " (select count(*) cnt"
			+ " from message_statistics_detail m, user u"
			+ " where u.user_id = " + userIds
			+ " and (u.keyword = m.keyword or m.customer_id_1 = ?)"
			+ " and substring(mobile_phone,2,3) = area_code"			
			+ " and status != 'Incoming'"
			+ dateClause
			+ " group by area_code) Outgoing"
			
			+ " from temp1";

		//inSql = "ifnull(" + inSql + "), 0) Incoming";
		//outSql = ", ifnull(" + outSql + "), 0) Outgoing";
		
		//add these args for the second sql piece if date range is specified
		if (dateClause != null && dateClause.length() > 0) {
			args.add(params.get("fromDate"));
			args.add(params.get("toDate"));
		}
		
		logger.debug("sql: " + sql);

		List<ReportData> reportDataList = new ArrayList<ReportData>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			dbs.update(dropSql, new Object[] {});
			dbs.update(tempSql, new Object[] {userIds});
			ResultSet rs = dbs.getFromDB(sql, args.toArray());
			while (rs.next()) {
				reportDataList.add(new ReportData(rs.getString("area_code"), rs.getString("Incoming"), rs.getString("Outgoing")));				
				dataset.addValue(rs.getDouble("Incoming"), "Incoming", rs.getString("area_code"));
				dataset.addValue(rs.getDouble("Outgoing"), "Outgoing", rs.getString("area_code"));
			}
		} finally  {
			dbs.close();
			close();
		}
		return reportDataList;
	}
	
	public List<ReportData> getCountByDayOfWeek(Map params, DefaultCategoryDataset dataset) throws Exception {
		/*
		String sql = "SELECT case dayofweek(last_updated)"
					+ " when 1 then 'Sun'"
					+ " when 2 then 'Mon'"
					+ " when 3 then 'Tue'"
					+ " when 4 then 'Wed'"
					+ " when 5 then 'Thu'"
					+ " when 6 then 'Fri'"
					+ " when 7 then 'Sat'"
					+ " end as day, count(*) cnt"
					+ " from message_statistics_detail"
					//+ " where shortcode = ?"
					+ " where customer_id_1 != 'Incoming'";

		String sql = "SELECT m.keyword, d.name day, if(status = 'Incoming', 'Incoming', 'Outgoing') direction,"
					+ " count(m.message_id) cnt"
					+ " from  (dayofweek d left join message_statistics_detail m"
					+ " on   dayofweek(m.last_updated) = d.day), user u"
					+ " where u.user_id in (?)"
					+ " and u.keyword = m.keyword"
					+ " and customer_id_1 != 'Incoming'";
		*/
		
		String userIds = (String) params.get("userIds");
		String groupBy = " group by m.keyword, d.day, direction"; 	
		
		String dateClause = "";

		Vector<Object> args = new Vector<Object>();
		
		if (params.get("fromDate") != null) {
			dateClause = " and m.last_updated between ? and ?";
			args.add(params.get("fromDate"));
			args.add(params.get("toDate"));
			args.add(params.get("fromDate"));
			args.add(params.get("toDate"));	
			args.add(params.get("fromDate"));
			args.add(params.get("toDate"));	
			args.add(params.get("fromDate"));
			args.add(params.get("toDate"));				
		}
		
		String inSql = " (select count(message_id) from"
					+ " (select m.message_id, m.last_updated"
					+ " from message_statistics_detail m, user u"
					+ " where u.user_id = " + userIds
					+ dateClause				
					+ " and u.keyword = m.keyword and status = 'Incoming'"
                
					+ " UNION"
					
					+ " select m.message_id, m.last_updated"
					+ " from message_statistics_detail m, campaign_multi_list cml"
					+ " where m.customer_id_1 = " + userIds
					+ " and status = 'Incoming'"
					+ dateClause				
					+ " and m.campaign_id = cml.campaign_id) x"
					+ " where d.day = dayofweek(last_updated) group by d.day) Incoming,";
		
		String outSql = " (select count(message_id) from"
					+ " (select m.message_id, m.last_updated"
		            + " from message_statistics_detail m, user u"
		            + " where u.user_id = " + userIds
					+ dateClause	            
		            + " and u.keyword = m.keyword and status != 'Incoming'"
		                
		            + " UNION"
		            + " select m.message_id, m.last_updated"
		            + " from message_statistics_detail m, campaign_multi_list cml"
		            + " where m.customer_id_1 = " + userIds
		            + " and status != 'Incoming'"
					+ dateClause	            
		            + " and m.campaign_id = cml.campaign_id) x"
		            + " where d.day = dayofweek(last_updated)"
		            + " group by d.day) Outgoing";
	
		
		//inSql = "ifnull(" + inSql + "), 0) Incoming";
		//outSql = ", ifnull(" + outSql + "), 0) Outgoing";
				
		String sql = "select d.name as day, " + inSql + outSql + " from dayofweek d";
		
		logger.debug("sql: " + sql);

		List<ReportData> reportDataList = new ArrayList<ReportData>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, args.toArray());
			while (rs.next()) {
				reportDataList.add(new ReportData(rs.getString("day"), rs.getString("Incoming"), rs.getString("Outgoing")));				
				dataset.addValue(rs.getDouble("Outgoing"), "Outgoing", rs.getString("day"));
				dataset.addValue(rs.getDouble("Incoming"), "Incoming", rs.getString("day"));
			}
		} finally  {
			dbs.close();
			close();
		}
		return reportDataList;
	}

	public List<ReportData> getCountByHour(Map params, DefaultCategoryDataset dataset) throws Exception {
		/*
		String sql = "SELECT hour(m.last_updated) hour, count(m.message_id) cnt"
					+ " from  message_statistics_detail m, user u"
					+ " where u.user_id in (?)"
					+ " and u.keyword = m.keyword";

		String groupBy = " group by hour"; 
		String orderBy = " order by hour";
		 */
		
		String groupBy = " group by h.hour"; 
		
		String userIds = (String) params.get("userIds");
		String inSql = " (select count(m.message_id) cnt"
			+ " from message_statistics_detail m, user u"
			+ " where u.user_id = " + userIds
			+ " and (u.keyword = m.keyword or m.customer_id_1 = ?)"
			+ " and h.hour_24 = hour(m.last_updated)"
			+ " and status = 'Incoming'";
			
			/*
			+ " UNION "
			
			+ " select count(m.message_id) cnt"
			+ " from message_statistics_detail m, user u, campaign_multi_list cml"
			+ " where m.customer_id_1 = " + userIds + " and u.keyword = m.keyword"
			+ " and h.hour_24 = hour(m.last_updated)"
			+ " and status = 'Incoming' " 
			+ " and m.campaign_id = cml.campaign_id group by h.hour";	
		    */				
					
		String outSql = " (select count(m.message_id) cnt"
			+ " from message_statistics_detail m, user u"
			+ " where u.user_id = " + userIds 
			+ " and (u.keyword = m.keyword or m.customer_id_1 = ?)"
			+ " and h.hour_24 = hour(m.last_updated)"
			+ " and status != 'Incoming'";
			
			/*
			+ " UNION "
			
			+ " select count(m.message_id) cnt"
			+ " from message_statistics_detail m, user u, campaign_multi_list cml"
			+ " where m.customer_id_1 = " + userIds + " and u.keyword = m.keyword"
			+ " and h.hour_24 = hour(m.last_updated)"
			+ " and status != 'Incoming'"
			+ " and m.campaign_id = cml.campaign_id group by h.hour";		
			*/								
		
		Vector<Object> args = new Vector<Object>();
		args.add(userIds);
		args.add(userIds);
		
		if (params.get("fromDate") != null) {
			inSql += " and m.last_updated between ? and ?";
			args.add(params.get("fromDate"));
			args.add(params.get("toDate"));
			outSql += " and m.last_updated between ? and ?";
			args.add(params.get("fromDate"));
			args.add(params.get("toDate"));			
		}
		
		inSql = "ifnull(" + inSql + groupBy + "), 0) Incoming";
		outSql = ", ifnull(" + outSql + groupBy + "), 0) Outgoing";
				
		String sql = "select h.hour as hour, " + inSql + outSql + " from hours h where h.hour_24 < 24";
		
		logger.debug("sql: " + sql);

		List<ReportData> reportDataList = new ArrayList<ReportData>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, args.toArray());
			while (rs.next()) {
				reportDataList.add(new ReportData(rs.getString("hour"), rs.getString("Incoming"), rs.getString("Outgoing")));				
				dataset.addValue(rs.getDouble("Incoming"), "Incoming", rs.getString("hour"));
				dataset.addValue(rs.getDouble("Outgoing"), "Outgoing", rs.getString("hour"));				
			}
		} finally  {
			dbs.close();
			close();
		}
		return reportDataList;
	}
	
	public List<ReportData> getByCampaign(Map params, DefaultCategoryDataset dataset) throws Exception {
		String userIds = (String) params.get("userIds");
		
		String sql = "SELECT c.campaign_id cid, c.name, c.start_date start_date, c.keyword kw,"
					+ " c.message_text, count(m.mobile_phone) cnt"
					+ " FROM campaign c, message_statistics_detail m"
					+ " where c.campaign_id = m.campaign_id"
					+ " and c.user_id in (" + userIds + ")"
					+ " and c.list_id != 'Multi'"
					+ " group by kw, cid"
					
					+ " UNION "
					
					+ " SELECT c.campaign_id cid, c.name, c.start_date start_date, c.keyword kw,"
					+ " c.message_text, count(m.mobile_phone) cnt"
					+ " FROM campaign c, message_statistics_detail m, campaign_multi_list cml"
					+ " where c.campaign_id = m.campaign_id"
					+ " and c.list_id = 'Multi'"
					+ " and m.customer_id_1 in (" + userIds + ")"
					+ " and cml.campaign_id = c.campaign_id"
					+ " group by kw, cid";					
		
		Vector<Object> args = new Vector<Object>();
		
		if (params.get("fromDate") != null) {
			sql += " and m.last_updated between ? and ?";
			args.add(params.get("fromDate"));
			args.add(params.get("toDate"));
		}
	
		//String groupBy = " group by kw, cid"; 
		
		//String orderBy = " order by kw, cid";
		String orderBy = " order by start_date desc";
		
		//sql += groupBy;
		sql += orderBy;
		
		logger.debug("sql: " + sql);

		List<ReportData> reportDataList = new ArrayList<ReportData>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, args.toArray());
			while (rs.next()) {
				reportDataList.add(new ReportData(rs.getString("cid"), rs.getString("name"),
										rs.getString("start_date"), rs.getString("kw"),
										rs.getString("message_text"), rs.getString("cnt")));				
			}
		} finally  {
			dbs.close();
			close();
		}
		return reportDataList;
	}	
	
	public List<ReportData> getByOffer(Map params, DefaultCategoryDataset dataset) throws Exception {
		String sql = "SELECT o.name, o.expiration_date, o.code, o.description"
					+ " FROM offers o, profile p"
					+ " where o.profile_id = p.profile_id"
					+ " and p.user_id in (?)";
		

		Vector<Object> args = new Vector<Object>();
		args.add(params.get("userIds"));
		
		String orderBy = " order by o.expiration_date"; 
		
		sql += orderBy;
		
		logger.debug("sql: " + sql);

		List<ReportData> reportDataList = new ArrayList<ReportData>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, args.toArray());
			while (rs.next()) {
				reportDataList.add(new ReportData(rs.getString("name"), rs.getString("expiration_date"),
										rs.getString("code"), rs.getString("description"), null, null));
			}
		} finally  {
			dbs.close();
			close();
		}
		return reportDataList;
	}	
	
	public List<ReportData> getScheduledTriggers(Map params, DefaultCategoryDataset dataset) throws Exception {
		String sql = "SELECT trigger_name, next_fire_time, job_data, trigger_group"
					+ " FROM " + PropertyUtil.load().getProperty("quartz_db") + ".QRTZ_TRIGGERS qt"
					+ " where qt.trigger_group in (?)";
		
		Vector<Object> args = new Vector<Object>();
		args.add(params.get("userIds"));
		
		String orderBy = " order by next_fire_time"; 
		
		sql += orderBy;
		
		logger.debug("sql: " + sql);

		List<ReportData> reportDataList = new ArrayList<ReportData>();
		Calendar cal = Calendar.getInstance();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, args.toArray());
			while (rs.next()) {
			    InputStream is = rs.getBlob("job_data").getBinaryStream();
			    ObjectInputStream ois = new ObjectInputStream(is);
			    Object obj = ois.readObject(); 
				JobDataMap jdmap = (JobDataMap) obj;
				Campaign camp = (Campaign) jdmap.get("campaign");
				cal.setTimeInMillis(Long.valueOf(rs.getString("next_fire_time")));
				reportDataList.add(new ReportData(rs.getString("trigger_name"), cal.getTime().toString(),
										camp.getName(), camp.getMessageText(), rs.getString("trigger_group"), null));
			}
		} finally  {
			dbs.close();
			close();
		}
		return reportDataList;
	}	
	
	//used in us411_admin
	public List<ReportData> getMsgCount(Map params, DefaultCategoryDataset dataset) throws Exception {
		String sql = "SELECT u.keyword,"
					+ " case MONTH(m.last_updated)"
					+ " when 1 then 'Jan'"
					+ " when 2 then 'Feb'"
					+ " when 3 then 'Mar'"
					+ " when 4 then 'Apr'"
					+ " when 5 then 'May'"
					+ " when 6 then 'Jun'"
					+ " when 7 then 'Jul'"
					+ " when 8 then 'Aug'"
					+ " when 9 then 'Sep'"
					+ " when 10 then 'Oct'"
					+ " when 11 then 'Nov'"
					+ " when 12 then 'Dec'"
					+ " end as rmonth,"
					+ " count(*) cnt FROM message_statistics_detail m, user u"
					+ " where u.user_id in (?)"
					+ " and u.keyword = m.keyword";
		
		String groupBy = " group by u.keyword, MONTH(m.last_updated)";
		String orderBy = " order by MONTH(m.last_updated)";
		
		Vector<Object> args = new Vector<Object>();
		args.add(params.get("userIds"));
		
		if (params.get("fromDate") != null) {
			sql += " and m.last_updated between ? and ?";
			args.add(params.get("fromDate"));
			args.add(params.get("toDate"));		
		}
					
		sql += groupBy;
		sql += orderBy;
		
		logger.debug("sql: " + sql);

		List<ReportData> reportDataList = new ArrayList<ReportData>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, args.toArray());
			while (rs.next()) {
				reportDataList.add(new ReportData(rs.getString("keyword"), rs.getString("rmonth"), rs.getString("cnt")));				
				dataset.addValue(rs.getDouble(3), "series1", rs.getString(2));
			}
		} finally  {
			dbs.close();
			close();
		}
		return reportDataList;
	}	
	
	//get opt-ins with timestamp
	public List<ReportData> getOptIns(Map params, DefaultCategoryDataset dataset) throws Exception {
		String sql = "SELECT tld.mobile_phone, tld.last_updated from target_list_data tld, target_user_list tul, user u"
					+ " where tul.list_id = tld.list_id"
					+ " and u.user_id = tul.user_id"
					+ " and u.keyword = tul.list_name"
					+ " and tul.user_id = ?";
		
		String orderBy = " order by tld.last_updated";
		
		Vector<Object> args = new Vector<Object>();
		args.add(params.get("userIds"));
		
		if (params.get("fromDate") != null) {
			sql += " and tld.last_updated between ? and ?";
			args.add(params.get("fromDate"));
			args.add(params.get("toDate"));		
		}
					
		sql += orderBy;
		
		logger.debug("sql: " + sql);

		List<ReportData> reportDataList = new ArrayList<ReportData>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, args.toArray());
			while (rs.next()) {
				reportDataList.add(new ReportData(rs.getString("mobile_phone"), rs.getString("last_updated"), null));				
			}
		} finally  {
			dbs.close();
			close();
		}
		return reportDataList;
	}	
	
	//All MOs and MTs for a given keyword - For CN - 10/24/13
	public List<ReportData> getKWActivity(Map params, DefaultCategoryDataset dataset) throws Exception {
		String sql = "SELECT msd.keyword, mobile_phone, status, last_updated,"
					+ " (select error_msg_text from message_statistics_detail m where m.message_id = msd.message_id"
					+ " 		and m.status = 'Error') error_msg"
					+ " FROM message_statistics_detail msd, user u"
					+ " where u.user_id = ?"
					+ " and u.keyword = msd.keyword"
					+ " order by msd.last_updated";
		

		Vector<Object> args = new Vector<Object>();
		
		args.add(params.get("userIds"));
		
		if (params.get("fromDate") != null) {
			sql += " and msd.last_updated between ? and ?";
			args.add(params.get("fromDate"));
			args.add(params.get("toDate"));		
		}						
		logger.debug("sql: " + sql);

		List<ReportData> reportDataList = new ArrayList<ReportData>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, args.toArray());
			while (rs.next()) {
				reportDataList.add(new ReportData(rs.getString("mobile_phone"), rs.getString("status"),
										rs.getString("last_updated"), rs.getString("error_msg"), rs.getString("keyword"), null));
			}
		} finally  {
			dbs.close();
			close();
		}
		return reportDataList;
	}	
}
