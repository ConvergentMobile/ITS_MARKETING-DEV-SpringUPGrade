package user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Queue;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.util.LabelValueBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import category_1.Event;
import category_1.MenuSection;
import jms.JMSProducer;

public class CategoryBase implements Serializable {
	protected static final long serialVersionUID = 1L;

	protected static final Logger logger = Logger.getLogger(CategoryBase.class);

	protected String categoryName;
	protected Integer categoryId;	
	protected Long userId;
	protected Long profileId;
	protected List<Field> fields;
	protected String initialMessage;  //text msg a user receives upon texting in
	protected String autoResponse;	//auto response text
	protected String busHours; //can be multiple lines e.g M-F 9 - 9, Sat 10-10, Sun 10-8
	protected String businessName;
	protected String zip;
	protected String address;
	protected String city;
	protected String state;
	protected String phone;
	protected String xmlFile;
	protected String previewFile; //file to display on preview
	protected String mwpPath; //path to the first web page
	protected String website;
	protected String custStatus = "A"; //Active by default
	protected String offers;
	protected String email;
	protected int repeatingLogo = 0; //default is no
	protected String timezone;
	protected String keyword;
	protected String stdRepeatNotificationMsg;
	protected String externalLink_1; //link to user-defined mobile web pages for first time MO
	protected String externalLink_2; //link to user-defined mobile web pages for subsequent MOs
	
	protected String adminMobilePhone; //mobile phone # of bus admin to send sms notifications to

	protected List<Offer> offerList = new ArrayList<Offer>();
	protected String[] xslTransformFiles;
	
	protected UserDAOManager dao = new UserDAOManager();

	protected JMSProducer producer;
	protected static final String APPLICATION_CONTEXT_FILE = "classpath:application_context.xml";
	
	protected int MAX_OFFERS = 1;
	
	public CategoryBase() {
		
	}
	
	public CategoryBase(Long userId, Integer categoryId) {
		this.userId = userId;
		this.categoryId = categoryId;
	}
	
	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Long getProfileId() {
		return profileId;
	}

	public void setProfileId(Long profileId) {
		this.profileId = profileId;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	
	public String getInitialMessage() {
		return initialMessage;
	}

	public void setInitialMessage(String initialMessage) {
		this.initialMessage = initialMessage;
	}

	public String getAutoResponse() {
		return autoResponse;
	}

	public void setAutoResponse(String autoResponse) {
		this.autoResponse = autoResponse;
	}

	public String getBusHours() {
		return busHours;
	}

	public void setBusHours(String busHours) {
		this.busHours = busHours;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getAddress() {
		return address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getXmlFile() {
		return xmlFile;
	}

	public void setXmlFile(String xmlFile) {
		this.xmlFile = xmlFile;
	}

	public String getPreviewFile() {
		return previewFile;
	}

	public void setPreviewFile(String previewFile) {
		this.previewFile = previewFile;
	}

	public String getMwpPath() {
		return mwpPath;
	}

	public void setMwpPath(String mwpPath) {
		this.mwpPath = mwpPath;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getCustStatus() {
		return custStatus;
	}

	public void setCustStatus(String custStatus) {
		this.custStatus = custStatus;
	}

	public String getOffers() {
		return offers;
	}

	public void setOffers(String offers) {
		this.offers = offers;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getAdminMobilePhone() {
		return adminMobilePhone;
	}

	public void setAdminMobilePhone(String adminMobilePhone) {
		this.adminMobilePhone = adminMobilePhone;
	}

	public int getRepeatingLogo() {
		return repeatingLogo;
	}

	public void setRepeatingLogo(int repeatingLogo) {
		this.repeatingLogo = repeatingLogo;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public List<Offer> getOfferList() {
		return offerList;
	}

	public int getActiveOfferListSize() {
		int i = 0;
		for (Offer o : this.offerList)
			if (o.getStatus().equals("A"))
				i++;
		
		return i;
	}
	
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public void setOfferList(List<Offer> offerList) {
		this.offerList = offerList;
	}

	public String[] getXslTransformFiles() {
		return xslTransformFiles;
	}

	public void setXslTransformFiles(String[] xslTransformFiles) {
		this.xslTransformFiles = xslTransformFiles;
	}

	public String getStdRepeatNotificationMsg() {
		return stdRepeatNotificationMsg;
	}

	public void setStdRepeatNotificationMsg(String stdRepeatNotificationMsg) {
		this.stdRepeatNotificationMsg = stdRepeatNotificationMsg;
	}

	public String getExternalLink_1() {
		return externalLink_1;
	}

	public void setExternalLink_1(String externalLink_1) {
		this.externalLink_1 = externalLink_1;
	}

	public String getExternalLink_2() {
		return externalLink_2;
	}

	public void setExternalLink_2(String externalLink_2) {
		this.externalLink_2 = externalLink_2;
	}

	public String getAddressForDisplay() {
		StringBuffer sb = new StringBuffer();
		String sep = "";
		if (this.getAddress() != null) {
			sep = ", ";			
			sb.append(this.getAddress()).append(sep);
		}
		if (this.getCity() != null) {
				sep = ", ";			
				sb.append(this.getCity()).append(sep);
		} else
			sep = "";
		if (this.getState() != null)
			sb.append(this.getState()).append(" ");
		
		sb.append(this.getZip());
		
		return sb.toString();
	}
	
	/*
	//This is not really needed - 3/8/2012
	public void getCategory() throws Exception {		
		List<Field> fields = dao.getCategory(categoryId);
		if (! fields.isEmpty())
			this.setFields(fields);		
	}
	*/
	
	public void getCategory() throws Exception {
		
	}
	
	public ActionErrors validate(CategoryForm cForm, HttpServletRequest request) throws Exception {
			return null;		
	}
	
	public void save(HttpServletRequest request) throws Exception {
		throw new Exception("Base class save - not implemented");
	}
	
	public void save(HttpServletRequest request, String currPage) throws Exception {
		throw new Exception("Base class save (currPage) - not implemented");
	}
	
	//Not used at present - figure out how
	/*
	public void get(CategoryForm cForm) throws Exception {
		if (categoryId == 1) {
			Category_1 category = new Category_1().get();
			Category_1Form c1Form = new Category_1Form();
			c1Form.setCategory_1(category);
			cForm.setCategory_1Form(c1Form);
		}
	}
	*/
	
	//return label given a value from a list of lvbs
	public String getLabel(String value, List<LabelValueBean> aList) throws Exception {
		for (LabelValueBean lvb : aList) {
			if (value.equals(lvb.getValue()))
				return lvb.getLabel();
		}
		return null;
	}
	
	//utility to delete empty sections
	public List<MenuSection> deleteEmptySections(List<MenuSection> theList) {
		List<MenuSection> mList = new ArrayList<MenuSection>();
		int size = theList.size();
		logger.debug("deleteEmptySections:before size = " + size);
		for (int i = 0; i < size; i++) {
			MenuSection mItem = theList.get(i);
			if (mItem.getContents() != null && mItem.getContents().length() > 0)
				mList.add(mItem);
		}
		return mList;
	}

	//delete empty Events - it is empty if
	public List<Event> deleteEmptyEvents(List<Event> theList) {
		List<Event> mList = new ArrayList<Event>();
		int size = theList.size();
		logger.debug("deleteEmptyEvents:before size = " + size);
		for (int i = 0; i < size; i++) {
			Event mItem = theList.get(i);
			if (mItem.getName() != null && mItem.getName().length() > 0)
				mList.add(mItem);
		}
		return mList;
	}
	
	//delete empty offers - should parameterize all of these
	public List<Offer> deleteEmptyOffers(List<Offer> theList) {
		List<Offer> mList = new ArrayList<Offer>();
		for (Offer mItem : theList) {
			if (mItem.getName() != null && mItem.getName().length() > 0)
				mList.add(mItem);
		}
		return mList;
	}	
	
	//common code to write out the offers section
	public StringBuffer createOffersSection(List<Offer> olist) {
		StringBuffer sb = new StringBuffer();
		
		/*
		Calendar cal = Calendar.getInstance();  
		cal.set(Calendar.HOUR_OF_DAY, 0);  
		cal.set(Calendar.MINUTE, 0);  
		cal.set(Calendar.SECOND, 0); 
		Date nowDt = cal.getTime();
		*/
		
		int offerCount = 0;
		sb.append("<offers>");
		for (Offer offer : this.getOfferList()) {
			if (offer == null || offer.getName() == null || offer.getName().length() <= 0)
				continue;
			/*
			Date offerExpires = offer.getExpiration();
			if (offerExpires != null && offerExpires.compareTo(nowDt) < 0) //check if the offer has expired
				continue;
			*/
			if (offer.getStatus() != null && offer.getStatus().equals("E")) //check if the offer has expired
				continue;
			
			offerCount++;
			sb.append("<offer>");
			sb.append("<offer_name><![CDATA[").append(offer.getName()).append("]]></offer_name>");
			sb.append("<offer_description><![CDATA[").append(offer.getDescription()).append("]]></offer_description>");
			if (offer.getExpiration() != null)
				sb.append("<offer_expiration>").append(offer.getExpirationAsString()).append("</offer_expiration>");
			if (offer.getCode() != null)
				sb.append("<offer_code><![CDATA[").append(offer.getCode()).append("]]></offer_code>");
			sb.append("</offer>");
		}
		
		if (offerCount == 0)
			sb.append("<offer><offer_name><![CDATA[").append("No current offers").append("]]></offer_name></offer>");
		sb.append("</offers>");	
		
		return sb;
	}
	
	//common code to write out the events section
	public StringBuffer createEventsSection(String eventIntro, List<Event> evlist) throws Exception {
		boolean noEvents = true;
		StringBuffer sb = new StringBuffer();
		
		sb.append("<events>");
		
		if (eventIntro != null && eventIntro.length() > 0) {
			sb.append("<event_intro><![CDATA[").append(eventIntro).append("]]></event_intro>");
			noEvents = false;
		}
		
		logger.debug("toXML: events size = " + evlist.size());
		for (Event ev : evlist) {
			if (ev.getName() != null && ev.getName().length() > 0) {
				sb.append("<event>");
				sb.append("<name><![CDATA[").append(ev.getName()).append("]]></name>");
				sb.append("<date>").append(ev.getDate()).append("</date>");
				if (ev.getHourStart() != null && ! ev.getHourStart().equals(" ")) {
					sb.append("<hours>").append(ev.getHourStart()).append(" to ")
									.append(ev.getHourEnd()).append("</hours>");
				}
				sb.append("<cost>").append(ev.getCost()).append("</cost>");
				sb.append("<description><![CDATA[").append(ev.getDescription()).append("]]></description>");
				sb.append("</event>");
				noEvents = false;
			}
		}
		sb.append("</events>");
		
		if (noEvents) {
			sb.append("<event_intro><![CDATA[").append("No events at this time").append("]]></event_intro>");
		}
		
		return sb;
	}
	
	//base class method
	/* not used - uses the one below with Campaign
	public void sendMessage(String message, String listId, String keyword, HttpSession session) throws Exception {
		logger.debug("sendMessage of categoryBase");
	    US411Handler smsh = new US411Handler(null, "US");
	    Object[] pNums = new TargetUserListDao().getListData(listId, this.getUserId()).toArray();
	    if (pNums == null || pNums.length == 0)
	    	throw new Exception("No phone numbers found");

	    smsh.sendBulkSMS(message, "US411", "0", pNums, this.getUserId().toString(), null, keyword);
	}
	*/
	
	public void sendMessage(Campaign campaign, String keyword, HttpServletRequest request) throws Exception {
		logger.debug("sendMessage of categoryBase - keyword: " + keyword);
		Object[] pNums = null;
		TargetUserListDao tulDAO = new TargetUserListDao();
		List<String> tmpNums = new ArrayList<String>();
		logger.debug("listId: " + campaign.getListId());
		
		if (campaign.getListId().equals("Numbers")) { //called when user specified one or more numbers
			pNums = campaign.getTargetNumbers();
		} else if (campaign.getListId().equals("Multi")) { //this is a multi list case
			for (TargetUserList tul : campaign.getMultiList()) {
				pNums = tulDAO.getListData(tul.getListId(), this.getUserId()).toArray();
				for (int i = 0; i < pNums.length; i++)
					tmpNums.add((String) pNums[i]);					
			}	
			pNums = tmpNums.toArray();
		} else {		
			pNums = tulDAO.getListData(campaign.getListId(), this.getUserId()).toArray();
		}
		
	    if (pNums == null || pNums.length == 0)
	    	throw new Exception("No phone numbers found");

	    //US411Handler smsh = new US411Handler(null, "US");
	    //smsh.sendBulkSMS(campaign.getMessageText(), "US411", campaign.getCampaignId(), pNums, this.getUserId().toString(), null, keyword);

	    //ApplicationContext  ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
	    ApplicationContext ctx = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_FILE);
	    JMSProducer producer = (JMSProducer)ctx.getBean("producer");
		Queue q = (Queue) ctx.getBean("destination");
		logger.debug("In CategoryBase - destination: " + q.getQueueName());
	    producer.sendMessage(campaign, pNums);
	}

	public void sendMessage(Campaign campaign, String keyword, HttpServletRequest request, List<TargetUserList>tuList, Long userId) throws Exception {
		logger.debug("sendMessage of categoryBase - keyword: " + keyword);
		Map<String, Long> pList = new HashMap<String, Long>();
		
		for (final TargetUserList tul : tuList) {
			if (tul.getListName().equals("All"))
				continue;
			
			List<String> pNums = new TargetUserListDao().getListData(tul.getListId(), tul.getUserId());
			for (String pNum : pNums)
				pList.put(pNum, userId);
		}
		
	    ApplicationContext ctx = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_FILE);
	    JMSProducer producer = (JMSProducer)ctx.getBean("producer");
		Queue q = (Queue) ctx.getBean("destination");
		logger.debug("In CategoryBase - destination: " + q.getQueueName());
	    producer.sendMessage(campaign, pList);
	}
	
	//put all the common xml stuff here
	public StringBuffer toXMLSection1(HttpSession session) throws Exception {
		StringBuffer sb = new StringBuffer();
		
		sb.append("<repeating-logo>").append(this.getRepeatingLogo()).append("</repeating-logo>");
		
		return sb;
	}
	
	//set the status to E (expired) for expired offers
	public List<Offer> setOfferStatus(List<Offer> offers) throws Exception {
		for (Offer offer : offers) {
			if (offer.getName() == null || offer.getName().length() <= 0 || (offer.getStatus() != null && offer.getStatus().equals("E")))
				continue;
			Date expireDate = offer.getExpiration();
			if (expireDate == null)
				continue;
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			cal.set(Calendar.HOUR, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			Date currDate = cal.getTime();
			
			if (currDate.compareTo(expireDate) > 0) 
				offer.setStatus("E");
			else
				offer.setStatus("A"); //active
		}
		
		return offers;
	}
	
	//create the actual text msg to be sent out - default is to do nothing
	public String createMessage(String msg, Campaign campaign) throws Exception {
		return msg;
	}
	
	public String preview(HttpServletRequest request, Campaign campaign) throws Exception {
		throw new Exception("Base class preview - not implemented");
	}
	
	public String preview(HttpServletRequest request) throws Exception {
		throw new Exception("Base class preview - not implemented");
	}

	public String preview(HttpServletRequest request, String currPage) throws Exception {
		throw new Exception("Base class preview (currPage) - not implemented");
	}
	
	public void init(HttpServletRequest request) throws Exception {
		throw new Exception("Base class init - not implemented");
	}
}
