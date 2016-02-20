package dao;

import java.io.Serializable;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import keyword.KeywordApplication;
import liberty.CustomFields;
import liberty.Territory;
import mdp_common.DBStuff;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

import user.TargetUserList;
import user.User;
import admin_user.UserProfileVO;

import common.HibernateUtil;

import data.ApprovedMessage;
import data.SendMessageList;
import data.ValueObject;
import util.PropertyUtil;

public class LibertyAdminDAOManager implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(LibertyAdminDAOManager.class);
	private Integer siteId = Integer.parseInt(PropertyUtil.load().getProperty("siteId"));

	DBStuff dbs = null;
	Session session = null;
	Transaction tx = null;

	public void close() {
		try {
			HibernateUtil.closeSession();
			if (dbs != null)
				dbs.close();
		} catch (Exception e) {
			logger.error("Exception closing session: " + e.toString());
		}
	}
	
	//get all the keywords that have not been claimed
	public List<CustomFields> getReservedKeywords(Long userId) throws Exception {
		String sql = "select u.user_id userId, ucd.custom_field_1 entityId, ucd.custom_field_2 officeId"
				+ " from user u, user_customer_defined ucd, keyword_application kw, liberty_closure lc"
				+ " where u.user_id = ucd.user_id"
				+ " and kw.keyword = u.keyword"
				+ " and kw.status = 'R'"
				+ " and ucd.custom_field_4 = 'LIB_S'"
				+ " and ucd.custom_field_5 = 1"
				+ " and ucd.custom_field_8 = 4"				
				+ " and u.user_id = lc.child_id and lc.parent_id = ?";
		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql)
						.addScalar("userId", new LongType())
						.addScalar("entityId", new StringType())
						.addScalar("officeId", new StringType())
						.setResultTransformer(Transformers.aliasToBean(CustomFields.class));

			List<CustomFields> sites = q.setLong(0, userId)
							.list();
			
			if (sites.isEmpty())
				return null;
			
			logger.debug("getReservedKeywords: size = " + sites.size());
			return sites;
		} finally {
			close();
		}		
	}
	
	//get the entity info for allocation of Entity keyword
	public List<CustomFields> getEntityKeyword(Long userId) throws Exception {
		String sql = "select u.user_id, ucd.custom_field_1, ucd.custom_field_2 office_id"
				+ " from user u, user_customer_defined ucd, keyword_application kw"
				+ " where u.user_id = ucd.user_id"
				+ " and kw.keyword = u.keyword"
				+ " and kw.status = 'R'"
				+ " and ucd.custom_field_4 in ('LIB_F', 'LIB_AD')"
				+ " and u.user_id = ?";
		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql).addEntity("cf", CustomFields.class);
			List<CustomFields> sites = q.setLong(0, userId)
							.list();			
			
			if (sites.isEmpty())
				return null;
			
			logger.debug("getReservedKeywords: size = " + sites.size());
			return sites;
		} finally {
			close();
		}		
	}
	
	//get all the Send a Friend offices for an entity
	public List<ValueObject> getSAFOffices(Long userId) throws Exception {
		String sql = "select s.user_id userId field1, s.keyword field2, s.entity_id field3, s.office_id field4"
					+ " from send_a_friend_offices s, user_customer_defined ucd"
					+ " where ucd.user_id = ?"
					+ " and s.entity_id = ucd.custom_field_1";
		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql)
					.addScalar("field1", new LongType())
					.addScalar("field2", new StringType())
					.addScalar("field3", new StringType())					
					.addScalar("field4", new StringType())							
					.setResultTransformer(Transformers.aliasToBean(ValueObject.class));
            
			List<ValueObject> sites = q.setLong(0, userId)	
										.list();			
			
			if (sites.isEmpty())
				return null;
			
			return sites;
		} finally {
			close();
		}
	}
	
	//get all the Send a Friend offices for an entity
	public List<UserProfileVO> getSAFSites(Long userId) throws Exception {
		String sql = "select u.site_id as siteId, u.keyword as keyword, ucd.custom_field_2 as customField2,"
				+ " u.user_id as userId, ucd.custom_field_1 as customField1,"
				+ " saf.saf_message as customField3, saf.include_phone as customField4"
				+ " from user u, keyword_application kw, liberty_closure lc,"
				+ " user_customer_defined ucd"
                + " left join send_a_friend_offices saf"
                + " on ucd.custom_field_1 = saf.entity_id"
                + " and ucd.custom_field_2 = saf.office_id"				
				+ " where u.user_id = ucd.user_id"
				+ " and kw.keyword = u.keyword"
				+ " and kw.status = 'P'"
				+ " and (ucd.custom_field_4 = 'LIB_S'"
				+ " and ucd.custom_field_5 = 1"
				+ " and ucd.custom_field_8 = 4)"	
				+ " and u.user_id = lc.child_id and lc.parent_id = ?"
				+ " and u.site_id = kw.site_id"
				+ " and u.site_id = ?";
		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql)
					.addScalar("userId", new LongType())
					.addScalar("keyword", new StringType())
					.addScalar("customField1", new StringType())					
					.addScalar("customField2", new StringType())	
					.addScalar("customField3", new StringType())
					.addScalar("customField4", new StringType())
					.setResultTransformer(Transformers.aliasToBean(UserProfileVO.class));
            
			List<UserProfileVO> sites = q.setLong(0, userId)
										.setInteger(1, siteId)
										.list();			
			
			if (sites.isEmpty())
				return null;
			
			return sites;
		} finally {
			close();
		}
	}

	//get all the sites for this user based on the access level
	//get only the storefront keywords - type = LIB_S
	public List<UserProfileVO> getAllSites(Long userId) throws Exception {
		return this.getAllSites(userId, null, null);
	}
	
	//get all the sites for this user based on the access level
	//get only the storefront keywords - type = LIB_S
	public List<UserProfileVO> getAllSites(Long userId, String sortColumn, String sortOrder) throws Exception {
		String orderBy = " order by ucd.custom_field_4 desc, customField3";

		String sql = "select u.site_id siteId, u.keyword keyword, ucd.custom_field_1 customField1, ucd.custom_field_2 customField2,"
				+ " kw.status customField3, u.user_id userId, ucd.custom_field_4 customField4"
				+ " from user u, user_customer_defined ucd,"
				+ " keyword_application kw, liberty_closure lc"
				+ " where u.user_id = ucd.user_id"
				+ " and kw.keyword = u.keyword"
				+ " and kw.site_id = u.site_id"
				+ " and ((ucd.custom_field_4 = 'LIB_S'"
				+ " and ucd.custom_field_5 = 1"
				+ " and ucd.custom_field_8 = 4)"	
				+ " or ucd.custom_field_4 = 'LIB_F')" //get the entity keyword as well
				+ " and u.user_id = lc.child_id and lc.parent_id = ?"
				+ " and u.site_id = ?";
		
		if (sortColumn != null) {
			orderBy = " order by " + sortColumn + " " + sortOrder;			
		}
		
		sql += orderBy;
		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql)
					.addScalar("userId", new LongType())
					.addScalar("siteId", new IntegerType())
					.addScalar("keyword", new StringType())
					.addScalar("customField1", new StringType())					
					.addScalar("customField2", new StringType())
					.addScalar("customField3", new StringType())
					.addScalar("customField4", new StringType()) //LIB_F or LIB_S 								
					.setResultTransformer(Transformers.aliasToBean(UserProfileVO.class));
            
			List<UserProfileVO> sites = q.setLong(0, userId)	
										.setInteger(1, siteId)
										.list();			
			
			if (sites.isEmpty())
				return null;
			
			logger.debug("getAllSites: size = " + sites.size());
			return sites;
		} finally {
			close();
		}
	}
	
	//get all the sites for this user based on the access level
	public List<UserProfileVO> getSites(Long userId) throws Exception {
		return this.getSites(userId, "", null, null);
	}
	
	//get all the sites for this user based on the access level
	public List<UserProfileVO> getSites(Long userId, String sortColumn, String sortOrder) throws Exception {
		return this.getSites(userId, "", sortColumn, sortOrder);
	}
	
	//get all the sites for this user based on the access level given a starting keyword letter
	//get only the storefront keywords - type = LIB_S
	public List<UserProfileVO> getSites(Long userId, String letter, String sortColumn, String sortOrder) throws Exception {
		/*
		String sql = "select u.keyword, u.user_id uid, p.* from user u"
				+ " left join profile p on (u.user_id = p.user_id)"
				+ " and u.keyword like ?"
				+ " where u.user_id in (select uc.user_id from user_closure where user_parent_id = ?)";
		*/
		String orderBy = " order by customField2";
		String sql = "select u.site_id as siteId, u.keyword as keyword, ifnull(ucd.custom_field_2, 'Entity') as customField2,"
				+ " u.user_id as userId, ucd.custom_field_1 as customField1"
				+ " from user u, user_customer_defined ucd,"
				+ " keyword_application kw, liberty_closure lc"
				+ " where u.keyword like ?"
				+ " and u.user_id = ucd.user_id"
				+ " and kw.keyword = u.keyword"
				+ " and kw.status = 'P'"
				+ " and ((ucd.custom_field_4 = 'LIB_S'"
				+ " and ucd.custom_field_5 = 1"
				+ " and ucd.custom_field_8 = 4)"	
				+ " or ucd.custom_field_4 = 'LIB_F')" //get the entity keyword as well
				+ " and u.user_id = lc.child_id and lc.parent_id = ?"
				+ " and u.site_id = kw.site_id"
				+ " and u.site_id = ?";

		if (sortColumn != null) {
			orderBy = " order by " + sortColumn + " " + sortOrder;
		}
		
		sql += orderBy;
		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql)
					.addScalar("userId", new LongType())
					.addScalar("siteId", new IntegerType())
					.addScalar("keyword", new StringType())
					.addScalar("customField2", new StringType())
					.addScalar("customField1", new StringType())					
					.setResultTransformer(Transformers.aliasToBean(UserProfileVO.class));
			
			/*
            q.setResultTransformer(new ResultTransformer() {
				private static final long serialVersionUID = 1L;

				public Object transformTuple(Object[] values, String[] colnames) {
					UserProfileVO vo = new UserProfileVO();
					vo.setUserId(Long.valueOf((String)values[0]));
					vo.setSiteId(Integer.valueOf((String)values[1]));
					vo.setKeyword(String.valueOf(values[2]));
					vo.setCustomField2(String.valueOf(values[3]));

                    return vo;
                }
               
                public List transformList(List arg0) {
                      return arg0;
                }
          });
          */
            
			List<UserProfileVO> sites = q.setString(0, letter+"%")
						.setLong(1, userId)	
						.setInteger(2, siteId)
						.list();			
			
			if (sites.isEmpty())
				return null;
			
			logger.debug("getSites: size = " + sites.size());
			return sites;
		} finally {
			close();
		}
	}
	
	//get the sites for an AD user
	public List<UserProfileVO> getSitesAD(Long userId) throws Exception {
		String sql = "select u.keyword, u.user_id userId, ifnull(ucd.custom_field_2, 'AD') customField2,"
				+ " ucd.custom_field_1 customField1, kw.status customField3"					
				+ " from user u, user_customer_defined ucd,"
				+ " keyword_application kw, liberty_closure lc"
				+ " where u.user_id = ucd.user_id"
				+ " and kw.keyword = u.keyword"
				+ " and kw.status = 'P'"
				+ " and ucd.custom_field_4 = 'LIB_AD'"
				+ " and ucd.custom_field_8 = 4"	
				+ " and u.user_id = lc.child_id and lc.parent_id = ?"
				+ " and u.site_id = kw.site_id"
				+ " and u.site_id = ?"				
				+ " order by customField2 desc";
		
		List<UserProfileVO> sites = new ArrayList<UserProfileVO>();		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql)
					.addScalar("userId", new LongType())
					.addScalar("keyword", new StringType())
					.addScalar("customField1", new StringType())					
					.addScalar("customField2", new StringType())
					.addScalar("customField3", new StringType())					
					.setResultTransformer(Transformers.aliasToBean(UserProfileVO.class));
			
			sites = q.setLong(0, userId)
						.setInteger(1, siteId)
						.list();			
			
			if (sites.isEmpty())
				return null;
			
			logger.debug("getSites: size = " + sites.size());
			return sites;
		} finally {
			close();
		}
	}
	
	//get all the sites for an AD user
	public List<UserProfileVO> getAllSitesAD(Long userId) throws Exception {
		String sql = "select u.keyword, u.user_id userId, ifnull(ucd.custom_field_2, 'AD') customField2,"
				+ "  ucd.custom_field_1 customField1, kw.status customField3"
				+ " from user u, user_customer_defined ucd,"
				+ " keyword_application kw, liberty_closure lc"
				+ " where u.user_id = ucd.user_id"
				+ " and kw.keyword = u.keyword"
				+ " and ucd.custom_field_4 = 'LIB_AD'"
				+ " and ucd.custom_field_8 = 4"	
				+ " and u.user_id = lc.child_id and lc.parent_id = ?"
				+ " and u.site_id = kw.site_id"
				+ " and u.site_id = ?"
				+ " order by customField2 desc";
		
		List<UserProfileVO> sites = new ArrayList<UserProfileVO>();		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql)
					.addScalar("userId", new LongType())
					.addScalar("keyword", new StringType())
					.addScalar("customField1", new StringType())					
					.addScalar("customField2", new StringType())
					.addScalar("customField3", new StringType())					
					.setResultTransformer(Transformers.aliasToBean(UserProfileVO.class));	
			
			sites = q.setLong(0, userId)
						.setInteger(1, siteId)
						.list();			
			
			if (sites.isEmpty())
				return null;
			
			logger.debug("getSites: size = " + sites.size());
			return sites;
		} finally {
			close();
		}
	}
	
	//get all the sites with status = P for a Corporate user
	//get only the storefront keywords - type = LIB_S
	public List<UserProfileVO> getSites() throws Exception {
		return this.getSites(null, null);
	}
	
	//get all the sites with status = P for a Corporate user
	//get only the storefront keywords - type = LIB_S
	public List<UserProfileVO> getSites(String sortColumn, String sortOrder) throws Exception {
		String orderBy = " order by customField1, customField2";
		
		String sqlOld = "select u.keyword, u.user_id userId, ucd.custom_field_2 customField2," 
				+ " ucd.custom_field_1 customField1, ucd.custom_field_2 customField2,"
				+ " kw.status customField3, u.site_id siteId"		
				+ " from user u, user_customer_defined ucd, keyword_application kw"
				+ " where u.user_id = ucd.user_id"
				+ " and u.keyword = kw.keyword"
				+ " and kw.status = 'P'"
				+ " and u.site_id = kw.site_id"
				+ " and ucd.custom_field_4 = 'LIB_S'"
				+ " and ucd.custom_field_5 = 1"
				+ " and ucd.custom_field_8 = 4"
				+ " and u.site_id = kw.site_id"
				+ " and u.site_id = ?";
	
		String sql = "select u.keyword, u.user_id userId, ucd.custom_field_2 customField2," 
				+ " ucd.custom_field_1 customField1, ucd.custom_field_2 customField2,"
				+ " kw.status customField3, u.site_id siteId"		
				+ " from user u, user_customer_defined ucd, keyword_application kw"
				+ " where u.user_id = ucd.user_id"
				+ " and u.keyword = kw.keyword"
				+ " and kw.status = 'P'"
				+ " and u.site_id = kw.site_id"
				+ " and ucd.custom_field_4 = 'LIB_S'"
				+ " and ucd.custom_field_5 = 1"
				+ " and ucd.custom_field_6 = 'LTCORP'"				
				+ " and ucd.custom_field_8 = 4"
				+ " and u.site_id = ?";	
		
		if (sortColumn != null) {
			orderBy = " order by " + sortColumn + " " + sortOrder;
		}
		
		sql += orderBy;
		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql)
					.addScalar("userId", new LongType())
					.addScalar("siteId", new IntegerType())
					.addScalar("keyword", new StringType())
					.addScalar("customField1", new StringType())					
					.addScalar("customField2", new StringType())
					.addScalar("customField3", new StringType())					
					.setResultTransformer(Transformers.aliasToBean(UserProfileVO.class));
			
			List<UserProfileVO> sites = q.setInteger(0, siteId)
											.list();			
			
			if (sites.isEmpty())
				return null;
			
			logger.debug("getSites: size = " + sites.size());
			return sites;
		} finally {
			close();
		}
	}
	
	//get all the lists for this user based on the access level given a starting listname letter
	public List<TargetUserList> getLists(Long userId, String letter) throws Exception {
		String sql = "select tul.* "
				+ " from target_user_list tul, user u, user_customer_defined ucd,"
				+ " keyword_application kw, liberty_closure lc"
				+ " where tul.list_name like ?"
				+ " and u.user_id = ucd.user_id"
				+ " and u.user_id = tul.user_id"
				+ " and ucd.custom_field_4 = 'LIB_S'"
				+ " and ucd.custom_field_5 = 1"
				+ " and ucd.custom_field_8 = 4"					
				+ " and u.keyword = kw.keyword"
				+ " and kw.status = 'P'"
				+ " and u.user_id = lc.child_id and lc.parent_id = ?";
		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql).addEntity("tul", TargetUserList.class);
			List<TargetUserList> lists = q.setString(0, letter+"%").setLong(1, userId).list();
			
			logger.debug("getLists: size = " + lists.size());
			return lists;
		} finally {
			close();
		}
	}
	
	//get all the lists for this user based on the access level
	public List<SendMessageList> getLists1(Long userId) throws Exception {
		String sql = "select u.keyword, tul.* "
				+ " from target_user_list tul, user u, user_customer_defined ucd, liberty_closure lc"
				+ " where u.user_id = ucd.user_id"
				+ " and u.user_id = tul.user_id"
				+ " and ucd.custom_field_4 = 'LIB_S'"
				+ " and ucd.custom_field_5 = 1"
				+ " and ucd.custom_field_8 = 4"					
				+ " and u.user_id = lc.child_id and lc.parent_id = ?";
		
		List<SendMessageList> lists = new ArrayList<SendMessageList>();
		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql).setResultTransformer(Transformers.aliasToBean(SendMessageList.class));
			lists = q.setLong(0, userId)
						.list();			
			
			if (lists.isEmpty())
				return null;
			
			logger.debug("getLists: size = " + lists.size());
			return lists;
		} finally {
			close();
		}
	}
	
	public Map<String, List<TargetUserList>> getLists(Long userId) throws Exception {
		String sql = "select u.keyword, tul.*, ucd.custom_field_2 office_id "
				+ " from target_user_list tul, user u, user_customer_defined ucd, "
				+ " keyword_application kw, liberty_closure lc"
				+ " where u.user_id = ucd.user_id"
				+ " and u.user_id = tul.user_id"
				+ " and ucd.custom_field_4 = 'LIB_S'"
				+ " and ucd.custom_field_5 = 1"
				+ " and ucd.custom_field_8 = 4"					
				+ " and u.keyword = kw.keyword"
				+ " and kw.status = 'P'"
				+ " and u.user_id = lc.child_id and lc.parent_id = ?";
		
		Object[] obj = new Object[] {userId};
		
		return getListAsMap(sql, obj);
	}
	
	//Get lists for a Corporate user
	public Map<String, List<TargetUserList>> getLists() throws Exception {
		String sql = "select u.keyword, ucd.custom_field_2 office_id, tul.* "
				+ " from target_user_list tul, user u, user_customer_defined ucd, keyword_application kw"
				+ " where u.user_id = ucd.user_id"
				+ " and ucd.custom_field_4 = 'LIB_S'"
				+ " and ucd.custom_field_5 = 1"
				+ " and ucd.custom_field_8 = 4"					
				+ " and u.keyword = kw.keyword"
				+ " and kw.status = 'P'"
				+ " and u.user_id = tul.user_id";
				
		return getListAsMap(sql, null);
	}
	
	//get the custom fields for a user
	public List<CustomFields> getCustomFields(Long userId) throws Exception {
		String sql = "from CustomFields where userId = ?";

		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createQuery(sql);
			List<CustomFields> cfields = q.setLong(0, userId).list();
			
			return cfields;
		} finally {
			close();
		}	
	}
	
	//get the custom fields for a user given officeId
	public List<CustomFields> getCustomFieldsByOffice(String id) throws Exception {
		String sql = "from CustomFields where officeId = ?";

		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createQuery(sql);
			List<CustomFields> cfields = q.setString(0, id).list();
			
			return cfields;
		} finally {
			close();
		}	
	}
	
	//get the custom fields for a user given entityId
	public CustomFields getCustomFieldsByEntity(String entityId) throws Exception {
		String sql = "from CustomFields where entityId = ? order by userType";

		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createQuery(sql);
			List<CustomFields> cfields = q.setString(0, entityId).list();
			
			if (cfields.size() == 0)
				return null;
			
			return cfields.get(0);
		} finally {
			close();
		}	
	}
	
	public User getUserByOfficeId(String officeId) throws Exception {
		String sql = "select u.* from user u, user_customer_defined cf where cf.user_id = u.user_Id and cf.custom_field_2 = ?";

		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql).addEntity("user", User.class);
			User user = (User) q.setString(0, officeId).list().get(0); //should be only one
			
			return user;
		} finally {
			close();
		}	
	}
	
	public User getUserByEntityId(String entityId) throws Exception {
		String sql = "select u.* from user u, user_customer_defined cf where cf.user_id = u.user_Id"
					+ " and cf.custom_field_1 = ? and cf.custom_field_2 is null";

		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql).addEntity("user", User.class);
			User user = (User) q.setString(0, entityId).list().get(0); //should be only one
			
			return user;
		} finally {
			close();
		}	
	}
	
	public KeywordApplication getKeywordByOfficeId(String officeId) throws Exception {
		String sql = "select kw.* from keyword_application kw, user u, user_customer_defined cf"
					+ " where cf.user_id = u.user_id and cf.custom_field_2 = ?"
					+ " and cf.custom_field_4 = 'LIB_S'"
					+ " and kw.keyword = u.keyword";

		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql).addEntity("kw", KeywordApplication.class);
			List<KeywordApplication> kwAppls = (List<KeywordApplication>) q.setString(0, officeId)
								.list(); //should be only one
			
			if (kwAppls.isEmpty())
				return null;
			
			return kwAppls.get(0);
		} finally {
			close();
		}	
	}
	
	public KeywordApplication getKeywordByEntityId(String entityId) throws Exception {
		String sql = "select kw.* from keyword_application kw, user u, user_customer_defined cf"
					+ " where cf.user_id = u.user_id and cf.custom_field_1 = ?"
					+ " and cf.custom_field_4 in ('LIB_F', 'LIB_AD')"
					+ " and kw.keyword = u.keyword";

		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql).addEntity("kw", KeywordApplication.class);
			List<KeywordApplication> kwAppls = (List<KeywordApplication>) q.setString(0, entityId)
								.list(); //should be only one
			
			if (kwAppls.isEmpty())
				return null;
			
			return kwAppls.get(0);
		} finally {
			close();
		}	
	}
	
	public KeywordApplication getKeywordByUserId(Long userId) throws Exception {
		String sql = "select kw.* from keyword_application kw, user u"
					+ " where kw.keyword = u.keyword"
					+ " and kw.status = 'P'"
					+ " and u.user_id = ?";

		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql).addEntity("kw", KeywordApplication.class);
			List<KeywordApplication> kwAppls = (List<KeywordApplication>) q.setLong(0, userId)
								.list(); //should be only one
			
			if (kwAppls.isEmpty())
				return null;
			
			return kwAppls.get(0);
		} finally {
			close();
		}	
	}
	
	//search for lists
	public Map<String, List<TargetUserList>> searchList(Integer siteId, String searchKeywordStr, String searchOfficeIdStr, 
													String searchCityStr, String searchEntityIdStr, String searchStateStr, String searchDMAString) throws Exception {	
		String sql = "select u.keyword, tul.*, ucd.custom_field_2 office_id"
				+ " from target_user_list tul, user u, user_customer_defined ucd, profile p, keyword_application kw"
				+ " where u.user_id = ucd.user_id"
				+ " and u.user_id = tul.user_id"
				+ " and u.user_id = p.user_id"
				+ " and u.site_id = ?"
				+ " and u.keyword like ?"
				+ " and u.keyword = kw.keyword"
				+ " and kw.status = 'P'"
				+ " and ucd.custom_field_4 = 'LIB_S'"
				+ " and ucd.custom_field_5 = 1"
				+ " and ucd.custom_field_8 = 4"					
				+ " and ucd.custom_field_2 like ?"
				+ " and p.business_city like ?"
				+ " and ucd.custom_field_1 like ?";
			
		if (searchStateStr != null && ! searchStateStr.equals(""))
			sql += " and p.business_state = '" + searchStateStr + "'";
		
		if (searchDMAString != null && ! searchDMAString.equals(""))
			sql += " and ucd.custom_field_7 = '" + searchDMAString + "'";	
		
		logger.debug("sql: " + sql);
		
		Object[] obj = new Object[] {siteId, "%"+searchKeywordStr+"%", "%"+searchOfficeIdStr+"%", "%"+searchCityStr+"%", 
										"%"+searchEntityIdStr+"%"};
		
		return getListAsMap(sql, obj);
	}
	
	//search for keywords
	public List<UserProfileVO> searchKeyword(Integer siteId, String searchKeywordStr, String searchOfficeIdStr, 
											String searchCityStr, String searchEntityIdStr, String searchStateStr, String searchDMAString) throws Exception {	
		String sql = "select u.user_id userId, u.keyword, ucd.custom_field_1 customField1, ucd.custom_field_2 customField2"
				+ " from user u, user_customer_defined ucd, profile p, keyword_application kw"
				+ " where u.user_id = ucd.user_id"
				+ " and u.user_id = p.user_id"
				+ " and u.site_id = :siteId"
				+ " and u.keyword like :keyword"
				+ " and u.keyword = kw.keyword"
				+ " and kw.status = 'P'"
				+ " and ucd.custom_field_4 = 'LIB_S'"
				+ " and ucd.custom_field_5 = 1"
				+ " and ucd.custom_field_8 = 4"					
				+ " and ucd.custom_field_2 like :officeId"
				+ " and p.business_city like :city"
				+ " and ucd.custom_field_1 like :entityId";

		if (searchStateStr != null && ! searchStateStr.equals(""))
			sql += " and p.business_state = '" + searchStateStr + "'";
		
		if (searchDMAString != null && ! searchDMAString.equals(""))
			sql += " and ucd.custom_field_7 = '" + searchDMAString + "'";
		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql)
					.addScalar("userId", new LongType())
					.addScalar("keyword", new StringType())
					.addScalar("customField1", new StringType())					
					.addScalar("customField2", new StringType())
					.setResultTransformer(Transformers.aliasToBean(UserProfileVO.class));
			
			List<UserProfileVO> kwList = q.setInteger("siteId", siteId)
											.setString("keyword", "%"+searchKeywordStr+"%")
											.setString("officeId", "%"+searchOfficeIdStr+"%")
											.setString("city", "%"+searchCityStr+"%")
											.setString("entityId", "%"+searchEntityIdStr+"%")
											.list();			
			
			if (kwList.isEmpty())
				return null;
			
			return kwList;
		} finally {
			close();
		}
	}
	
	//used internally to get the results as a Map
	private  Map<String, List<TargetUserList>> getListAsMap(String sql, Object[] obj) throws Exception {
		Map<String, List<TargetUserList>> lists = new HashMap<String, List<TargetUserList>>();

		try {
			logger.debug("sql: " + sql);
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql);
			
			dbs = new DBStuff(session.connection());
			ResultSet rs = null;
			if (obj != null)
				rs = dbs.getFromDB(sql, obj);
			else
				rs = dbs.getFromDB(sql);

			String oldKey = "";
			ArrayList<TargetUserList> tuList = new ArrayList<TargetUserList>();
			
			while (rs.next()) {
				String newKey = rs.getString("keyword");
				if (! newKey.equals(oldKey)) {
					if (tuList.size() > 0) {
						lists.put(oldKey + "-" + tuList.get(0).getListPath(), tuList); //save the key as keyword - officeId
						tuList = new ArrayList<TargetUserList>();
					}
					oldKey = newKey;
				}
				TargetUserList tul = new TargetUserList();
				tul.setListId(rs.getString("list_id"));
				tul.setUserId(rs.getLong("user_id"));
				tul.setListName(rs.getString("list_name"));
				tul.setListPath(rs.getString("office_id")); //use this to store the officeId
				tul.setIsSelected(false);
				tuList.add(tul);
			}
			
			if (! tuList.isEmpty())
				lists.put(oldKey, tuList);
			
			logger.debug("getLists: size = " + lists.size());
			return lists;
		} finally {
			close();
		}
	}
	
	//get the list of approved messages for CA
	public List<ApprovedMessage> getApprovedMsgsCA(Integer siteId, String lang) throws Exception {
		//String sql = "select id, message_text from approved_messages where site_id = ? and language = ? and user_id is null and location = ?";
		String sql = "from ApprovedMessage where siteId = :siteId and language = :lang and userId is null and location = :loc";
				
		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createQuery(sql);
			List<ApprovedMessage> msgList = q.setInteger("siteId", siteId)
											.setString("lang", lang)
											.setString("loc", "CA")
											.list();
			if (msgList.isEmpty())
				return null;
			
			return msgList;
		} finally {
			close();
		}	
	}
	
	//get the list of approved messages - just the Corporate msgs
	public List<ApprovedMessage> getApprovedMsgs(Integer siteId, String lang) throws Exception {
		String sql = "from ApprovedMessage where siteId = :siteId and language = :lang and userId is null";
		
		logger.debug("getApprovedMsgs start 1: " + Calendar.getInstance().getTimeInMillis());

		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createQuery(sql);
			
			List<ApprovedMessage> msgList = q.setInteger("siteId", siteId)
											.setString("lang", lang)
											.list();
			if (msgList.isEmpty())
				return null;
			
			logger.debug("getApprovedMsgs end 1: " + Calendar.getInstance().getTimeInMillis());
			
			return msgList;
		} finally {
			close();
		}	
	}

	public List<ApprovedMessage> getApprovedMsgs(Integer siteId) throws Exception {
		return this.getApprovedMsgs(siteId, "EN");
	}

	//get the list of custom approved messages
	public List<ApprovedMessage> getCustomMsgs(Integer siteId, String entityId) throws Exception {
		String sql = "from ApprovedMessage where siteId = :siteId and entityId = :entityId and status = 'A'";
		
		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createQuery(sql);
			
			List<ApprovedMessage> msgList = q.setInteger("siteId", siteId)
											.setString("entityId", entityId)
											.list();
			if (msgList.isEmpty())
				return null;
			
			return msgList;
		} finally {
			close();
		}	
	}
	
	//get the list of custom approved messages for the last x days
	//the entity_id field in approved_messages table can hold either the entityId or the officeId
	//based on who created it
	//if entType == Entity, get the msgs for this entity and all their offices
	public List<ApprovedMessage> getCustomMsgs(Integer siteId, Long userId, String entType, String status, Integer days) throws Exception {
		String esql = "select ap.* from approved_messages ap, user_customer_defined ucd"
					+ " where ap.site_id = :siteId"
					+ " and ap.status = :status"
					+ " and ucd.custom_field_1 = (select custom_field_1 from user_customer_defined where user_id = :userId)"				
					+ " and (ap.office_id = ucd.custom_field_2 or ap.user_id = ucd.user_id)";
		
		String osql = "select ap.* from approved_messages ap"
					+ " where ap.site_id = :siteId"
					+ " and ap.status = :status"				
					+ " and ap.user_id = :userId";					
		
		/*
		String osql = "select ap.* from approved_messages ap"
				+ " where ap.site_id = :siteId"
				+ " and ap.status = :status"				
				+ " and (ap.user_id = :userId or entity_id = (select custom_field_1 from"
				+ " user_customer_defined where user_id = " + userId + "))";
		*/	
		
		String csql = "select ap.* from approved_messages ap"
				+ " where ap.site_id = :siteId"
				+ " and ap.status = :status"				
				+ " and ap.user_id > :userId";
		
		String sql = osql;
		
		if (entType.equals("Ent"))
			sql = esql;

		if (entType.equals("Corp")) {
			sql = csql;
			userId = 0L;
		}
		
		if (days != null)
			sql += " and ap.updated >= date_sub(curdate(), interval " + days + " day)";
		
		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql).addEntity("ap", ApprovedMessage.class);
			
			List<ApprovedMessage> msgList = q.setInteger("siteId", siteId)
											.setLong("userId", userId)
											.setString("status", status)											
											.list();
			if (msgList.isEmpty())
				return null;
			
			return msgList;
		} finally {
			close();
		}	
	}
	
	//For Corporate - no userId
	public List<ApprovedMessage> getCustomMsgs(Integer siteId, String status, Integer days) throws Exception {
		String sql = "select ap.* from approved_messages ap"
					+ " where ap.site_id = :siteId"
					+ " and ap.status = :status";				
		
		if (days != null)
			sql += " and ap.updated >= date_sub(curdate(), interval " + days + " day)";
		
		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql).addEntity("ap", ApprovedMessage.class);
			
			List<ApprovedMessage> msgList = q.setInteger("siteId", siteId)
											.setString("status", status)											
											.list();
			if (msgList.isEmpty())
				return null;
			
			return msgList;
		} finally {
			close();
		}	
	}
	
	public ApprovedMessage getCustomMsgByIdNA(Integer msgId) throws Exception {
		String sql = "select ap.* from approved_messages ap"
					+ " where ap.id = :msgId";
		
		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql).addEntity("ap", ApprovedMessage.class);
			
			List<ApprovedMessage> msgList = q.setInteger("msgId", msgId)
											.list();
			if (msgList.isEmpty())
				return null;
			
			return msgList.get(0);
		} finally {
			close();
		}	
	}
	
	public ApprovedMessage getCustomMsgById(Integer msgId) throws Exception {
		String sql = "from ApprovedMessage ap"
					+ " where ap.id = :msgId";
		
		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createQuery(sql);
			
			List<ApprovedMessage> msgList = q.setInteger("msgId", msgId)
											.list();
			if (msgList.isEmpty())
				return null;
			
			return msgList.get(0);
		} finally {
			close();
		}	
	}
	
	//get the list of approved & rejected messages for an AD
	public List<ApprovedMessage> getApprovedMsgsAD(Integer siteId, Long userId) throws Exception {
		String sql = "from ApprovedMessage where siteId = ? and userId = ? and status in ('A', 'R') order by status";
		List<ApprovedMessage> msgList = new ArrayList<ApprovedMessage>();
		
		try {		
			session = HibernateUtil.currentSession();
			org.hibernate.Query q = session.createQuery(sql);
			msgList = q.setInteger(0, siteId).setLong(1, userId).list();

			return msgList;
		} finally {
			close();
		}	
	}

	//get the list of pending messages for an AD
	public List<ApprovedMessage> getPendingMsgsAD(Integer siteId, Long userId) throws Exception {
		String sql = "from ApprovedMessage where siteId = ? and userId = ? and status = 'P'";
		List<ApprovedMessage> msgList = new ArrayList<ApprovedMessage>();
		
		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createQuery(sql);
			msgList = q.setInteger(0, siteId).setLong(1, userId).list();

			return msgList;
		} finally {
			close();
		}	
	}
	
	public List<ApprovedMessage> getPendingMsgs(String eid) throws Exception {
		String sql = "from ApprovedMessage where entityId = ? and status = 'P'";
		List<ApprovedMessage> msgList = new ArrayList<ApprovedMessage>();
		
		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createQuery(sql);
			msgList = q.setString(0, eid).list();

			return msgList;
		} finally {
			close();
		}	
	}
	
	//save
	public void saveObject(Object obj) throws Exception {
		try {
			session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			session.saveOrUpdate(obj);
			tx.commit();
		} finally {
			close();
		}		
	}
	
	public void deleteObject(Object obj) throws Exception {
		try {
			session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			session.delete(obj);
			tx.commit();
		} finally {
			close();
		}		
	}
	
	public int deleteSAF(String officeId) throws Exception {
		String sql = "delete SAF where officeId = :officeId";
		int rows = 0;
		
		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createQuery(sql);
			q.setParameter("officeId", officeId);
			rows = q.executeUpdate();
		} catch (Exception e) {
		} finally {
			close();
		}
		return rows;
	}
	
	//update msg status to A (for Approved)
	public int updateADMsg(String msgId) throws Exception {
		String sql = "update approved_messages set status = 'A' where id = ?";
		String gSql = "select ap from ApprovedMessage ap where id = :id";
		Transaction tx = null;
		
		try {		
			session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			ApprovedMessage ap = (ApprovedMessage)session.get(ApprovedMessage.class, msgId); 
			if (ap == null) {
				throw new Exception("No object found with id: " + msgId);
			}
			ap.setStatus("A");
			session.saveOrUpdate(ap);

			return 1;
		} catch (HibernateException e) {
			if (tx != null) 
				tx.rollback();
			throw new Exception(e);
		} finally {
			close();
		}	
	}

	//get the list of pending AD messages
	public List<ApprovedMessage> getPendingMsgsAD(Integer siteId) throws Exception {
		String sql = "from ApprovedMessage where siteId = ? and status = 'P'";
		List<ApprovedMessage> msgList = new ArrayList<ApprovedMessage>();
		
		try {		
			session = HibernateUtil.currentSession();
			org.hibernate.Query q = session.createQuery(sql);
			msgList = q.setInteger(0, siteId).list();

			return msgList;
		} finally {
			close();
		}	
	}
	
	//Change keyword
	public void changeKW(String oldKeyword, String keyword, Integer siteId) throws Exception {
		String sql = "select u.user_id as userId from user u"
					+ " where u.keyword = ?"
					+ " and u.site_id = ?";
		
		String usql1 = "update user set keyword = ? where user_id = ? and keyword = ?";
		String usql2 = "update target_user_list set list_name = ? where user_id = ? and list_name = ?";
		String usql3 = "update keyword_application set keyword = ? where keyword = ?";
			
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql)
					.addScalar("userId", new LongType())					
					.setResultTransformer(Transformers.aliasToBean(UserProfileVO.class));
			
			List<UserProfileVO> uids = q.setString(0, oldKeyword)
										.setInteger(1, siteId)
										.list();			

			if (uids.isEmpty())
				throw new Exception("Null userId");
			
			Long userId = uids.get(0).getUserId();			
			logger.debug("userId: " + userId);
			
			q = session.createSQLQuery(usql1);	
			q.setString(0, keyword)
			  .setLong(1, userId)
			  .setString(2, oldKeyword);					
			int ret = q.executeUpdate();			
			logger.debug(ret + " rows updated in user");
			
			q = session.createSQLQuery(usql2);	
			q.setString(0, keyword)
			 .setLong(1, userId)
			 .setString(2, oldKeyword);					
			ret = q.executeUpdate();
			logger.debug(ret + " rows updated in target_user_list");
	
			q = session.createSQLQuery(usql3);	
			q.setString(0, keyword)
			 .setString(1, oldKeyword);					
			ret = q.executeUpdate();
			logger.debug(ret + " rows updated in keyword_application");
		} finally {
			close();
		}	
	}
	
	//update the territory info as part of the incremental load
	public Map<String, String> updateTerritoy(Territory territory) throws Exception {
		String sql = "update user_customer_defined set custom_field_1 = ?";
		Map<String, String> msgList = new HashMap<String, String>();
		
		try {		
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(session.connection());
			ResultSet rs = dbs.getFromDB(sql, new Object[] {});
			while (rs.next()) {
				msgList.put(rs.getString("message_text"), rs.getString("id"));
			}
			
			return msgList;
		} finally {
			close();
		}	
	}
	
	public Territory getTerritory(String territoryId) throws Exception {
		String sql = " from Territory where territoryId = ?";

		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createQuery(sql);
			List<Territory> terrList = q.setString(0, territoryId).list();
			
			if (terrList.isEmpty())
				return null;
			
			return terrList.get(0);
		} finally {
			close();
		}	
	}
	
	public List<Territory> getTerritoryByMBEntity(String mbEntityId) throws Exception {
		String sql = " from Territory where mbEntityId = ?";

		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createQuery(sql);
			List<Territory> terrList = q.setString(0, mbEntityId).list();
			
			return terrList;
		} finally {
			close();
		}	
	}
	
	public List<Territory> getTerritoryByEntity(String entityId) throws Exception {
		String sql = " from Territory where entityId = ?";

		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createQuery(sql);
			List<Territory> terrList = q.setString(0, entityId).list();
			
			return terrList;
		} finally {
			close();
		}	
	}
	
	//get officeIds for a given territoryId
	public List<String> getOfficeByTerritory(String territoryId) throws Exception {
		String sql = "select custom_field_2 from user_customer_defined where custom_field_6 = ?";
		List<String> officeIds = new ArrayList<String>();
		
		try {		
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(session.connection());
			ResultSet rs = dbs.getFromDB(sql, new Object[] {territoryId});
			while (rs.next()) {
				officeIds.add(rs.getString("custom_field_2"));
			}
			
			return officeIds;
		} finally {
			close();
		}	
	}
	
	//get the mobile numbers give a list of AD ids
	public void createAccountingADList(String listId, Map<String, String> adMap) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
    	String sql = "insert into target_list_data(list_id, mobile_phone, address2)"
    				+ " select ?, p.admin_mobile_phone, ? from profile p, user_customer_defined ucd"
    				+ " where p.user_id = ucd.user_id"
    				+ " and ucd.custom_field_1 = ?"
    				+ " and (ucd.custom_field_4 = 'LIB_AD' or ucd.custom_field_4 = 'LIB_F')";
    	
		try {			
			session = HibernateUtil.currentSession();
			conn = session.connection();
		    pstmt = conn.prepareStatement(sql);
			
			for (Map.Entry<String, String> entry : adMap.entrySet()) {
				pstmt.setString(1, listId);
				pstmt.setString(2, entry.getValue());				
				pstmt.setString(3, entry.getKey());
				
				pstmt.addBatch();
			}

			int[] rows = pstmt.executeBatch();
			logger.info(rows.length + " rows inserted");	
		} catch (BatchUpdateException be) {
			be.printStackTrace();
			logger.error("Ignoring dup");
			return;
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		} finally {
			session.flush();
		}
	}
	
	//get the mobile numbers give a list of AD ids
	public void createAccountingADListOld(String listId, List<String> adIds) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
    	String sql = "insert into target_list_data(list_id, mobile_phone)"
    				+ " select ?, p.admin_mobile_phone from profile p, user_customer_defined ucd"
    				+ " where p.user_id = ucd.user_id"
    				+ " and ucd.custom_field_1 = ?"
    				+ " and (ucd.custom_field_4 = 'LIB_AD' or ucd.custom_field_4 = 'LIB_F')";
    	
		try {			
			session = HibernateUtil.currentSession();
			conn = session.connection();
		    pstmt = conn.prepareStatement(sql);
			
			for (String adId : adIds) {
				pstmt.setString(1, listId);
				pstmt.setString(2, adId);
				
				pstmt.addBatch();
			}

			int[] rows = pstmt.executeBatch();
			logger.info(rows.length + " rows inserted");	
		} catch (BatchUpdateException be) {
			be.printStackTrace();
			logger.error("Ignoring dup");
			return;
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
		} finally {
			session.flush();
		}
	}
}
