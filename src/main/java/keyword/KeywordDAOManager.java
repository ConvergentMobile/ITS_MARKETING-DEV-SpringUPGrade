package keyword;

import java.io.Serializable;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mdp_common.DBStuff;

import org.apache.log4j.Logger;
import org.apache.struts.util.LabelValueBean;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import user.User;

import common.HibernateUtil;

public class KeywordDAOManager implements Serializable {
	private static final long serialVersionUID = 1L;

	Logger logger = Logger.getLogger(KeywordDAOManager.class);
	
	DBStuff dbs = null;
	Session session = null;
	Transaction tx = null;
	//MessageResources messages = MessageResources.getMessageResources("MessageResources");

	public void close() {
		try {
			HibernateUtil.closeSession();
			if (dbs != null)
				dbs.close();
		} catch (Exception e) {
			logger.error("Exception closing session: " + e.toString());
		}
	}

	public void save(KeywordApplication kwAppl) throws Exception {
		try {
			session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			session.saveOrUpdate(kwAppl);
			tx.commit();
		} finally {
			close();
		}
	}
	
	//Get a given keyword
	public KeywordApplication getKeyword(String email, String mobilePhone, String keyword) throws Exception {
		return this.getKeyword(email, mobilePhone, keyword, "US411");
	}

	public KeywordApplication getKeyword(String email, String mobilePhone, String keyword, String shortcode) throws Exception {
		//get only if the keyword has been purchased
		String sql = "from KeywordApplication where email = ? and mobile_phone = ? and keyword = ? and shortcode = ? and status = 'P'";
		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql);
			List<KeywordApplication> kwApps = q.setParameter(0, email)
											.setParameter(1, mobilePhone)
											.setParameter(2, keyword)
											.setParameter(3, shortcode)
											.list();
	
			if (kwApps.isEmpty()) {
				return null;
			}
	
			return kwApps.get(0); //should be only one
		} finally {
			close();
		}			
	}
	
	//get all keywords for a given email
	public List<User> getKeywordByEmail(String email, String shortcode, Integer siteId) throws Exception {
		//get only if the keyword has been purchased
		String sql = "from User as u, KeywordApplication as k where k.email = ? and k.shortcode = ? and k.status = 'P'"
					+ " and u.site_id = ? and u.keyword = k.keyword";
		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql);
			List<User> users = q.setParameter(0, email)
											.setParameter(1, shortcode)
											.setParameter(2, siteId)
											.list();
	
			return users;
		} finally {
			close();
		}			
	}

	//Get info for a given keyword
	public KeywordApplication getKeyword(Integer kwApplId) throws Exception {
		String sql = "from KeywordApplication where id = ?";
		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql);
			List<KeywordApplication> kwApps = q.setParameter(0, kwApplId).list();
	
			if (kwApps.isEmpty()) {
				return null;
			}
	
			return kwApps.get(0); //should be only one
		} finally {
			close();
		}			
	}
	
	//Get info for a given keyword
	public KeywordApplication getKeyword(String keyword, String shortcode) throws Exception {
		String sql = "from KeywordApplication where keyword = ? and shortcode in (:sclist)";
		
		if (shortcode.equals("US411") || shortcode.equals("87411"))
			shortcode = "US411" + "," + "87411";
		
		String[] scodes = shortcode.split(",");
		
		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql);
			List<KeywordApplication> kwApps = q.setParameter(0, keyword).setParameterList("sclist", Arrays.asList(scodes)).list();
	
			if (kwApps.isEmpty()) {
				return null;
			}
	
			return kwApps.get(0); //should be only one
		} finally {
			close();
		}			
	}
	
	//Get info for a given keyword given officeId or entityId
	public KeywordApplication getKeywordById(String eid, String mode) throws Exception {
		String sql = "select kw.* from keyword_application kw, user u, user_customer_defined ucd"
					+ " where kw.keyword = u.keyword and u.user_id = ucd.user_id and ucd.custom_field_2 = ?";

		if (mode.equals("Ent"))
			sql = "select kw.* from keyword_application kw, user u, user_customer_defined ucd"
					+ " where kw.keyword = u.keyword and u.user_id = ucd.user_id"
					+ " and (ucd.custom_field_4 = 'LIB_F' or ucd.custom_field_4 = 'LIB_AD')"
					+ " and ucd.custom_field_1 = ?";
		
		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql).addEntity("kw", KeywordApplication.class);
			List<KeywordApplication> kwApps = q.setParameter(0, eid).list();
	
			if (kwApps.isEmpty()) {
				return null;
			}
	
			return kwApps.get(0); //should be only one
		} finally {
			close();
		}			
	}
	
	//Get all the keywords for a given rep - get only the R and P 
	public List<KeywordApplication> getAllKeywords(String repName, String email, String mobilePhone, String shortcode) throws Exception {
		String sql = "from KeywordApplication where rep_email = ? and rep_mobile_phone = ? and rep_name = ?"
					+ " and status in ('R', 'P')"
					+ " and shortcode in (:sclist)"
					+ " order by customerField1";
		
		if (shortcode.equals("US411") || shortcode.equals("87411"))
			shortcode = "US411" + "," + "87411";
		
		String[] scodes = shortcode.split(",");

		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql);
			List<KeywordApplication> kwApps = q.setParameter(0, email)
											.setParameter(1, mobilePhone)
											.setParameter(2, repName)
											.setParameterList("sclist", Arrays.asList(scodes))											
											.list();
	
			if (kwApps.isEmpty()) {
				return null;
			}
	
			return kwApps;
		} finally {
			close();
		}			
	}

	//Get all the keywords for a given rep - get only the R and P 
	public List<KeywordApplication> getAllKeywords(String district, String shortcode) throws Exception {
		String sql = "from KeywordApplication where customerField4 = ?"
					+ " and status in ('R', 'P')"
					+ " and shortcode in (:sclist)"
					+ " order by customerField4, customerField1";
		
		if (shortcode.equals("US411") || shortcode.equals("87411"))
			shortcode = "US411" + "," + "87411";
		
		String[] scodes = shortcode.split(",");
		
		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql);
			List<KeywordApplication> kwApps = q.setParameter(0, district)
											.setParameterList("sclist", Arrays.asList(scodes))
											.list();
	
			if (kwApps.isEmpty()) {
				return null;
			}
	
			return kwApps;
		} finally {
			close();
		}			
	}
	
	//Get all the keywords for a given rep given a search string - get only the R and P 
	public List<KeywordApplication> getAllKeywords(String repName, String email, String mobilePhone, String shortcode, String searchString) throws Exception {
		String sql = "from KeywordApplication where repEmail = ? and repMobilePhone = ? and repName = ?"
					+ " and status in ('R', 'P')"
					+ " and (keyword like ? or businessName like ? or customerField1 like ? or customerField2 like ?)"
					+ " and shortcode in (:sclist)"
					+ " order by customerField1";
		
		String searchStr = "%" + searchString + "%";
		
		if (shortcode.equals("US411") || shortcode.equals("87411"))
			shortcode = "US411" + "," + "87411";
		
		String[] scodes = shortcode.split(",");
		
		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql);
			List<KeywordApplication> kwApps = q.setParameter(0, email)
											.setParameter(1, mobilePhone)
											.setParameterList("sclist", Arrays.asList(scodes))
											.setParameter(2, repName)
											.setParameter(3, searchStr)
											.setParameter(4, searchStr)
											.setParameter(5, searchStr)
											.setParameter(6, searchStr)
											.list();
	
			if (kwApps.isEmpty()) {
				return null;
			}
	
			return kwApps;
		} finally {
			close();
		}			
	}
	
	//Get all the Reserved keywords for a given account - N&N
	public List<KeywordApplication> getKeywordsByAccount(String accountNumber) throws Exception {
		String sql = "from KeywordApplication where customerField1 = ? and status = 'R'";
		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql);
			List<KeywordApplication> kwApps = q.setParameter(0, accountNumber).list();
	
			if (kwApps.isEmpty()) {
				return null;
			}
	
			return kwApps;
		} finally {
			close();
		}			
	}
	
	//check if the keyword is available
	/*
	public User checkKWAvail(String keyword, String login) throws Exception {
		String sql = "from User where keyword = ? and login = ?";
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql);
			List<User> users = q.setParameter(0, keyword).setParameter(1, login).list();
	
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

	//used by N&N - does not check for category keyword as they have their own shortcode
	public List<KeywordApplication> checkKWAvail(String keyword, String shortcode, String sql) throws Exception {
		return this.checkKWAvail(keyword, shortcode, sql, false);
	}

	//used for the US411 shortcode - check against the category keyword list
	public List<KeywordApplication> checkKWAvail(String keyword, String shortcode) throws Exception {
		String sql = "from KeywordApplication where keyword = ? and shortcode in (:sclist)";
		String scodes = shortcode;
		
		if (shortcode.equals("US411") || shortcode.equals("87411"))
			scodes = "US411" + "," + "87411";
		
		return this.checkKWAvail(keyword, scodes, sql, true);
	}
	
	public List<KeywordApplication> checkKWAvail(String keyword, String shortcode, String sql, boolean checkCategoryKw) throws Exception {
		String reservedSql = "select category_name from keyword_category where find_in_set(?, category_synonym) > 0";

		String[] scodes = shortcode.split(",");
		
		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql);
			//List<KeywordApplication> kwApps = q.setParameter(0, keyword).setParameter(1, shortcode).list();
			List<KeywordApplication> kwApps = q.setParameter(0, keyword).setParameterList("sclist", Arrays.asList(scodes)).list();

			if (! kwApps.isEmpty()) { //keyword exists
				return kwApps;
			}
			
			if (! checkCategoryKw && kwApps.isEmpty()) {
				return null;
			}
	
			//check if it is a category keyword
			q = session.createNativeQuery(reservedSql);
			List<String> catKws = q.setParameter(0, keyword).list();
			
			if (catKws.isEmpty())
				return null;
			
			KeywordApplication kwAppl = new KeywordApplication();
			kwAppl.setKeyword(catKws.get(0));
			
			kwApps.add(kwAppl);
			
			return kwApps;
		} finally {
			close();
		}			
	}
	
	//not used
	public List<KeywordApplication> checkKWAvail(String keyword, String shortcode, Integer siteId, String sql) throws Exception {
		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql);
			List<KeywordApplication> kwApps = q.setParameter(0, keyword)
												.setParameter(1, shortcode)
												.setParameter(2, siteId)
												.list();
	
			if (kwApps.isEmpty()) {
				return null;
			}
	
			return kwApps;
		} finally {
			close();
		}			
	}
	
	//check that the login for that site is unique
	public boolean checkUniqueLogin(Integer siteId, String login) throws Exception {
		String sql = "select user_id from user where login = ? and site_id = ?";
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, new Object[] {login, siteId});
			while (rs.next()) {
				return false;
			}
			return true;
		} finally {
			close();
		}			
	}
	
	public void reserve(List<KeywordApplication> kwApps) throws Exception {
		try {
			session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			for (KeywordApplication kwApp : kwApps)
				session.saveOrUpdate(kwApp);
			tx.commit();
		} finally {
			close();
		}	
	}
	
	public void deleteKeyword(KeywordApplication kwApp) throws Exception {	
		try {
			session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			session.delete(kwApp);
			tx.commit();
		} finally {
			close();
		}
	}
	
	//get list of values for homeMarket - used by N&N
	public List<LabelValueBean> getHomeMarket() throws Exception {
		DBStuff dbs = null;
		String csql = "select home_market_id, name from home_market";
		
		List<LabelValueBean> homeMarkets = new ArrayList<LabelValueBean>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(csql);
			while (rs.next()) {
				homeMarkets.add(new LabelValueBean(rs.getString("name"), rs.getString("home_market_id")));
			}
			return homeMarkets;
		} finally {
			dbs.close();
			close();
		}
	}
	
	//get list of values for accountNumber (customerField1) - used by N&N
	public List<LabelValueBean> getAcountNumbers() throws Exception {
		DBStuff dbs = null;
		String csql = "select distinct customer_field_1 from keyword_application where shortcode = '5STAR'";
		
		List<LabelValueBean> accountNumbers = new ArrayList<LabelValueBean>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(csql);
			while (rs.next()) {
				accountNumbers.add(new LabelValueBean(rs.getString("customer_field_1"), rs.getString("customer_field_1")));
			}
			return accountNumbers;
		} finally {
			dbs.close();
			close();
		}
	}
	
	public Promotion getPromotion(String promoCode) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String todayDate = sdf.format(Calendar.getInstance().getTime());
		
		String sql = "from Promotion where promoCode = ? and (endDate is null or endDate >= ?)";
		try {		
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql);
			List<Promotion> promos = q.setParameter(0, promoCode)
									   .setParameter(1, todayDate).list();
	
			if (promos == null || promos.isEmpty()) {
				return null;
			}
	
			return promos.get(0); //should be only one
		} finally {
			close();
		}			
	}
	
	//for password protecting the USP keyword purchase page
	public boolean uspPurchaseLogin(String login, String password) throws Exception {
		if (login.equals("usp_purchasing") && password.equals("usp_admin"))
				return true;
		
		return false;
	}

    //for password protecting the NN keyword purchase page
    public boolean nnPurchaseLogin(String login, String password) throws Exception {
            if (login.equals("5starmobile") && password.equals("5star2299"))
            	return true;

            return false;
    }

	//get the hotspot files for a rep
	public List<KeywordApplication> getHotspotFile(String accountNumber, Integer siteId) throws Exception {
		DBStuff dbs = null;
		String csql = "SELECT u.hotspot_file, k.*"
					+ " from user u, keyword_application k"
					+ " where k.keyword = u.keyword"
					+ " and u.site_id = ?"
					+ " and k.customer_field_1 = ?"; // this is the accountNumber
		
		List<KeywordApplication> kwList = new ArrayList<KeywordApplication>();
		ResultSet rs = null;
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			rs = dbs.getFromDB(csql, new Object[] {siteId, accountNumber});
			while (rs.next()) {
				KeywordApplication kw = new KeywordApplication();
				kw.setRepName(rs.getString("rep_name"));
				kw.setCustomerField1(accountNumber);
				kw.setCustomerField2(rs.getString("customer_field_2"));
				kw.setCustomerField3(rs.getString("hotspot_file"));
				kw.setKeyword(rs.getString("keyword"));
				kw.setBusinessName(rs.getString("business_name"));
				kwList.add(kw);
			}
			
			return kwList;
		} finally {
			rs.close();
			dbs.close();
			close();
		}		
	}
	
	//get list of the business categories
	public List<LabelValueBean> getBusCategories() throws Exception {
		DBStuff dbs = null;
		String csql = "select business_category_id, category_name from business_category";
		
		List<LabelValueBean> busCategories = new ArrayList<LabelValueBean>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(csql);
			while (rs.next()) {
				busCategories.add(new LabelValueBean(rs.getString("category_name"), rs.getString("business_category_id")));
			}
			return busCategories;
		} finally {
			dbs.close();
			close();
		}
	}
	
	//get list of the business categories
	public List<LabelValueBean> getBusCategories(String searchString) throws Exception {
		DBStuff dbs = null;
		String csql = "select business_category_id, category_name from business_category where category_name like ?";
		
		List<LabelValueBean> busCategories = new ArrayList<LabelValueBean>();
		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(csql, new Object[]{searchString + "%"});
			while (rs.next()) {
				busCategories.add(new LabelValueBean(rs.getString("category_name"), rs.getString("business_category_id")));
			}
			return busCategories;
		} finally {
			dbs.close();
			close();
		}
	}
}