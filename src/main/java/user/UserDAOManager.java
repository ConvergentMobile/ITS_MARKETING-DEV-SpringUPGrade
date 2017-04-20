package user;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import keyword.KeywordApplication;

import mdp_common.DBStuff;

import org.apache.log4j.Logger;
import org.apache.struts.util.LabelValueBean;
import org.apache.struts.util.MessageResources;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jfree.data.category.DefaultCategoryDataset;
import org.quartz.JobDataMap;

import reports_graphs.ReportData;

import common.HibernateUtil;

import util.FormPageDataForm;
import util.FormPageForm;
import util.GeneratePassword;
import util.PropertyUtil;
import util.US411Message;

public class UserDAOManager implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(UserDAOManager.class);
	
	protected DBStuff dbs = null;
	protected Session session = null;
	protected Transaction tx = null;
	//MessageResources messages = MessageResources.getMessageResources("MessageResources");
	
	public void close() {
		try {
			HibernateUtil.closeSession();
		} catch (Exception e) {
			logger.error("Exception closing session: " + e.toString());
		}
	}
	
	public User login1(String l, String p) throws Exception {
		String sql = "from User as u where u.login = ? and u.password = ?";
		try {
			session = HibernateUtil.currentSession();
			org.hibernate.query.Query q = session.createNativeQuery(sql);
			List<User> users = q.setParameter(0, l).setParameter(1, p).list();

			if (users.isEmpty()) {
				return null;
			}

			User m = (User) users.get(0);
			return m;
		} finally {
			close();
		}
	}
	
	//get user given keyword and siteId - for subscription managing
	public User login(String keyword, int siteId) throws Exception {
		String sql = "from User as u where u.keyword = ? and u.siteId = ?";
		try {
			session = HibernateUtil.currentSession();
			org.hibernate.query.Query q = session.createNativeQuery(sql);
			List<User> users = q.setParameter(0, keyword).setParameter(1, siteId).list();

			if (users.isEmpty()) {
				return null;
			}

			User m = (User) users.get(0);
			return m;
		} finally {
			close();
		}
	}

	//get all users for a given userAccountNumber - for multiple keywords per user
	public List<User> getUsers(String userAccountNumber) throws Exception {
		String sql = "from User as u where u.userAccountNumber = ?";
		try {
			session = HibernateUtil.currentSession();
			org.hibernate.query.Query q = session.createNativeQuery(sql);
			List<User> users = q.setParameter(0, userAccountNumber).list();
                        
			return users;
		} finally {
			close();
		}
	}
	
	public User login(String l, String p, Integer siteId) throws Exception {
		//String sql = "select * from user u where u.login = ? and u.password = md5(?) and site_id = ?";
		String sql = "from User as u where u.login = ? and u.password = md5(?) and siteId = ?";
		String l_sql = "from Login as u where u.username = ? and u.password = md5(?) and siteId = ?";
		try {
			session = HibernateUtil.currentSession();
			
			//check the Login first
			Query q = session.createNativeQuery(l_sql);
			List<Login> logins = q.setParameter(0, l).setParameter(1, p).setParameter(2, siteId).list();
			if (logins.isEmpty()) {
				return null;
			}
			
			return this.login(logins.get(0).getUserId());
			
			/*			
			//Query q = session.createNativeQuery(sql).addEntity("user", User.class);
			q = session.createNativeQuery(sql);
			List<User> users = q.setParameter(0, l).setParameter(1, p).setParameter(2, siteId).list();

			if (users.isEmpty()) {
				return null;
			}

			User user = (User) users.get(0);
			
			//add the "All" option
			TargetUserList allTul = new TargetUserList();
			allTul.setListId("All");
			allTul.setUserId(user.getUserId());
			allTul.setListName("All");
			user.getTargetUserLists().add(0, allTul);
			
			for (TargetUserList tul : user.getTargetUserLists())
				logger.debug("list_name, id: " + tul.getListName() + ", " + tul.getListId());
						
			return user;
			*/
		} finally {
			close();
		}
	}
	
	public User adminLogin(String l, String p, Integer siteId) throws Exception {
		String sql = "select u.* from user u, login l where l.username = ? and l.password = md5(?) and l.site_id = ?"
						+ " and l.user_id = u.user_id and u.user_type = 'A'";
		try {
			session = HibernateUtil.currentSession();
			
			Query q = session.createNativeQuery(sql).addEntity("user", User.class);

			List<User> users = q.setParameter(0, l)
								.setParameter(1, p)
								.setParameter(2, siteId)
								.list();
			
			if (users.isEmpty()) {
				return null;
			}
			
			return users.get(0);
		} finally {
			close();
		}
	}
	
	public Login uControlLogin(String l, String p) throws Exception {
		String sql = "select l.* from login l where l.username = ? and l.password = md5(?)";
		try {
			session = HibernateUtil.currentSession();
			
			Query q = session.createNativeQuery(sql).addEntity("user", User.class);

			List<Login> logins = q.setParameter(0, l)
								.setParameter(1, p)
								.list();
			
			if (logins.isEmpty()) {
				return null;
			}
			
			return logins.get(0);
		} finally {
			close();
		}
	}
	
	public User loginNetwork(String network, String networkId, Integer siteId) throws Exception {
		String l_sql = "from Login as u where u.network = ? and u.networkId = ? and site_id = ?";
		try {
			session = HibernateUtil.currentSession();
                            
			//check the Login first
			Query q = session.createNativeQuery(l_sql);
                      
			List<Login> logins = q.setParameter(0, network).setParameter(1, networkId).setParameter(2, siteId).list();
			if (logins.isEmpty()) {
				return null;
			}
			
			return this.login(logins.get(0).getUserId());
		} finally {
			close();
		}
	}
	
	/*
	//called from user registration in case there was an incomplete transaction - due to non-payment, etc.
	public User login(String l, String p, String keyword) throws Exception {
		String sql = "select * from user u where u.login = ? and u.password = md5(?) and keyword = ?";
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql).addEntity("user", User.class);

			List<User> users = q.setParameter(0, l).setParameter(1, p).setParameter(2, keyword).list();

			if (users.isEmpty()) {
				return null;
			}

			User m = (User) users.get(0);
			return m;
		} finally {
			close();
		}
	}
	*/
	
	public User login(long userId) throws Exception {
		return this.login(userId, 0);
	}
	
	//do not add the All list if called from a batch program
	public User login(long userId, int mode) throws Exception {
		//String sql = "select * from user u where u.user_id = ?";
		String sql = "from User where userId = ?";
		
		try {
			session = HibernateUtil.currentSession();
			/*
			Query q = session.createNativeQuery(sql).addEntity("user", User.class);

			List<User> users = q.setParameter(0, userId).list();
			*/
			
			Query q = session.createNativeQuery(sql);
			List<User> users = q.setParameter(0, userId).list();
			
			if (users.isEmpty()) {
				return null;
			}

			User user = (User) users.get(0);
			
			for (TargetUserList tuList : user.getTargetUserLists()) {
				logger.debug("tuList: " + tuList.getListName());
			}
			
			if (mode == 1)
				return user;
			
			//add the "All" option
			TargetUserList allTul = new TargetUserList();
			allTul.setListId("All");
			allTul.setUserId(user.getUserId());
			allTul.setListName("All");
			user.getTargetUserLists().add(0, allTul);
			
			return user;
		} finally {
			close();
		}
	}
	
	public UserProfile getUserProfile(Long userId, Integer categoryId) throws Exception {
		String sql = "from UserProfile as p where p.userId = ?";
		String csql = "select c.* from category_1 c"
					+ " where c.category_id = ?";
		
		Map<String, Object> fieldValues = new HashMap<String, Object>();
		UserProfile profile = null;
		
		Category category = new Category();
		category.setCategoryId(categoryId); 

		List<Field>fields = new ArrayList<Field>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			//Get the category
			ResultSet rs = dbs.getFromDB(csql, new Object[] {categoryId});
			while (rs.next()) {
				fields.add(new Field(rs.getInt("field_id"), rs.getString("field_name"), rs.getString("field_type"), 
								rs.getInt("sequence"), rs.getString("java_field")));
			}
			
			category.setFields(fields);
			
			//Get the profile
			Query q = session.createNativeQuery(sql);
			List<UserProfile> profiles = q.setParameter(0, userId).list();
			if (! profiles.isEmpty())
				profile = profiles.get(0);
			else {
				profile = new UserProfile();
				//create the fieldvalue map
				for (Field field : category.getFields()) {
					fieldValues.put(field.getFieldName(), null);
				}
				profile.setFieldValues(fieldValues);
			}
			
			profile.setUserId(userId);						
			profile.setCategory(category);
			logger.debug("login: profile id: " + profile.getProfileId());
			
			return profile;
		} finally {
			dbs.close();
			close();
		}
	}
	
	public void saveUser(User user) throws Exception {
		try {
			session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			session.saveOrUpdate(user);
			tx.commit();
		} finally {
			close();
		}
	}
	
	public void saveLogin(Login login) throws Exception {
		try {
			session = HibernateUtil.currentSession();

			//check if the login exists with the same username or the same userId
			String sql = "from Login as l where l.username = ? or l.userId = ?";
			Query q = session.createNativeQuery(sql);
			List<Login> logins = q.setParameter(0, login.getUsername())
									.setParameter(1, login.getUserId())
									.list();
			if (logins.size() > 0)
				throw new Exception("Login exists");
			
			tx = session.beginTransaction();
			session.saveOrUpdate(login);
			tx.commit();
		} finally {
			close();
		}
	}
	
	public void saveUserProfile(UserProfile profile) throws Exception {
		try {
	      	session = HibernateUtil.currentSession();

			tx = session.beginTransaction();
			/*
			if (profile.getProfileId() == null || profile.getProfileId() == 0)
				session.save(profile);
			else
				session.update(profile);
			*/
			session.saveOrUpdate(profile);
			tx.commit();
		} catch (Exception e) {
		   	if (tx != null) tx.rollback();
		   	e.printStackTrace();
			throw new Exception(e);
		} finally {
			session.flush();
			close();
		}
	}	
	
	public List<Field> getCategory(Integer categoryId) throws Exception {
		DBStuff dbs = null;
		String csql = "select c.* from category_1 c"
			+ " where c.category_id = ?";
		
		List<Field>fields = new ArrayList<Field>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(csql, new Object[] {categoryId});
			while (rs.next()) {
				fields.add(new Field(rs.getInt("field_id"), rs.getString("field_name"), rs.getString("field_type"), 
								rs.getInt("sequence"), rs.getString("java_field")));
			}
			return fields;
		} finally {
			dbs.close();
			close();
		}
	}
	
	public List getDetails(Long userId, String sql) throws Exception {
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql);
			List profiles = q.setParameter(0, userId).list();
				
			return profiles;
		} finally {
			close();
		}
	}
	
	public void saveDetails(Object obj) throws Exception {
		try {
	      	session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			session.saveOrUpdate(obj);
			tx.commit();
		} catch (Exception e) {
		   	if (tx != null) tx.rollback();
		   	e.printStackTrace();
			throw new Exception(e);
		} finally {
			//session.flush();
			close();
		}
	}
	
	//get a list of all the available categories
	public List<LabelValueBean> getCategories(int siteId) throws Exception {
		DBStuff dbs = null;
		String csql = "select c.category_id, c.name, c.sub_category_id from categories c where site_id = ? and is_visible = 1";
		
		List<LabelValueBean>categories = new ArrayList<LabelValueBean>();
		List<BusinessCategory> busCategories = new ArrayList<BusinessCategory>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(csql, new Object[] {siteId});
			while (rs.next()) {
				String subId = rs.getString("sub_category_id");
				if (subId == null) {
					subId = "0";
				}
				categories.add(new LabelValueBean(rs.getString("name"), rs.getString("category_id") 
										+ "<>" + subId));
			}
			return categories;
		} finally {
			dbs.close();
			close();
		}
	}

	//get only category Ids - no sub cat
	public List<LabelValueBean> getCategoriesOnly(int siteId) throws Exception {
		DBStuff dbs = null;
		String csql = "select c.category_id, c.name from categories c where site_id = ? and is_visible = 1";
		
		List<LabelValueBean>categories = new ArrayList<LabelValueBean>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(csql, new Object[] {siteId});
			while (rs.next()) {
				categories.add(new LabelValueBean(rs.getString("name"), rs.getString("category_id")));
			}
			return categories;
		} finally {
			dbs.close();
			close();
		}
	}
	
	//get the xslt files
	public String getXslFiles(Integer categoryId, Integer subCategoryId) throws Exception {
		String sql = "select filenames from xsl_files where category_id = ?";
		String sql1 = " and sub_category_id = ?";
		String sql2 = " and sub_category_id is null";
		Object[] params;
		String xsltFiles = null;
		try {
			if (subCategoryId == null) {
				params = new Object[] {categoryId};
				sql += sql2;
			} else {
				params = new Object[] {categoryId, subCategoryId};
				sql += sql1;
			}
			
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, params);
			while (rs.next()) {
				xsltFiles = rs.getString("filenames");
			}
			return xsltFiles;
		} finally {
			dbs.close();
			close();
		}		
	}
	
	//get the xslt files
	public String getXslFiles(Long userId) throws Exception {
		String sql = "select x.filenames from xsl_files x, user u where u.user_id = ?"
					+ " and u.category_id  = x.category_id"
					+ " and ifnull(u.sub_category_id,0)  = ifnull(x.sub_category_id,0)";
		String xsltFiles = null;
		try {		
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, new Object[] {userId});
			while (rs.next()) {
				xsltFiles = rs.getString("filenames");
			}
			return xsltFiles;
		} finally {
			dbs.close();
			close();
		}		
	}
	
	//check to see if any of the given keywords is available 
	//also check that the login is unqiue
	public String checkKeyword(String[] keywords, String login) throws Exception {
		session = HibernateUtil.currentSession();
		dbs = new DBStuff(HibernateUtil.getConnection(session));
		
		for (int i = 0; i < keywords.length; i++) {
			if (keywords[i] == null || keywords[i].length() <= 0 || keywords[i].equals(" "))
				continue;
			ResultSet rs = dbs.execSP("{call check_keyword_sp(?, ?)}", new Object[] {keywords[i], login});
			while (rs.next()) {
				int ret = rs.getInt(1);
				if (ret == 2) //login exists
					return "2";
				if (ret == 1) //keyword is available
					return keywords[i];
			}				
		}
		return "0"; //None of the keywords are available
	}
	
	//used to validate a user's email in case of forgotten password
	public String validUser(String login, String email) throws Exception {
		return this.validUser(login, email, 1); //default to US411
	}
	
	public String validUser(String login, String email, int siteId) throws Exception {
		DBStuff dbs = null;
		String sql = "select l.user_id from user u, login l"
			+ " where l.username = ? and l.user_id = u.user_id and u.email_id = ? and u.site_id = ?";
		String usql = "update login set password = md5(?) where user_id = ?";
		
		Long userId = null;
		String password = null;
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, new Object[] {login, email, siteId});
			while (rs.next()) {
				userId = rs.getLong("user_id");
			}
			
			//if valid, reset the password
			if (userId != null) {
				password = GeneratePassword.getRandomString();
				int ret = dbs.update(usql, new Object[] {password, userId});
			}
			
			return password;
		} finally {
			dbs.close();
			close();
		}
	}

	//get userId from mobilePhone - used by forgot username
	public String validUserByPhone(String mobilePhone, String email, int siteId, String keyword) throws Exception {
		DBStuff dbs = null;
		String sql = "select l.user_id, l.username from keyword_application k, user u, login l"
			+ " where k.mobile_phone = ? and u.user_id = l.user_id and u.email_id = ? and u.site_id = ?"
			+ " and k.keyword = u.keyword and k.keyword = ?";
		
		Long userId = null;
		String username = null;
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, new Object[] {mobilePhone, email, siteId, keyword});
			while (rs.next()) {
				userId = rs.getLong("user_id");
				username = rs.getString("username");
			}
			
			return username;
		} finally {
			dbs.close();
			close();
		}
	}

	
	public int resetPassword(String login, String email, String oldP, String newP) throws Exception {
		DBStuff dbs = null;
		//check to see if there is a profile. If so, use that email. This is to handle the case where a user may have changed
		//their email from the profile page
		String csql = "select p.email from profile p, login l where l.username = ? and l.password = md5(?) and l.user_id = p.user_id";
		String usql = "update login l, user u set l.password = md5(?) where l.username = ? and u.email_id = ? and l.password = md5(?)"
					+ " and u.user_id = l.user_id";
		String upsql = "update login l, user u, profile p set l.password = md5(?) where l.username = ? and p.email = ? and l.password = md5(?)"
					+ " and u.user_id = l.user_id and u.user_id = p.user_id";
		
		int ret = 0;
		String sql = upsql; //use the email from profile by default
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(csql, new Object[] {login, oldP});
			String profileEmail = null;
			while (rs.next()) {
				profileEmail = rs.getString("email");
			}

			if (profileEmail == null || profileEmail.length() <= 0)
				sql = usql; //if there is no profile, use the email from user
			
			ret = dbs.update(sql, new Object[] {newP, login, email, oldP});
			return ret;
		} finally {
			dbs.close();
			close();
		}
	}
	
	//get a list of all the available cuisines
	public List<LabelValueBean> getCuisine() throws Exception {
		DBStuff dbs = null;
		String csql = "select c.cuisine_id, c.name from cuisine c";
		
		List<LabelValueBean>categories = new ArrayList<LabelValueBean>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(csql);
			while (rs.next()) {
				categories.add(new LabelValueBean(rs.getString("name"), rs.getString("cuisine_id")));
			}
			return categories;
		} finally {
			dbs.close();
			close();
		}
	}
	
	//get list of values for hours
	public List<LabelValueBean> getHours() throws Exception {
		DBStuff dbs = null;
		String csql = "select id, hour from hours";
		
		List<LabelValueBean>hours = new ArrayList<LabelValueBean>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(csql);
			while (rs.next()) {
				hours.add(new LabelValueBean(rs.getString("hour"), rs.getString("hour")));
			}
			return hours;
		} finally {
			dbs.close();
			close();
		}
	}
	
	//get a list of all the available fonts
	public List<LabelValueBean> getFonts() throws Exception {
		DBStuff dbs = null;
		String csql = "select id, name from fonts";
		
		List<LabelValueBean>categories = new ArrayList<LabelValueBean>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(csql);
			while (rs.next()) {
				categories.add(new LabelValueBean(rs.getString("name"), rs.getString("id")));
			}
			return categories;
		} finally {
			dbs.close();
			close();
		}
	}
	
	//get a list of all the sites an admin user has
	public List<LabelValueBean> getSites(Long userId) throws Exception {
		DBStuff dbs = null;
		String csql = "select user_id, keyword from user where admin_profile_id = ?";
		
		List<LabelValueBean>sites = new ArrayList<LabelValueBean>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(csql, new Object[] {userId});
			while (rs.next()) {
				sites.add(new LabelValueBean(rs.getString("keyword"), rs.getString("user_id")));
			}
			return sites;
		} finally {
			dbs.close();
			close();
		}
	}

	//get a list of all the reports
	public List<Report> getReports(Long userId, int siteId) throws Exception {
		String sql = "select * from reports where site_id = ? order by is_multi, sort_order";
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql).addEntity("report", Report.class);

			List<Report> reports = q.setParameter(0, siteId).list();;

			if (reports.isEmpty()) {
				return null;
			}

			return reports;
		} finally {
			close();
		}
	}
	
	//get a list of all the states
	public List<LabelValueBean> getStates() throws Exception {
		DBStuff dbs = null;
		String csql = "select * from us_state_codes";
		
		List<LabelValueBean>sites = new ArrayList<LabelValueBean>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(csql, new Object[] {});
			while (rs.next()) {
				sites.add(new LabelValueBean(rs.getString("name"), rs.getString("code")));
			}
			return sites;
		} finally {
			dbs.close();
			close();
		}
	}
	
	//get a list of all the CA states
	public List<LabelValueBean> getCAStates() throws Exception {
		DBStuff dbs = null;
		String csql = "select * from ca_state_codes";
		
		List<LabelValueBean>sites = new ArrayList<LabelValueBean>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(csql, new Object[] {});
			while (rs.next()) {
				sites.add(new LabelValueBean(rs.getString("name"), rs.getString("code")));
			}
			return sites;
		} finally {
			dbs.close();
			close();
		}
	}

	//get all the mobile numbers in the target list for a user
	public List<String> getTargetList(Long userId) throws Exception {
		DBStuff dbs = null;
		String csql = "select mobile_phone from target_user_list tul, target_list_data tud"
					+ " where tul.list_id = tud.list_id and tul.user_id = ?";
		
		List<String>pNums = new ArrayList<String>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(csql, new Object[] {userId});
			while (rs.next()) {
				pNums.add(rs.getString("mobile_phone"));
			}
			return pNums;
		} finally {
			dbs.close();
			close();
		}
	}
	
	//get all the mobile numbers in the target list for a user with matching zip (for Delta)
	public List<String> getTargetList(Long userId, String zip) throws Exception {
		DBStuff dbs = null;
		String csql = "select mobile_phone from target_user_list tul, target_list_data tld"
					+ " where tul.list_id = tld.list_id and tul.user_id = ? and tld.field_1 = ?";
		
		List<String>pNums = new ArrayList<String>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(csql, new Object[] {userId, zip});
			while (rs.next()) {
				pNums.add(rs.getString("mobile_phone"));
			}
			return pNums;
		} finally {
			dbs.close();
			close();
		}
	}
	
	//save the FormPage values
	public void saveFormPage(Object mForm) throws Exception {
		try {
			session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			session.saveOrUpdate(mForm);
			tx.commit();
		} finally {
			close();
		}
	}
	
	//get the form page data fields
	public Map<String, Object> getFormPageData(Long profileId) throws Exception {
		String sql = "select fpd.*, fp.* from formpage fp left join formpage_data fpd  on fp.formpage_id = fpd.formpage_id where fp.profile_id = ?";
		DBStuff dbs = null;
		
		Map<String, Object>fpdFields = new HashMap<String, Object>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, new Object[] {profileId});
			while (rs.next()) {
				fpdFields.put("formPageId", rs.getString("formpage_id"));
				fpdFields.put("field1", rs.getString("field1_label"));
				fpdFields.put("field2", rs.getString("field2_label"));
				fpdFields.put("field3", rs.getString("field3_label"));
				fpdFields.put("field4", rs.getString("field4_label"));
				fpdFields.put("field5", rs.getString("field5_label"));
				fpdFields.put("field1Val", rs.getString("field1_value"));
				fpdFields.put("field2Val", rs.getString("field2_value"));
				fpdFields.put("field3Val", rs.getString("field3_value"));
				fpdFields.put("field4Val", rs.getString("field4_value"));
				fpdFields.put("field5Val", rs.getString("field5_value"));
			}
			return fpdFields;
		} finally {
			dbs.close();
			close();
		}
	}
	
	//create a campaign record for sending out messages
	public void saveCampaign(Campaign campaign) throws Exception {
		try {
			session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			session.saveOrUpdate(campaign);
			tx.commit();
		} finally {
			close();
		}		
	}
	
	public List<Campaign> getCampaign(Long userId) throws Exception {
		String sql = "from Campaign where userId = ?";
		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql);
			List<Campaign> campaigns = q.setParameter(0, userId)
											.list();
	
			if (campaigns.isEmpty()) {
				return null;
			}
	
			return campaigns;
		} finally {
			close();
		}			
	}
	
	public int deleteOffer(String offerId) throws Exception {
		String sql = "delete from Offer as o where o.offerId = ?";
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql);
			
			int ret = q.setParameter(0, offerId).executeUpdate();

			return ret;
		} finally {
			close();
		}
	}
	
	//get all the scheduled deliveries for a user
	public List<ReportData> getScheduledTriggers(Long userId) throws Exception {
		String sql = "SELECT trigger_name, next_fire_time, job_data, trigger_group, job_name"
					+ " FROM " + PropertyUtil.load().getProperty("quartz_db") + ".QRTZ_TRIGGERS qt"
					+ " where qt.trigger_group = ?";
		
		String orderBy = " order by next_fire_time"; 
		
		sql += orderBy;
		
		logger.debug("sql: " + sql);

		List<ReportData> reportDataList = new ArrayList<ReportData>();
		Calendar cal = Calendar.getInstance();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, new Object[] {userId});
			while (rs.next()) {
			    InputStream is = rs.getBlob("job_data").getBinaryStream();
			    ObjectInputStream ois = new ObjectInputStream(is);
			    Object obj = ois.readObject(); 
				JobDataMap jdmap = (JobDataMap) obj;
				Campaign camp = (Campaign) jdmap.get("campaign");
				cal.setTimeInMillis(Long.valueOf(rs.getString("next_fire_time")));
				reportDataList.add(new ReportData(rs.getString("trigger_name"), cal.getTime().toString(),
										camp.getName(), camp.getMessageText(), rs.getString("trigger_group"), rs.getString("job_name")));
			}
		} finally  {
			dbs.close();
			close();
		}
		return reportDataList;
	}	

	public List<Offer> getAllOffers(Long profileId) throws Exception {
		String sql = "select * from offers where profile_id = ?";
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, new Object[] {profileId});
			List<Offer> offers = new ArrayList<Offer>();

			while (rs.next()) {
				Offer offer = new Offer();
				offer.setName(rs.getString("name"));
				offer.setDescription(rs.getString("description"));
				offer.setCode(rs.getString("code"));
				offer.setExpiration(rs.getDate("expiration_date"));
				offers.add(offer);
			}

			return offers;
		} finally {
			close();
		}
	}
	
	//save errored message
	public void saveUS411Message(US411Message message) throws Exception {
		try {
			session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			session.saveOrUpdate(message);
			tx.commit();
		} finally {
			close();
		}		
	}
	
	//get all the keywords for a given user
	public List<String> getKeywords(String userId) throws Exception {
		String sql = "select u.keyword from user u, keyword_application kw"
					+ " where u.user_account_number = ?"
					+ " and u.keyword = kw.keyword";
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, new Object[] {userId});
			List<String> keywords = new ArrayList<String>();

			while (rs.next()) {
				keywords.add(rs.getString("keyword"));
			}

			return keywords;
		} finally {
			close();
		}
	}
	
	//get all the optouts for a given site
	public Map<String, String> getOptouts(int siteId) throws Exception {
		String sql = "select o.keyword, o.phone_number from opt_out o, keyword_application kw"
					+ " where kw.site_id = ?"
					+ " and o.keyword = kw.keyword";
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, new Object[] {siteId});
			Map<String, String> optouts = new HashMap<String, String>();

			while (rs.next()) {
				optouts.put(rs.getString("keyword"), rs.getString("phone_number"));
			}

			return optouts;
		} finally {
			close();
		}
	}
	
	public Map<String, String> getOptouts(int siteId, String dateFilter) throws Exception {
		String sql = "select o.keyword, o.phone_number from opt_out o, keyword_application kw"
					+ " where kw.site_id = ?"
					+ " and o.last_updated >= ?"
					+ " and o.keyword = kw.keyword";
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, new Object[] {siteId, dateFilter});
			Map<String, String> optouts = new HashMap<String, String>();

			while (rs.next()) {
				optouts.put(rs.getString("keyword"), rs.getString("phone_number"));
			}

			return optouts;
		} finally {
			close();
		}
	}
}
