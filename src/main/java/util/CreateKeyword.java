package util;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import keyword.KeywordApplication;
import keyword.KeywordDAOManager;
import mdp_common.DBStuff;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import common.HibernateUtil;

import sms.SMSDelivery;
import user.Login;
import user.TargetUserList;
import user.User;
import user.UserDAOManager;
import category_3.Category_3;

//utility class to create a create keyword and associated user & login
public class CreateKeyword {
	private static Logger logger = Logger.getLogger(CreateKeyword.class);
	protected static final int CATEGORY_ID = 3;
	protected UserDAOManager userDAO =  new UserDAOManager();

	public CreateKeyword() {
		
	}
	
	private String getShortCode(int siteId) throws Exception {
		Session session = null;
		DBStuff dbs = null;
		String sql = "select shortcode from site where site_id = ?";
		
		String shortcode = null;

		try {
			session = HibernateUtil.currentSession();
			dbs = new DBStuff(HibernateUtil.getConnection(session));
			ResultSet rs = dbs.getFromDB(sql, new Object[] {siteId});
			while (rs.next()) {
				shortcode = rs.getString("shortcode");
			}
			
			return shortcode;
		} finally {
			dbs.close();
		}
	}
	
	public Long createIt(int siteId, String keyword, String userEmail, String mobilePhone,
			 String businessName) throws Exception {
		String shortcode = getShortCode(siteId);
		
		return this.createIt(siteId, shortcode, keyword, userEmail, mobilePhone, businessName, null, null, "P");
	}
	
	public Long createIt(int siteId, String keyword, String userEmail, String mobilePhone,
			 String businessName, String fName, String lName) throws Exception {
		String shortcode = getShortCode(siteId);
		
		return this.createIt(siteId, shortcode, keyword, userEmail, mobilePhone, businessName, fName, lName, "P");
	}
	
	public Long createIt(int siteId, String keyword, String userEmail, String mobilePhone,
			 String businessName, String fName, String lName, String status) throws Exception {
		String shortcode = getShortCode(siteId);
		
		return this.createIt(siteId, shortcode, keyword, userEmail, mobilePhone, businessName, fName, lName, status);
	}
	
	public Long createIt(int siteId, String shortcode, String keyword, String userEmail, String mobilePhone,
			 String businessName) throws Exception {
		return this.createIt(siteId, shortcode, keyword, userEmail, mobilePhone, businessName, null, null, "P");
	}
	
	public Long createIt(KeywordApplication kw, String fName, String lName) throws Exception {
		String shortcode = getShortCode(kw.getSiteId());
		String keyword = kw.getKeyword();

		KeywordDAOManager kwDAO = new KeywordDAOManager();		
		
		KeywordApplication kwAppl = new KeywordApplication();

		//check if the Keyword exists, else create keyword
		List<KeywordApplication> kwApps = kwDAO.checkKWAvail(keyword, shortcode);
		if (kwApps != null) {
			logger.error("Keyword - " + keyword + " exists");
			kwAppl = kwApps.get(0); //there should be only one
		}
		
		kwAppl.setEmail(kw.getEmail());
		if (kw.getMobilePhone() != null) {
			String mobilePhone = new SMSDelivery().normalizePhoneNumber(kw.getMobilePhone());
			kwAppl.setMobilePhone(mobilePhone);
		}
		kwAppl.setKeyword(keyword);
		kwAppl.setCategoryId(CATEGORY_ID);
		kwAppl.setStatus(kw.getStatus());
		kwAppl.setRepName("SYSTEM");
		kwAppl.setAcceptTerms(true);
		kwAppl.setBusinessName(kw.getBusinessName());
		kwAppl.setSiteId(kw.getSiteId());
		kwAppl.setShortcode(shortcode);
		
		kwDAO.save(kwAppl);
		logger.debug("saved keyword - " + keyword);
				
		Long userId = null;
		//check if a User exists, else create one
		User user = userDAO.login(keyword, kw.getSiteId()); 
		if (user == null) {
			userId = createUser(kw.getSiteId(), keyword, kw.getEmail(), fName, lName);
		} else { 
			logger.debug("User exists");
			user.setEmail(kw.getEmail());
			user.setFirstName(fName);
			user.setLastName(lName);
			userDAO.saveUser(user);
			userId = user.getUserId();
		}
		
		logger.debug("userId: " + userId);

		//check if a Login exists, else create one
		if (userDAO.login(keyword.toLowerCase(), keyword.toLowerCase() + "_password", kw.getSiteId()) == null) {
		// check for a login based on the userId to handle the case of stores changing ownership
		// if (userDAO.login(userId, 1) == null) {
			Login login = new Login(keyword.toLowerCase(), keyword.toLowerCase() + "_password", userId, kw.getSiteId());
			userDAO.saveLogin(login);
		}
		
		return userId;
	}
	
	private Long createIt(int siteId, String shortcode, String keyword, String userEmail, String mobilePhone,
						 String businessName, String fName, String lName, String status) throws Exception {
		
		KeywordApplication kw = new KeywordApplication();
		kw.setSiteId(siteId);
		kw.setShortcode(shortcode);
		kw.setEmail(userEmail);
		kw.setMobilePhone(mobilePhone);
		kw.setKeyword(keyword);
		kw.setStatus(status);
		kw.setBusinessName(businessName);
		
		return createIt(kw, fName, lName);
	}
	
	public Long createUser(int siteId, String keyword, String userEmail, String fName, String lName) throws Exception {
		User user = new User();
		user.setEmail(userEmail);
		user.setCategoryId(CATEGORY_ID);
		user.setKeyword(keyword);
		user.setSiteId(siteId);
		user.setFirstName(fName);
		user.setLastName(lName);
		
		//default list
		List<TargetUserList> tuLists = new ArrayList<TargetUserList>();
		TargetUserList tuList = new TargetUserList();
		tuList.setListId(UUID.randomUUID().toString());
		tuList.setListName(user.getKeyword());
		tuLists.add(tuList);
		user.setTargetUserLists(tuLists);
		
		userDAO.saveUser(user);
		return user.getUserId();
	}
	
	public void createProfile(Long userId, Map<String, String> catgFields) throws Exception {
		String sql = "from Category_3 as p where p.userId = ?";
				
		Category_3 category = new Category_3();
		
		List<Category_3> categoryList = userDAO.getDetails(userId, sql);
		if (categoryList != null && categoryList.size() > 0)
			category = categoryList.get(0);
		
		category.setUserId(userId);
		category.setCategoryId(CATEGORY_ID); 
		
		if (catgFields.get("businessName") != null)
			category.setBusinessName(catgFields.get("businessName"));
		if (catgFields.get("address") != null)
			category.setAddress(catgFields.get("address"));
		if (catgFields.get("city") != null)
			category.setCity(catgFields.get("city"));
		if (catgFields.get("state") != null)
			category.setState(catgFields.get("state"));
		if (catgFields.get("zip") != null)
			category.setZip(catgFields.get("zip"));
		if (catgFields.get("phone") != null)
			category.setPhone(catgFields.get("phone"));
		if (catgFields.get("email") != null)
			category.setEmail(catgFields.get("email"));
		if (catgFields.get("busHours") != null)
			category.setBusHours(catgFields.get("busHours"));
		if (catgFields.get("timezone") != null)
			category.setTimezone(catgFields.get("timezone"));
		if (catgFields.get("description") != null)
			category.setDescription(catgFields.get("description"));
		if (catgFields.get("busMobilePhone") != null)
			category.setAdminMobilePhone(catgFields.get("busMobilePhone"));
		if (catgFields.get("initialMessage") != null)
			category.setInitialMessage(catgFields.get("initialMessage"));
		if (catgFields.get("autoResponse") != null)
			category.setAutoResponse(catgFields.get("autoResponse"));
		
		userDAO.saveDetails(category);
	}
	
	//used by knowme for a user getting additional keywords
	public void copyProfile(Long fromUserId, Long toUserId) throws Exception {
		String sql = "from Category_3 as p where p.userId = ?";
				
		Category_3 categoryTo = new Category_3();
		Category_3 categoryFrom = null;

		List<Category_3> categoryList = userDAO.getDetails(fromUserId, sql);
		if (categoryList != null && categoryList.size() > 0)
			categoryFrom = categoryList.get(0);
		
		categoryTo.setUserId(toUserId);
		categoryTo.setCategoryId(CATEGORY_ID); 
		
		if (categoryFrom.getBusinessName() != null)
			categoryTo.setBusinessName(categoryFrom.getBusinessName());
		if (categoryFrom.getAddress() != null)
			categoryTo.setAddress(categoryFrom.getAddress());
		if (categoryFrom.getCity() != null)
			categoryTo.setCity(categoryFrom.getCity());
		if (categoryFrom.getState() != null)
			categoryTo.setState(categoryFrom.getState());
		if (categoryFrom.getZip() != null)
			categoryTo.setZip(categoryFrom.getZip());
		if (categoryFrom.getPhone() != null)
			categoryTo.setPhone(categoryFrom.getPhone());
		if (categoryFrom.getEmail() != null)
			categoryTo.setEmail(categoryFrom.getEmail());
		if (categoryFrom.getBusHours() != null)
			categoryTo.setBusHours(categoryFrom.getBusHours());
		if (categoryFrom.getTimezone() != null)
			categoryTo.setTimezone(categoryFrom.getTimezone());
		if (categoryFrom.getDescription() != null)
			categoryTo.setDescription(categoryFrom.getDescription());
		if (categoryFrom.getAdminMobilePhone() != null)
			categoryTo.setAdminMobilePhone(categoryFrom.getAdminMobilePhone());
		
		userDAO.saveDetails(categoryTo);
	}
}
