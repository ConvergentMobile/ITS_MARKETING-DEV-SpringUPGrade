package dao;

import java.util.List;
import java.util.Map;

import liberty.CustomFields;
import mdp_common.DBStuff;

import org.hibernate.Query;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

import subclass.LTCategory_3;
import user.RoleAction;
import user.TargetUserList;
import user.User;
import user.UserDAOManager;
import util.PropertyUtil;
import category_3.Category_3;

import common.HibernateUtil;

import data.ApprovedMessage;
import data.ValueObject;

public class LTUserDAOManager extends UserDAOManager {
	private static final long serialVersionUID = 1L;

	public User loginLT(String role, String id) throws Exception {
		return this.loginLT(role, id, 1);
	}
	
	public User getUser(Long userId) throws Exception {
		User user = null;
		try {
			session = HibernateUtil.currentSession();
			//session.beginTransaction();
			user = (User)session.get(User.class, userId);
			//session.getTransaction().commit();
			
			return user;
		} finally {
			close();
		}
	}
	
	public LTCategory_3 getProfile(Long userId, String sql) throws Exception {
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createQuery(sql);
			List<LTCategory_3> profiles = q.setLong(0, userId).list();
			
			if (profiles.isEmpty())
				return null;
			
			return profiles.get(0);
		} finally {
			close();
		}
	}
	
	public LTCategory_3 getProfileFromSession(Long userId) throws Exception {
		LTCategory_3 catg = null;
		try {
			session = HibernateUtil.currentSession();
			//session.beginTransaction();
			catg = (LTCategory_3)session.get(LTCategory_3.class, userId);
			//session.getTransaction().commit();
			
			return catg;
		} finally {
			//close();
		}
	}
	
	//if mode == 1, do not add the All list to tul
	public User loginLT(String role, String id, int mode) throws Exception {
		String l_sql = "from CustomFields where";
		
		try {
			if (role.equals("Entity"))
				l_sql += " entityId = ? and userType = 'LIB_F'";

			if (role.equals("AD"))
				l_sql += " entityId = ? and userType = 'LIB_AD'";
			
			if (role.equals("Office"))
				l_sql += " officeId = ? and userType = 'LIB_S'";

			if (role.equals("Transmitter"))
				l_sql += " entityId = ? and userType = 'LIB_S'";
			
			session = HibernateUtil.currentSession();
			
			Query q = session.createQuery(l_sql);
			List<CustomFields> cfields = q.setString(0, id).list();
			if (cfields.isEmpty()) {
				return null;
			}
			
			User user = this.login(cfields.get(0).getUserId(), mode);
			user.setUserLevel(cfields.get(0).getOfficeId()); //set this to indicate the user's hierarchy level - Store or Franchisee
			
			//set the location - US or CA
			String loc = cfields.get(0).getLocation() != null ? cfields.get(0).getLocation() : "US";
			user.setBillingCountry(loc);
			
			return user;
		} finally {
			close();
		}
	}
	
	//get the actions allowed for a role
	public List<RoleAction> getRoleActions(String role) throws Exception {
		String sql = "select role_action roleAction, role_action_url roleActionUrl, role_type roleType from liberty_roles"
					+ " where role = :role and site_id = :siteId";
		
		Integer siteId = Integer.valueOf(PropertyUtil.load().getProperty("siteId"));
				
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql)
					.addScalar("roleAction", new StringType())					
					.addScalar("roleActionUrl", new StringType())
					.addScalar("roleType", new StringType())					
					.setResultTransformer(Transformers.aliasToBean(RoleAction.class));	
			
			List<RoleAction> roleActions = q.setString("role", role)
											.setInteger("siteId", siteId)
											.list();			
			
			return roleActions;
		} finally {
			close();
		}
	}
	
	public CustomFields getCustomFields(String id, int type) throws Exception {
		String sql = " from CustomFields where entityId = ? and officeId is null"; //get only the entity, not the stores (can use type=LIB_S as well)
		
		if (type == 2)
			sql = " from CustomFields where officeId = ?";
		
		session = HibernateUtil.currentSession();
		
		Query q = session.createQuery(sql);
		List<CustomFields> cfields = q.setString(0, id).list();
		if (cfields.isEmpty()) {
			return null;
		}
		
		return cfields.get(0);
	}
	
	//get CustomFields given userId
	public CustomFields getCustomFields(Long userId) throws Exception {
		String sql = " from CustomFields where userId = ?";
		
		session = HibernateUtil.currentSession();
		
		Query q = session.createQuery(sql);
		List<CustomFields> cfields = q.setLong(0, userId).list();
		if (cfields.isEmpty()) {
			return null;
		}
		
		return cfields.get(0);
	}
	
	//get the lists for a set of offices + Entity
	public List<TargetUserList> getList(List<String> officeIds, String entityId) throws Exception {
		String sql = "select tul.list_id, tul.user_id, tul.list_name, tul.list_path, ucd.custom_field_2 as list_display_path, tul.list_type"
				+ " from target_user_list tul, user_customer_defined ucd, user u, keyword_application kw"
				+ " where ucd.user_id = tul.user_id"
				+ " and ucd.user_id = u.user_id"
				+ " and u.keyword = kw.keyword"
				+ " and kw.status = 'P'"
				+ " and (ucd.custom_field_2 in (:ids)"
				+ " 	or ucd.custom_field_1 = :entityId)";
		
		session = HibernateUtil.currentSession();
		
		Query q = session.createSQLQuery(sql).addEntity("tul", TargetUserList.class);
		List<TargetUserList> tuList = q.setParameterList("ids", officeIds)
										.setParameter("entityId", entityId)
										.list();
		if (tuList.isEmpty()) {
			return null;
		}
		
		return tuList;
	}
	
	//get the lists for a set of offices
	public List<TargetUserList> getList(List<String> officeIds) throws Exception {
		String sql = "select tul.list_id, tul.user_id, tul.list_name, tul.list_path, ucd.custom_field_2 as list_display_path, tul.list_type"
					+ " from target_user_list tul, user_customer_defined ucd, user u, keyword_application kw"
					+ " where ucd.user_id = tul.user_id"
					+ " and ucd.user_id = u.user_id"
					+ " and u.keyword = kw.keyword"
					+ " and kw.status = 'P'"					
					+ " and ucd.custom_field_2 in (:ids)";
		
		session = HibernateUtil.currentSession();
		
		Query q = session.createSQLQuery(sql).addEntity("tul", TargetUserList.class);
		List<TargetUserList> tuList = q.setParameterList("ids", officeIds).list();
		if (tuList.isEmpty()) {
			return null;
		}
		
		return tuList;
	}
	
	//get the quota for an entity
	public List<ValueObject> getMsgQuota(Long userId) throws Exception {
		String sql = "select lmc.entity_id field1, messages_allowed field2, messages_used field3 from liberty_message_count lmc, user_customer_defined u"
					+ " where u.user_id = :userId and lmc.entity_id = u.custom_field_1";
				
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql)
					.addScalar("field1", new LongType())					
					.addScalar("field2", new IntegerType())
					.addScalar("field3", new IntegerType())					
					.setResultTransformer(Transformers.aliasToBean(ValueObject.class));	
			
			List<ValueObject> quota = q.setLong("userId", userId).list();

			return quota;
		} finally {
			close();
		}
	}
	
	//update the counts
	public int updateMsgQuota(Long userId, Integer msgsUsed) throws Exception {
		String sql = "update liberty_message_count lmc, user_customer_defined u"
					+ " set lmc.messages_used = ?"
					+ " where u.user_id = ? and lmc.entity_id = u.custom_field_1";

		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql);	
			q.setInteger(0, msgsUsed)
			 .setLong(1, userId);
					
			int ret = q.executeUpdate();
			
			return ret;
		} finally {
			close();
		}
	}
	
	//get all offices for a user
	public List<ValueObject> getAllOffices(Long userId) throws Exception {
		String sql = "select eo.custom_field_2 field1, eo.custom_field_2 field2"
				+ " from user_customer_defined eo, liberty_closure lc"
				+ " where eo.custom_field_4 = 'LIB_S'"
				+ " and eo.custom_field_5 = 1"
				+ " and eo.custom_field_8 = 4"			
				+ " and eo.user_id = lc.child_id and lc.parent_id = :parentId"
				+ " order by eo.custom_field_2";
		
		//Map<String, String> offices = new LinkedHashMap<String, String>();

		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql)
					.addScalar("field1", new StringType())					
					.addScalar("field2", new StringType())
					.setResultTransformer(Transformers.aliasToBean(ValueObject.class));							
			List<ValueObject> res = q.setLong("parentId", userId).list();
			
			if (res.isEmpty())
				return null;
			
			return res;
		} finally {
			close();
		}
	}	
	
	//get list of values for hours
	public List<ValueObject> getHoursLT() throws Exception {
		DBStuff dbs = null;
		String sql = "select id field1, hour field2 from hours";
			
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql)
					.addScalar("field1", new IntegerType())					
					.addScalar("field2", new StringType())
					.setResultTransformer(Transformers.aliasToBean(ValueObject.class));					
			List<ValueObject> hours = q.list();
			
			if (hours.isEmpty())
				return null;			
			
			return hours;
		} finally {
			close();
		}
	}
	
	//get a list of all the CA states
	public List<ValueObject> getCAStatesLT() throws Exception {
		DBStuff dbs = null;
		String sql = "select name field1, code field2 from ca_state_codes";
				
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql)
					.addScalar("field1", new StringType())					
					.addScalar("field2", new StringType())					
					.setResultTransformer(Transformers.aliasToBean(ValueObject.class));					
			List<ValueObject> sites = q.list();

			if (sites.isEmpty())
				return null;
			
			return sites;
		} finally {
			dbs.close();
			close();
		}
	}	
	
	//return a list of just the phone numbers and userId
	public List<ValueObject> getListData(String listId) throws Exception {
		String sql = "select mobile_phone field1, tul.user_id field2, tld.address2 field3"
					+ " from target_list_data tld, target_user_list tul"
					+ " where tld.list_id = :listId "
					+ " and tul.list_id = tld.list_id"
					+ " order by mobile_phone";

		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql)
					.addScalar("field1", new StringType())					
					.addScalar("field2", new LongType())
					.addScalar("field3", new StringType())										
					.setResultTransformer(Transformers.aliasToBean(ValueObject.class));						
										
			List<ValueObject> listData = q.setString("listId", listId).list();

			return listData;
		} finally {
			close();
		}
	}	
	
	//delete a list
	public int deleteList(String listId) throws Exception {
		String sql_d = "delete from target_list_data where list_id = ?";
		String sql = "delete from target_user_list where list_id = ?";
		int numDeleted = 0;

		try {
			session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			numDeleted = session.createSQLQuery(sql_d)
						.setParameter(0, listId)
						.executeUpdate();
			numDeleted = session.createSQLQuery(sql)
	        			.setParameter(0, listId)
	        			.executeUpdate();
			tx.commit();
			return numDeleted;
		} finally {
			close();
		}
	}
	
	//opt out used by Corp
	//TO DO - change it so that insert into optout happens only if the number exists
	public int optout(String mobilePhone, String shortcode, String keyword) throws Exception {
		String sql = "insert into opt_out(phone_number, shortcode, keyword, firstname, lastname)"
				+ "	select tld.mobile_phone, ?, u.keyword, tld.first_name, tld.last_name"
				+ "	from target_list_data tld, target_user_list tul, user u"
				+ "	where tul.list_id = tld.list_id and u.user_id = tul.user_id and tld.mobile_phone in (?, ?)";
			
		String deleteSql = "delete tld"
				+ " FROM target_list_data tld"
				+ " where tld.mobile_phone in (?, ?)";

		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql);				
							
			int idx = 0;
			q.setString(idx++, shortcode)
			  .setString(idx++, mobilePhone)
			  .setString(idx++, mobilePhone.substring(1));
			
			int ret = q.executeUpdate();
			
			q = session.createSQLQuery(deleteSql);
			idx = 0;
			q.setString(idx++, mobilePhone)
				.setString(idx++, mobilePhone.substring(1));
			  
			ret = q.executeUpdate();
			
			return ret;
		} finally {
			close();
		}
	}	
	
	public TargetUserList getList(String listId) throws Exception {
		String sql = "select tul.list_id, tul.user_id, tul.list_name, tul.list_path, tul.list_display_path, tul.list_type"
					+ " from target_user_list tul"
					+ " where tul.list_id = :id";
		
		session = HibernateUtil.currentSession();
		
		Query q = session.createSQLQuery(sql).addEntity("tul", TargetUserList.class);
		List<TargetUserList> tuList = q.setParameter("id", listId).list();
		if (tuList.isEmpty()) {
			return null;
		}
		
		return tuList.get(0);
	}
	
	/*
	//opt out used by Corp
	public int optout(String mobilePhone, String shortcode, String keyword) throws Exception {
		String sql = "insert into opt_out(phone_number, shortcode, keyword, firstname, lastname)"
				+ " select ?, ?, ?,"
				+ "		(select tld.first_name from target_list_data tld, target_user_list tul, user u"
				+ "			where tul.list_id = tld.list_id and u.user_id = tul.user_id and u.keyword = ? and tld.mobile_phone in (?, ?)),"
				+ " 	(select tld.last_name from target_list_data tld, target_user_list tul, user u"
				+ "			where tul.list_id = tld.list_id and u.user_id = tul.user_id and u.keyword = ? and tld.mobile_phone in (?, ?))";		
			
		String deleteSql = "delete tld"
				+ " FROM target_user_list tul, keyword_application kw, user u, target_list_data tld"
				+ " where tul.user_id = u.user_id"
				+ " and tul.list_id = tld.list_id"
				+ " and tld.mobile_phone = ?"
				+ " and kw.keyword = ?"
				+ " and kw.shortcode in ('87411', 'US411')"
				+ " and u.keyword = kw.keyword";

		try {
			session = HibernateUtil.currentSession();
			Query q = session.createSQLQuery(sql);				
							
			int idx = 0;
			q.setString(idx++, mobilePhone)
			  .setString(idx++, shortcode)
			  .setString(idx++, keyword)
			  .setString(idx++, keyword)
			  .setString(idx++, mobilePhone)
			  .setString(idx++, mobilePhone.substring(1))
			  .setString(idx++, keyword)
			  .setString(idx++, mobilePhone)
			  .setString(idx++, mobilePhone.substring(1));
			
			int ret = q.executeUpdate();
			
			q = session.createSQLQuery(deleteSql);
			idx = 0;
			q.setString(idx++, mobilePhone)
			  .setString(idx++, keyword);
			  
			ret = q.executeUpdate();
			
			return ret;
		} finally {
			close();
		}
	}	
	*/
}
