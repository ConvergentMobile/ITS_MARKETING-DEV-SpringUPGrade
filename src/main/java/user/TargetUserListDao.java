package user;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import mdp_common.DBStuff;

import org.apache.log4j.Logger;
import org.apache.struts.util.MessageResources;
import org.hibernate.Hibernate;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

import common.HibernateUtil;

public class TargetUserListDao implements Serializable {
	private static final long serialVersionUID = 1L;

	Logger logger = Logger.getLogger(TargetUserListDao.class);
	
	DBStuff dbs = null;
	Session session = null;
	Transaction tx = null;
	MessageResources messages = MessageResources.getMessageResources("MessageResources");
	
	public void close() {
		try {
			HibernateUtil.closeSession();
		} catch (Exception e) {
			logger.error("Exception closing session: " + e.toString());
		}
	}
	
	public List<TargetUserList> get(Long userId) throws Exception {
		String sql = "from TargetUserList as u where u.userId = ?";
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql);
			
			List<TargetUserList> lists = q.setParameter(0, userId).list();

			if (lists.isEmpty()) {
				return null;
			}

			//add the "All" option
			TargetUserList allTul = new TargetUserList();
			allTul.setListId("All");
			allTul.setUserId(userId);
			allTul.setListName("All");
			lists.add(0, allTul);
			
			return lists;
		} finally {
			close();
		}
	}

	/* Not used - replaced by the one below
	public List<TargetListData> getListData(String listId) throws Exception {
		String sql = "from TargetListData as u where u.listId = ?";
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql);
			
			List<TargetListData> listData = q.setParameter(0, listId).list();

			if (listData.isEmpty()) {
				return null;
			}

			return listData;
		} finally {
			close();
		}
	}
	*/
	
	//return the default list data as TargetListData
	public List<TargetListData> getDefaultTargetListData(Long userId) throws Exception {
		String sql = "select tld.* from target_list_data tld, target_user_list tul, user u"
					+ " where u.user_id = ?"
					+ " and u.user_id = tul.user_id"
					+ " and tul.list_id = tld.list_id"
					+ " and tul.list_name = u.keyword";

		try {
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql).addEntity("tld", TargetListData.class);
			
			List<TargetListData> lists = q.setParameter(0, userId).list();			

			return lists;
		} finally {
			close();
		}
	}
	
	//return the list data as TargetListData
	public List<TargetListData> getTargetListData(String listId) throws Exception {
		String sql = "from TargetListData tld where tld.listId = ? order by tld.lastUpdated";

		try {
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql);
			
			List<TargetListData> lists = q.setParameter(0, listId).list();			

			return lists;
		} finally {
			close();
		}
	}
	
	//return the list data as TargetListData - paged
	public List<TargetListData> getTargetListDataPaged(List<String> listIds, Integer numRecords, Integer offset) throws Exception {
		String sql = "select * from target_list_data tld where tld.list_id in (:ids) group by tld.mobile_phone order by tld.last_updated";
		if (numRecords != null)
			sql += " limit " + offset + ", " + numRecords;

		try {
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql).addEntity("tld", TargetListData.class);
			
			List<TargetListData> lists = q.setParameterList("ids", listIds.toArray()).list();			

			return lists;
		} finally {
			close();
		}
	}
	
	//return the list data as TargetListData
	public List<TargetListData> getTargetListData(String listId, String sortCol, String order) throws Exception {
		String sql = "from TargetListData where listId = ?";

		if (sortCol == null)
			sortCol = "lastUpdated";
		if (order == null)
			order = "desc";
		
		sql += " order by " + sortCol + " " + order;
		
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql);
			
			List<TargetListData> lists = q.setParameter(0, listId).list();			

			return lists;
		} finally {
			close();
		}
	}
	
	//return a list of just the phone numbers
	public List<String> getListData(String listId, Long userId) throws Exception {
		String sql = "select mobile_phone from target_list_data tld where tld.list_id = ? order by mobile_phone";
		String allSql = "select distinct mobile_phone from target_list_data tld, target_user_list tul"
						+ " where tld.list_id = tul.list_id and tul.user_id = ? order by mobile_phone";
		try {
			session = HibernateUtil.currentSession();
			Query q;
			if (listId.equals("All")) {
				q = session.createNativeQuery(allSql).setParameter(0, userId);
			} else
				q = session.createNativeQuery(sql).setParameter(0, listId);
			
			List<String> listData = q.list();

			return listData;
		} finally {
			close();
		}
	}
	
	public List<String> getListData(String listId) throws Exception {
		return getListData(listId, null); 
	}
	
	
	//delete a list
	public int deleteList(String listId) throws Exception {
		String sql_d = "delete from target_list_data where list_id = ?";
		String sql = "delete from target_user_list where list_id = ?";
		int numDeleted = 0;

		logger.debug("In deleteList");
		try {
			session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			numDeleted = session.createNativeQuery(sql_d)
						.setParameter(0, listId)
						.executeUpdate();
			numDeleted = session.createNativeQuery(sql)
	        			.setParameter(0, listId)
	        			.executeUpdate();
			tx.commit();
			return numDeleted;
		} finally {
			close();
		}
	}

	public void saveListData(List<TargetListData> tListData) throws Exception {
		try {
	      	session = HibernateUtil.currentSession();

			tx = session.beginTransaction();

			for (TargetListData item : tListData) {
				if (item == null || item.getMobilePhone() == null)
					continue;
				logger.debug("saveListData - listId, pNum = " + item.getListId() + ", " + item.getMobilePhone());
				try {
					session.save(item);
				} catch (NonUniqueObjectException nuex) {
					logger.debug("Got duplicate number. Ignoring ...");
				}
			}

			tx.commit();
			session.flush();			
		} finally {
			close();
		}
	}
	
	public int deleteNumber(String listId, String mobilePhone) throws Exception {
		String sql = "delete from TargetListData as u where u.listId = ? and u.mobilePhone = ?";
		try {
			session = HibernateUtil.currentSession();
			Query q = session.createNativeQuery(sql);
			
			int ret = q.setParameter(0, listId)
						.setParameter(1, mobilePhone).executeUpdate();

			return ret;
		} finally {
			close();
		}
	}
	
	//delete from all lists - when delete number is selected with All
	public int deleteNumber(Long userId, String mobilePhone) throws Exception {
		int ret = 0;
		try {
			List<TargetUserList> lists = this.get(userId);
			for (TargetUserList list : lists) {
				if (list.getListId().equals("All"))
					continue;
				ret += this.deleteNumber(list.getListId(), mobilePhone);
			}
			return ret;
		} finally {
			close();
		}
	}
	
	public void save(TargetUserList tuList) throws Exception {
		try {
			session = HibernateUtil.currentSession();
			tx = session.beginTransaction();
			session.saveOrUpdate(tuList);
			tx.commit();
		} finally {
			close();
		}
	}
}
