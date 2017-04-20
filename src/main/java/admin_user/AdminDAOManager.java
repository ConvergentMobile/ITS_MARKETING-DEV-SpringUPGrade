package admin_user;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mdp_common.DBStuff;

import org.apache.log4j.Logger;
import org.apache.struts.util.MessageResources;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import user.Category;
import user.Field;
import user.UserProfile;

import common.HibernateUtil;

public class AdminDAOManager implements Serializable {
	private static final long serialVersionUID = 1L;

	Logger logger = Logger.getLogger(AdminDAOManager.class);
	
	DBStuff dbs = null;
	Session session = null;
	Transaction tx = null;
	MessageResources messages = MessageResources.getMessageResources("MessageResources");
	
	public void close() {
		try {
			HibernateUtil.closeSession();
			if (dbs != null)
				dbs.close();
		} catch (Exception e) {
			logger.error("Exception closing session: " + e.toString());
		}
	}
	
	//get all the sites for this admin given a starting letter
	public List<UserProfileVO> getSites(Integer siteId, String letter) throws Exception {
		/*
		String sql = "select p.*, u.keyword from admin_sites a, profile p, user u where a.admin_id = ?"
					+ " and p.profile_id = a.profile_id"
					+ " and u.user_id = p.profile_id";
		*/
		/*
		String sql = "select u.keyword, u.user_id uid, p.* from user u"
					+ " left join profile p on (u.user_id = p.user_id)"
					+ " where u.admin_profile_id = ?";
		*/

		String sql = "select u.keyword, u.user_id uid, p.* from user u"
				+ " left join profile p on (u.user_id = p.user_id)"
				+ " where u.site_id = ?"
				+ " and u.keyword like ?";
		
		if (letter.equals("All"))
			return getAllSites(siteId);
		
		List<UserProfileVO> sites = new ArrayList<UserProfileVO>();		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, new Object[] {siteId, letter+"%"});
			while (rs.next()) {
				//use the user_id from user in case there is no profile
				sites.add(new UserProfileVO(rs.getLong("profile_id"), rs.getLong("uid"), siteId, rs.getString("keyword"),
								rs.getString("business_name")));
			}
			
			logger.debug("getSites: size = " + sites.size());
			return sites;
		} finally {
			close();
		}
	}
	
	//get all the sites for this admin
	public List<UserProfileVO> getAllSites(Integer siteId) throws Exception {
		String sql = "select u.keyword, u.user_id uid, p.* from user u"
				+ " left join profile p on (u.user_id = p.user_id)"
				+ " where u.site_id = ?";
		
		List<UserProfileVO> sites = new ArrayList<UserProfileVO>();		
		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, new Object[] {siteId});
			while (rs.next()) {
				//use the user_id from user in case there is no profile
				sites.add(new UserProfileVO(rs.getLong("profile_id"), rs.getLong("uid"), siteId, rs.getString("keyword"),
								rs.getString("business_name")));
			}
			
			logger.debug("getSites: size = " + sites.size());
			return sites;
		} finally {
			close();
		}
	}
}
